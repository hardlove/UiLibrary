package com.hardlove.library.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.hardlove.library.R;
import com.hardlove.library.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author：CL
 * 日期:2020/10/9
 * 说明：
 **/
public class LuckDisk extends View {
    private static final String TAG = "RotatePan";
    private Context context;

    private int sellSize = 0;

    private Paint dPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float InitAngle = 0;
    private float radius = 0;
    private float verPanRadius;
    private float diffRadius;
    public static final int FLING_VELOCITY_DOWNSCALE = 4;
    private Integer[] images;
    private List<String> cellNames = new ArrayList<>();
    private List<Bitmap> bitmapList = new ArrayList<>();
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

    public LuckDisk(Context context) {
        this(context, null);
    }

    public LuckDisk(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckDisk(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mDetector = new GestureDetectorCompat(context, new RotatePanGestureListener());
        scroller = ScrollerCompat.create(context);

        initAttrs(context, attrs);
        initData();

        //设置可点击
        setClickable(true);


    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LuckDisk);
        sellSize = typedArray.getInteger(R.styleable.LuckDisk_cellSize, 0);

        cellNames.clear();
        bitmapList.clear();

        int namesId = typedArray.getResourceId(R.styleable.LuckDisk_names, 0);
        /*布局中设置了默认值*/
        if (namesId != 0) {
            String[] array = context.getResources().getStringArray(namesId);
            cellNames = Arrays.asList(array);
        }

        int iconResId = typedArray.getResourceId(R.styleable.LuckDisk_icons, 0);
        if (iconResId != 0) {
            int[] array = context.getResources().getIntArray(iconResId);
            for (int iconId : array) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), iconId);
                bitmapList.add(bitmap);
            }
        }

        typedArray.recycle();

    }

    private void initData() {
        InitAngle = 360 * 1.0f / sellSize;
        verPanRadius = 360 * 1.0f / sellSize;
        diffRadius = verPanRadius / 2;
        dPaint.setColor(Color.WHITE);
        sPaint.setColor(Color.YELLOW);
        textPaint.setColor(textColor);
        textPaint.setTextSize(Util.dip2px(context, textSize));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        int minValue = Math.min(getMeasuredWidth(), getMeasuredHeight());
//        minValue -= Util.dip2px(context, 38) * 2;
//        setMeasuredDimension(minValue, minValue);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();


        width = getMeasuredWidth();
        height = width;

        float minValue = Math.min(width, height);

        radius = minValue * 1.0f / 2-60;

        int  left = getPaddingLeft();
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

        float startAngle = (sellSize % 4 == 0) ? InitAngle : InitAngle - diffRadius;
        Log.d(TAG, "onDraw~~~~~~~startAngle:"+ startAngle+"  rectF:"+ rectF.toString());

        for (int i = 0; i < sellSize; i++) {
            if (i % 2 == 0) {
                //偶数
                canvas.drawArc(rectF, startAngle, verPanRadius, true, dPaint);
            } else {
                //奇数
                canvas.drawArc(rectF, startAngle, verPanRadius, true, sPaint);
            }
            startAngle += verPanRadius;
        }

        //绘制图标
        for (int i = 0; i < sellSize; i++) {
            drawIcon(width / 2, height / 2, radius, (sellSize % 4 == 0) ? InitAngle + diffRadius : InitAngle, i, canvas);
            InitAngle += verPanRadius;
        }

        //绘制文字
        for (int i = 0; i < sellSize; i++) {
            drawText((sellSize % 4 == 0) ? InitAngle + diffRadius + (diffRadius * 3 / 4) : InitAngle + diffRadius, cellNames.get(i), 2 * radius, textPaint, canvas, rectF);
            InitAngle += verPanRadius;
        }
    }

    private void drawText(float startAngle, String string, float mRadius, Paint mTextPaint, Canvas mCanvas, RectF mRange) {
        Path path = new Path();

        path.addArc(mRange, startAngle, verPanRadius);
        float textWidth = mTextPaint.measureText(string);

        //圆弧的水平偏移
        float hOffset = (sellSize % 4 == 0) ? (float) ((mRadius * Math.PI / sellSize / 2)) : (float) ((mRadius * Math.PI / sellSize / 2 - textWidth / 2));
        //圆弧的垂直偏移
        float vOffset = mRadius * 1.0f / 2 / 6;

        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    private void drawIcon(float xx, float yy, float mRadius, float startAngle, int i, Canvas mCanvas) {
        Bitmap bitmap = bitmapList.get(i);
        if (bitmap == null) {
            Log.e(TAG, "未指定图标，position:" + i);
            return;
        }
        float imgWidth = mRadius * 1.0f / 4;

        float angle = (float) Math.toRadians(verPanRadius + startAngle);

        //确定图片在圆弧中 中心点的位置
        float x = (float) (xx + (mRadius * 1.0f / 2 + mRadius * 1.0f / 12) * Math.cos(angle));
        float y = (float) (yy + (mRadius * 1.0f / 2 + mRadius * 1.0f / 12) * Math.sin(angle));

        // 确定绘制图片的位置
        RectF rect = new RectF(x - imgWidth * 2.0f / 3, y - imgWidth * 2.0f / 3, x + imgWidth * 2.0f / 3, y + imgWidth * 2.0f / 3);


        mCanvas.drawBitmap(bitmap, null, rect, null);
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
                angle = (pos - initPos) * verPanRadius;
                lap -= 1;
                angle = 360 - angle;
            } else if (pos < initPos) {
                angle = (initPos - pos) * verPanRadius;
            } else {
                //nothing to do.
            }
        }

        //All of the rotate angle.
        float increaseDegree = lap * 360 + angle;
        double time = (lap + angle * 1.0f / 360) * ONE_WHEEL_TIME;
        float DesRotate = increaseDegree + InitAngle;

        //TODO 为了每次都能旋转到转盘的中间位置
        float offRotate = DesRotate % 360 % verPanRadius;
        DesRotate -= offRotate;
        DesRotate += diffRadius;

        ValueAnimator animtor = ValueAnimator.ofFloat(InitAngle, DesRotate);
        animtor.setInterpolator(new AccelerateDecelerateInterpolator());
        animtor.setDuration((long) time);
        animtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float updateValue = (float) animation.getAnimatedValue();
                InitAngle = (updateValue % 360 + 360) % 360;
                ViewCompat.postInvalidateOnAnimation(LuckDisk.this);
            }
        });

        animtor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (((LuckDiskLayout) getParent()).getAnimationEndListener() != null) {
                    ((LuckDiskLayout) getParent()).setStartBtnEnable(true);
                    ((LuckDiskLayout) getParent()).setDelayTime(LuckDiskLayout.DEFAULT_TIME_PERIOD);
                    ((LuckDiskLayout) getParent()).getAnimationEndListener().endAnimation(queryPosition());
                }
            }
        });
        animtor.start();
    }


    private int queryPosition() {
        InitAngle = (InitAngle % 360 + 360) % 360;
        int pos = (int) (InitAngle / verPanRadius);
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
        InitAngle = rotation;
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
            float centerX = (LuckDisk.this.getLeft() + LuckDisk.this.getRight()) * 0.5f;
            float centerY = (LuckDisk.this.getTop() + LuckDisk.this.getBottom()) * 0.5f;

            float scrollTheta = vectorToScalarScroll(distanceX, distanceY, e2.getX() - centerX, e2.getY() -
                    centerY);
            int rotate = (int) (InitAngle - scrollTheta / FLING_VELOCITY_DOWNSCALE);

            setRotate(rotate);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float centerX = (LuckDisk.this.getLeft() + LuckDisk.this.getRight()) * 0.5f;
            float centerY = (LuckDisk.this.getTop() + LuckDisk.this.getBottom()) * 0.5f;

            float scrollTheta = vectorToScalarScroll(velocityX, velocityY, e2.getX() - centerX, e2.getY() -
                    centerY);

            scroller.abortAnimation();
            scroller.fling(0, (int) InitAngle, 0, (int) scrollTheta / FLING_VELOCITY_DOWNSCALE,
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
