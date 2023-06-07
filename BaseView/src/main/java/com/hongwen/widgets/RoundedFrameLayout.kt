package com.hongwen.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * ==================================================
 * Author：CL
 * 日期:2023/6/7
 * 说明：实现圆角裁减
 * ==================================================
 **/
class RoundedFrameLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val path: Path = Path()
    private val rectF: RectF = RectF()

    private var topLeftRadius: Float = 0f
    private var topRightRadius: Float = 0f
    private var bottomLeftRadius: Float = 0f
    private var bottomRightRadius: Float = 0f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundedFrameLayout)
        val radius = a.getDimension(R.styleable.RoundedFrameLayout_radius, 0f)
        topLeftRadius = a.getDimension(R.styleable.RoundedFrameLayout_topLeftRadius, radius)
        topRightRadius = a.getDimension(R.styleable.RoundedFrameLayout_topRightRadius, radius)
        bottomLeftRadius = a.getDimension(R.styleable.RoundedFrameLayout_bottomLeftRadius, radius)
        bottomRightRadius = a.getDimension(R.styleable.RoundedFrameLayout_bottomRightRadius, radius)
        a.recycle()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.save()

        rectF.set(0f, 0f, width.toFloat(), height.toFloat())
        path.reset()
        path.addRoundRect(rectF, floatArrayOf(
            topLeftRadius, topLeftRadius,
            topRightRadius, topRightRadius,
            bottomRightRadius, bottomRightRadius,
            bottomLeftRadius, bottomLeftRadius
        ), Path.Direction.CW)

        canvas?.clipPath(path)
        super.dispatchDraw(canvas)

        canvas?.restore()
    }
}