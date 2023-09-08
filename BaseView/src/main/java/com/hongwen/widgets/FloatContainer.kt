package com.hongwen.widgets

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * 悬浮窗容器构建器
 */
class FloatContainerBuilder(val context: Context) {
    private var contentViewID = 0
    private var onVisibleListener: FloatContainer.OnVisibleListener? = null
    private var onInitialize: FloatContainer.OnInitialize? = null
    private var permission: FloatContainer.PermissionLister? = null
    fun setContentViewID(contentViewID: Int): FloatContainerBuilder {
        this.contentViewID = contentViewID
        return this
    }

    fun onVisibleListener(onVisibleListener: FloatContainer.OnVisibleListener): FloatContainerBuilder {
        this.onVisibleListener = onVisibleListener
        return this
    }

    fun setOnInitialize(onInitialize: FloatContainer.OnInitialize): FloatContainerBuilder {
        this.onInitialize = onInitialize
        return this

    }

    fun setPermissionLister(permission: FloatContainer.PermissionLister): FloatContainerBuilder {
        this.permission = permission
        return this

    }

    fun build(): FloatContainer {
        val floatContainer = FloatContainer(context)
        assert(contentViewID != 0) {
            "请指定contentViewID"
        }
        val child = LayoutInflater.from(context).inflate(contentViewID, floatContainer, false)
        floatContainer.addView(
            child,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        floatContainer.requestLayout()
        onInitialize?.onInit(child)
        onVisibleListener?.let {
            floatContainer.setOnVisibleListener(it)
        }
        permission?.let {
            floatContainer.setPermissionLister(it)
        }
        return floatContainer
    }


}


class FloatContainer(context: Context) : FrameLayout(context), LifecycleOwner {
    private var windowManager: WindowManager
    private val mRegistry = LifecycleRegistry(this)
    private lateinit var layoutParams: WindowManager.LayoutParams
    private var isShowing = false
    private var startX = 0.0f
    private var startY = 0.0f
    private val TAG = "FloatContainer"

    companion object {
        /**
         * 开起悬浮窗权限
         */
        @TargetApi(Build.VERSION_CODES.M)
        fun requestPermission(activity: Activity) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + activity.packageName)
            activity.startActivityForResult(intent, 1001)
        }

    }

    init {
        mRegistry.currentState = Lifecycle.State.CREATED
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mRegistry.currentState = Lifecycle.State.RESUMED


    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        } else if (visibility == GONE || visibility == INVISIBLE) {
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun getLifecycle(): Lifecycle {
        return mRegistry
    }


    fun show() {
        if (!isShowing) {
            if (hasPermission()) {
                layoutParams = WindowManager.LayoutParams()
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                layoutParams.format = PixelFormat.TRANSPARENT
                layoutParams.flags =
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
                }

                layoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE

                layoutParams.x = 0
                layoutParams.y = 0
                layoutParams.gravity = Gravity.START or Gravity.TOP

                windowManager.addView(this, layoutParams)
                isShowing = true
                listener?.onStart()
            } else {
                permission?.noPermission()
            }
        }
    }

    fun dismiss() {
        if (isShowing) {
            windowManager.removeView(this)
            isShowing = false
        }
        listener?.onStop()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isShowing) {
            return super.onTouchEvent(event)
        } else {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    startY = event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    layoutParams.x = (layoutParams.x + event.rawX - startX).toInt()
                    layoutParams.y = (layoutParams.y + event.rawY - startY).toInt()
                    windowManager.updateViewLayout(this, layoutParams)
                    startX = event.rawX
                    startY = event.rawY
                    Log.d(
                        TAG,
                        " layoutParams.x:" + layoutParams.x + "   layoutParams.y:" + layoutParams.y + " startX:" + startX + "  startY:" + startY
                    )
                }

                MotionEvent.ACTION_UP -> {

                }

            }
            return false
        }

    }

    interface OnVisibleListener {
        fun onStart()
        fun onStop()
    }

    interface PermissionLister {
        fun noPermission()

    }

    interface OnInitialize {
        fun onInit(content: View)
    }


    private var listener: OnVisibleListener? = null
    private var permission: PermissionLister? = null
    fun setOnVisibleListener(visibleListener: OnVisibleListener) {
        this.listener = visibleListener
    }

    fun setPermissionLister(permission: PermissionLister) {
        this.permission = permission
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }

    }

    fun isShowing(): Boolean {
        return isShowing
    }


}