package com.carlos.widgets.progress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

/**
 * ==================================================
 * Author：CL
 * 日期:2023/9/13
 * 说明：圆形进度条 起始点可设置图标
 * ==================================================
 **/
public class CircularProgressBar extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint iconPaint;
    private float progress;
    private Bitmap icon;
    private float centerX;
    private float centerY;
    private float radius;

    private float ringWidth = 10;
    private RectF rectF;

    public CircularProgressBar(Context context) {
        super(context);
        init();
    }


    public CircularProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(20);

        progressPaint = new Paint();
        progressPaint.setColor(Color.parseColor("#FFFF6E57"));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20);

        iconPaint = new Paint();
        // 初始化 icon

        progress = 0;
        rectF = new RectF();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        centerX = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;
        centerY = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
        radius = Math.min(centerX, centerY) - Math.max(Math.max(ringWidth, Math.max(icon.getWidth() / 2, icon.getHeight() / 2)), 2 * ringWidth);
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制背景圆环
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);
        // 绘制进度圆弧
        float startAngle = -90;//以正上方为起点
        float sweepAngle = 360 * progress / 100;
        canvas.drawArc(rectF, startAngle, sweepAngle, false, progressPaint);
        // 计算图标位置和旋转角度
        if (icon != null) {
            canvas.save();
            canvas.translate(centerX, centerY);
//            canvas.drawCircle(0,0,10,progressPaint);
//            canvas.drawLine(0,0,0,-radius,progressPaint);
            canvas.rotate(sweepAngle);

            canvas.drawBitmap(icon, -icon.getWidth() / 2, -radius - icon.getHeight() / 2, null);
            canvas.restore();
        }

    }

    public void setProgress(@FloatRange(from = 0, to = 100) float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
        invalidate();
    }
}
