package com.carlos.widgets.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.carlos.widgets.R;

/**
 * ==================================================
 * <p>Author：CL
 * <p>日期: 2022/12/27
 * <p>说明：仪表盘View
 * <p>
 * ==================================================
 **/
public class DashBoardProgressBar extends View {
    private final Paint mPaint = new Paint();
    private final RectF mRectF = new RectF();
    private int centerX = 0;
    private int centerY = 0;
    private int ringSize = 5;
    private int mWith;
    private int mHeight;
    private int backgroundColor;
    private int progressColor;


    private int max = 100;
    private int progress;

    public DashBoardProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DashBoardProgressBar);
        ringSize = array.getDimensionPixelSize(R.styleable.DashBoardProgressBar_ringSize, (int) (context.getResources().getDisplayMetrics().density * 10));
        max = array.getInteger(R.styleable.DashBoardProgressBar_max, 100);
        backgroundColor = array.getColor(R.styleable.DashBoardProgressBar_backgroundColor, Color.parseColor("#EEEEEE"));
        progressColor = array.getColor(R.styleable.DashBoardProgressBar_progressColor, ContextCompat.getColor(context, R.color.colorAccent));
        progress = array.getInteger(R.styleable.DashBoardProgressBar_progress, 0);
        array.recycle();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringSize);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (progress > max) {
            progress = max;
        }
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWith = getWidth();
        mHeight = getHeight();
        centerX = mWith / 2;
        centerY = mHeight / 2;
        canvas.rotate(45 + 90, centerX, centerY);
        drawBackgroundCircle(canvas);
        drawProgressCircle(canvas);

    }

    private void drawProgressCircle(Canvas canvas) {
        mPaint.setColor(progressColor);
        mRectF.set(centerX - mWith * 1.0f / 2 + ringSize, centerY - mHeight * 1.0f / 2 + ringSize, centerX + mWith * 1.0f / 2 - ringSize, centerY + mHeight * 1.0f / 2 - ringSize);
        canvas.drawArc(mRectF, 0, 270 * (progress * 1.0f / max), false, mPaint);
    }

    private void drawBackgroundCircle(Canvas canvas) {
        mPaint.setColor(backgroundColor);
        mRectF.set(centerX - mWith * 1.0f / 2 + ringSize, centerY - mHeight * 1.0f / 2 + ringSize, centerX + mWith * 1.0f / 2 - ringSize, centerY + mHeight * 1.0f / 2 - ringSize);
        canvas.drawArc(mRectF, 0, 270, false, mPaint);
    }
}
