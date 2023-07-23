package com.hongwen.location

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.hongwen.location.callback.OnPickerListener
import com.hongwen.location.loader.ChinaCityDataLoader
import com.hongwen.location.loader.StationDataLoader
import com.hongwen.location.model.IModel
import com.hongwen.location.model.LocationType
import com.hongwen.location.ui.LocationSelectDialogFragment
import java.security.InvalidParameterException

/**
 * Created by chenlu at 2023/7/22 16:11
 */
class LocationPicker private constructor() {
    private lateinit var iModelLoader: OnPickerListener.IModelLoader<IModel>
    private var mCancelable: Boolean = true
    private var autoLocate: Boolean = false
    private lateinit var fragmentManager: FragmentManager
    private lateinit var locationType: LocationType
    private lateinit var TAG: String

    private var onCancelListener: OnPickerListener.OnCancelListener? = null
    private var onDismissListener: OnPickerListener.OnDismissListener? = null
    private var onShowListener: OnPickerListener.OnShowListener? = null
    private var onLocateListener: OnPickerListener.OnLocateListener? = null
    private var onItemClickListener: OnPickerListener.OnItemClickListener<IModel>? = null

    private constructor(fragmentManager: FragmentManager) : this() {
        this.fragmentManager = fragmentManager
    }

    companion object {
        @JvmStatic
        fun from(activity: FragmentActivity): LocationPicker {
            return LocationPicker(activity.supportFragmentManager)
        }

        @JvmStatic
        fun from(fragment: Fragment): LocationPicker {
            return LocationPicker(fragment.childFragmentManager)
        }
    }

    /**
     * 设置数据来源类型
     * @param locationType 城市数据[LocationType.ChinaCity] 火车站点[LocationType.TrainStation] 自定义数据[LocationType.CustomLocation]
     *
     */
    fun setLocationType(locationType: LocationType = LocationType.ChinaCity): LocationPicker {
        this.locationType = locationType
        this.TAG = "fragment_location_tag:$locationType"
        return this
    }

    /**
     * 设置数据加载器
     */
    fun <T : IModel> setIModelLoader(loader: OnPickerListener.IModelLoader<T>): LocationPicker {
        this.iModelLoader = loader
        return this
    }

    /**
     * 是否自动回调定位
     */
    fun isAutoLocate(autoLocate: Boolean = false): LocationPicker {
        this.autoLocate = autoLocate
        return this
    }

    /**
     * 点击返回按键是否可以取消
     */
    fun setCancelable(cancelable: Boolean = true): LocationPicker {
        this.mCancelable = cancelable
        return this
    }

    fun setOnCancelListener(onCancelListener: OnPickerListener.OnCancelListener): LocationPicker {
        this.onCancelListener = onCancelListener
        return this
    }

    fun setOnDismissListener(onDismissListener: OnPickerListener.OnDismissListener): LocationPicker {
        this.onDismissListener = onDismissListener
        return this
    }

    fun setOnShowListener(onShowListener: OnPickerListener.OnShowListener): LocationPicker {
        this.onShowListener = onShowListener
        return this
    }

    fun setOnItemClickListener(onItemClickListener: OnPickerListener.OnItemClickListener<IModel>): LocationPicker {
        this.onItemClickListener = onItemClickListener
        return this
    }

    /**
     * 显示
     */
    fun show(context:Context) {
        if (!::iModelLoader.isInitialized) {
            if (locationType == LocationType.ChinaCity) {
                iModelLoader = ChinaCityDataLoader(context.applicationContext)
            } else if (locationType == LocationType.TrainStation) {
                iModelLoader = StationDataLoader(context.applicationContext)
            } else {
                throw InvalidParameterException("iModelLoader 未初始化,请调用函数setIModelLoader(loader: OnPickerListener.IModelLoader<T>)进行初始化")
            }
        }
        val dialogFragment = LocationSelectDialogFragment()
        dialogFragment.apply {
            this.isCancelable = this@LocationPicker.mCancelable
            this.setAutoLocate(this@LocationPicker.autoLocate)
            this.setOnCancelListener(this@LocationPicker.onCancelListener)
            this.setOnDismissListener(this@LocationPicker.onDismissListener)
            this.setOnShowListener(this@LocationPicker.onShowListener)
            this.setOnLocateListener(this@LocationPicker.onLocateListener)
            this.setIModelLoader(this@LocationPicker.iModelLoader)
        }
        dialogFragment.show(fragmentManager, TAG)
    }

    fun dismiss() {
        val fragment = fragmentManager.findFragmentByTag(TAG) as? LocationSelectDialogFragment
        fragment?.dismiss()
    }


}