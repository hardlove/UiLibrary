package com.hardlove.library.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ScrollerCompat;

import com.hardlove.library.bean.Sector;
import com.hardlove.library.utils.Util;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author：CL
 * 日期:2020/10/9
 * 说明：
 **/
public class LuckDiskView extends View {
    private static final String TAG = "RotatePan";
    private Context context;

    private int sellSize = 0;

    private Paint dPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float initAngle = 0;
    private float radius = 0;
    private float verCellRadius;
    private float diffRadius;
    public static final int FLING_VELOCITY_DOWNSCALE = 4;
    List<Sector> list = new ArrayList<>();
    private GestureDetectorCompat mDetector;
    private ScrollerCompat scroller;

    //旋转一圈所需要的时间
    private static final long ONE_WHEEL_TIME = 500;
    /*文字颜色*/
    private int textColor = Color.WHITE;
    /*文字大小 单位：sp*/
    private float textSize = 16;
    private RectF rectF;
    private float width;
    private float height;
    private ValueAnimator valueAnimator;

    public LuckDiskView(Context context) {
        this(context, null);
    }

    public LuckDiskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckDiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mDetector = new GestureDetectorCompat(context, new RotatePanGestureListener());
        scroller = ScrollerCompat.create(context);

        //设置可点击
        setClickable(true);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();


        width = height = getMeasuredWidth();
        float minValue = Math.min(width, height);

        radius = minValue * 1.0f / 2 - 60;

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = (int) (left + width);
        int bottom = (int) (top + height);
        rectF = new RectF(left, top, right, bottom);
        Log.d(TAG, "measureWidth:" + getMeasuredWidth() + " measureHeight:" + getMeasuredHeight() + "  paddingTop:" + getPaddingTop() + " paddingLeft:" + getPaddingLeft() + " width:" + getWidth() + " height:" + getHeight());


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width / 2, height / 2);

        rectF.set(-radius, -radius, radius, radius);

        float startAngle = (sellSize % 4 == 0) ? initAngle : initAngle - diffRadius;
        Log.d(TAG, "onDraw~~~~~~~startAngle:" + startAngle + "  rectF:" + rectF.toString());

        //绘制扇形
        for (int i = 0; i < sellSize; i++) {
            dPaint.setColor(list.get(i).getBgColor());
            canvas.drawArc(rectF, startAngle, verCellRadius, true, dPaint);
            startAngle += verCellRadius;
        }

        //绘制图标
        for (int i = 0; i < sellSize; i++) {
            drawIcon(0, 0, radius, (sellSize % 4 == 0) ? initAngle + diffRadius : initAngle, list.get(i).getBitmap(), canvas);
            initAngle += verCellRadius;
        }

        //绘制文字
        for (int i = 0; i < sellSize; i++) {
            textPaint.setColor(list.get(i).getTextColor());
            textPaint.setTextSize(Util.sp2px(context, list.get(i).getTextSize() == 0 ? textSize : list.get(i).getTextSize()));

            drawText((sellSize % 4 == 0) ? initAngle + diffRadius + (diffRadius * 3 / 4) : initAngle + diffRadius, list.get(i).getName(), 2 * radius, textPaint, canvas, rectF);
            initAngle += verCellRadius;
        }
    }

    private void drawText(float startAngle, String text, float mRadius, Paint mTextPaint, Canvas mCanvas, RectF mRange) {
        Path path = new Path();

        path.addArc(mRange, startAngle, verCellRadius);
        float textWidth = mTextPaint.measureText(text);

        //圆弧的水平偏移
        float hOffset = (sellSize % 4 == 0) ? (float) ((mRadius * Math.PI / sellSize / 2)) : (float) ((mRadius * Math.PI / sellSize / 2 - textWidth / 2));
        //圆弧的垂直偏移
        float vOffset = mRadius * 1.0f / 2 / 6;

        mCanvas.drawTextOnPath(text, path, hOffset, vOffset, mTextPaint);

    }

    private void drawIcon(float cx, float cy, float mRadius, float startAngle, Bitmap bitmap, Canvas mCanvas) {
        if (bitmap == null) {
            Log.e(TAG, "未指定图标");
            return;
        }
        float imgWidth = mRadius * 1.0f / 4;

        //将度转为弧度
        float angle = (float) Math.toRadians(verCellRadius + startAngle);

        //确定图片在圆弧中 中心点的位置
        float x = (float) (cx + (mRadius * 1.0f / 2 + mRadius * 1.0f / 10) * Math.cos(angle));
        float y = (float) (cy + (mRadius * 1.0f / 2 + mRadius * 1.0f / 10) * Math.sin(angle));

        // 确定绘制图片的位置
        RectF rect = new RectF(x - imgWidth * 2.0f / 3, y - imgWidth * 2.0f / 3, x + imgWidth * 2.0f / 3, y + imgWidth * 2.0f / 3);

        mCanvas.drawBitmap(bitmap, null, rect, null);
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(List<Sector> data) {
        list.clear();

        if (data != null && data.size() > 0) {
            list.addAll(data);
        }
        sellSize = list.size();

        if (sellSize == 0) {
            throw new InvalidParameterException("数据不能位空");
        }

        initAngle = 360 * 1.0f / sellSize;
        verCellRadius = 360 * 1.0f / sellSize;
        diffRadius = verCellRadius / 2;

    }

    /**
     * 开始转动
     *
     * @param pos 如果 pos = -1 则随机，如果指定某个值，则转到某个指定区域
     */
    protected void startRotate(int pos) {

        //Rotate lap.
        int lap = (int) (Math.random() * 12) + 4;

        //Rotate angle.
        float angle = 0;
        if (pos < 0) {
            angle = (float) (Math.random() * 360);
        } else {
            int initPos = queryPosition();
            if (pos > initPos) {
                angle = (pos - initPos) * verCellRadius;
                lap -= 1;
                angle = 360 - angle;
            } else if (pos < initPos) {
                angle = (initPos - pos) * verCellRadius;
            } else {
                //nothing to do.
            }
        }

        //All of the rotate angle.
        float increaseDegree = lap * 360 + angle;
        double time = (lap + angle * 1.0f / 360) * ONE_WHEEL_TIME;
        float desRotate = increaseDegree + initAngle;

        //TODO 为了每次都能旋转到转盘的中间位置
        float offRotate = desRotate % 360 % verCellRadius;
        desRotate -= offRotate;
        desRotate += diffRadius;

        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        valueAnimator = initValueAnimator((long) time, desRotate);
        valueAnimator.start();


    }

    private ValueAnimator initValueAnimator(long time, float desRotate) {
        valueAnimator = ValueAnimator.ofFloat(initAngle, desRotate);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(time);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float updateValue = (float) animation.getAnimatedValue();
                initAngle = (updateValue % 360 + 360) % 360;
                ViewCompat.postInvalidateOnAnimation(LuckDiskView.this);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onResultListener != null) {
                    onResultListener.onSelectedResult(list.get(queryPosition()));
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

        });
        return valueAnimator;
    }


    /**
     * 查询当前选择结果
     *
     * @return
     */
    private int queryPosition() {
        initAngle = (initAngle % 360 + 360) % 360;
        int pos = (int) (initAngle / verCellRadius);
        if (sellSize == 4) pos++;
        return calculateAngle(pos);
    }

    private int calculateAngle(int pos) {
        if (pos >= 0 && pos <= sellSize / 2) {
            pos = sellSize / 2 - pos;
        } else {
            pos = (sellSize - pos) + sellSize / 2;
        }
        return pos;
    }


    @Override
    protected void onDetachedFromWindow() {
        clearAnimation();
        if (getParent() instanceof LuckDiskLayout) {
            ((LuckDiskLayout) getParent()).getHandler().removeCallbacksAndMessages(null);
        }
        super.onDetachedFromWindow();
    }

    /**
     * 选中结果回调
     */
    public interface OnResultListener {
        void onSelectedResult(Sector sector);
    }

    private OnResultListener onResultListener;

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }
    // TODO ==================================== 手势处理 ===============================================================

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean consume = mDetector.onTouchEvent(event);
        if (consume) {
            getParent().getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }

        return super.onTouchEvent(event);
    }


    public void setRotate(int rotation) {
        rotation = (rotation % 360 + 360) % 360;
        initAngle = rotation;
        ViewCompat.postInvalidateOnAnimation(this);
    }


    @Override
    public void computeScroll() {

        if (scroller.computeScrollOffset()) {
            setRotate(scroller.getCurrY());
        }

        super.computeScroll();
    }

    private class RotatePanGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float centerX = (LuckDiskView.this.getLeft() + LuckDiskView.this.getRight()) * 0.5f;
            float centerY = (LuckDiskView.this.getTop() + LuckDiskView.this.getBottom()) * 0.5f;

            float scrollTheta = vectorToScalarScroll(distanceX, distanceY, e2.getX() - centerX, e2.getY() -
                    centerY);
            int rotate = (int) (initAngle - scrollTheta / FLING_VELOCITY_DOWNSCALE);

            setRotate(rotate);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float centerX = (LuckDiskView.this.getLeft() + LuckDiskView.this.getRight()) * 0.5f;
            float centerY = (LuckDiskView.this.getTop() + LuckDiskView.this.getBottom()) * 0.5f;

            float scrollTheta = vectorToScalarScroll(velocityX, velocityY, e2.getX() - centerX, e2.getY() -
                    centerY);

            scroller.abortAnimation();
            scroller.fling(0, (int) initAngle, 0, (int) scrollTheta / FLING_VELOCITY_DOWNSCALE,
                    0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            return true;
        }
    }

    //TODO 判断滑动的方向
    private float vectorToScalarScroll(float dx, float dy, float x, float y) {

        float l = (float) Math.sqrt(dx * dx + dy * dy);

        float crossX = -y;
        float crossY = x;

        float dot = (crossX * dx + crossY * dy);
        float sign = Math.signum(dot);

        return l * sign;
    }


}
