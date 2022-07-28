package com.hardlove.library.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ScrollerCompat;

import com.hardlove.library.R;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author：CL
 * 日期:2020/10/9
 * 说明：幸运随机转盘
 **/
public class LuckDiskView extends View {
    private static final String TAG = "LuckDiskView";
    private Context context;

    private int sellSize = 0;

    private Paint dPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint outCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint flashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int flashActiveColor = Color.YELLOW;
    private int flashInActiveColor = Color.WHITE;

    private float initAngle = 0;
    /*内环半径*/
    private float innerCircleRadius = 0;
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
    private float textSize = 12;
    private RectF rectF = new RectF();
    private float width;
    private float height;
    private ValueAnimator valueAnimator;
    /*外圆环宽度*/
    private int outCircleSize = 20;
    @ColorInt
    private int outCircleColor = Color.rgb(255, 92, 93);
    private boolean addFlash;
    private boolean firstActive;
    private boolean enableRotate;


    public LuckDiskView(Context context) {
        this(context, null);
    }

    public LuckDiskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckDiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        initAttrs(context, attrs);
        mDetector = new GestureDetectorCompat(context, new RotatePanGestureListener());
        scroller = ScrollerCompat.create(context);

        outCirclePaint.setColor(Color.YELLOW);
        outCirclePaint.setStrokeWidth(outCircleSize);


        if (addFlash) {
            //开启闪光
            startFlash();
        }

        //设置可点击
        setClickable(enableRotate);


    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LuckDiskView);
        outCircleColor = array.getColor(R.styleable.LuckDiskView_outCircleColor, outCircleColor);
        outCircleSize = array.getDimensionPixelSize(R.styleable.LuckDiskView_outCircleSize, dip2px(context, outCircleSize));
        textSize = array.getDimensionPixelSize(R.styleable.LuckDiskView_textSize, sp2px(context, textSize));
        flashActiveColor = array.getColor(R.styleable.LuckDiskView_flash_active_color, flashActiveColor);
        flashInActiveColor = array.getColor(R.styleable.LuckDiskView_flash_inactive_color, flashInActiveColor);

        addFlash = array.getBoolean(R.styleable.LuckDiskView_addFlash, true);
        enableRotate = array.getBoolean(R.styleable.LuckDiskView_enable_rotate, true);
        array.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        width = height = getMeasuredWidth();
        float minValue = Math.min(width, height);

        if (outCircleSize >= width / 2) {
            throw new InvalidParameterException("外环过大");
        }

        innerCircleRadius = minValue * 1.0f / 2 - outCircleSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width / 2, height / 2);
        canvas.rotate(-90);

        //绘制外环
        drawOutCircle(canvas);

        //绘制外环闪光灯
        drawFlash(canvas);

        rectF.set(-innerCircleRadius, -innerCircleRadius, innerCircleRadius, innerCircleRadius);
        float startAngle = initAngle;
        Log.d(TAG, "onDraw~~~~~~~startAngle:" + startAngle + "  rectF:" + rectF.toString());

        //绘制扇形
        for (int i = 0; i < sellSize; i++) {
            dPaint.setColor(list.get(i).getBgColor());
            canvas.drawArc(rectF, startAngle, verCellRadius, true, dPaint);
            startAngle += verCellRadius;
        }

        startAngle = initAngle + diffRadius;
        //绘制图标
        for (int i = 0; i < sellSize; i++) {
            drawIcon(startAngle, list.get(i).getBitmap(), canvas);
            startAngle += verCellRadius;
        }

        startAngle = initAngle + diffRadius;
        //绘制文字
        for (int i = 0; i < sellSize; i++) {
            textPaint.setColor(list.get(i).getTextColor());
            float textSize = list.get(i).getTextSize() == 0 ? this.textSize : list.get(i).getTextSize();
            textPaint.setTextSize(textSize);

            drawText(startAngle, list.get(i).getName(), textPaint, canvas);
            startAngle += verCellRadius;
        }


    }

    private void drawText(float startAngle, String name, Paint textPaint, Canvas canvas) {
        char[] chars = name.toCharArray();
        int count = chars.length;
        //最大绘制个字符
        int max = 8;
        if (count > max) {
            count = max;
        }
        Path path = new Path();

        float dx = (float) (Math.cos(Math.toRadians(startAngle)) * innerCircleRadius);
        float dy = (float) (Math.sin(Math.toRadians(startAngle)) * innerCircleRadius);
        path.moveTo(dx, dy);
        path.lineTo(dx / 16, dy / 16);

        float hOffset = (innerCircleRadius - textPaint.measureText(name, 0, count)) / 2;

        hOffset = dip2px(context, 6);

        float vOffset = (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top) / 4;
        canvas.drawTextOnPath(chars, 0, count, path, hOffset, vOffset, textPaint);

        //参考线
//        canvas.drawLine(0, 0, dx, dy, textPaint);

    }


    /**
     * 开启闪光灯
     */
    private void startFlash() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                firstActive = !firstActive;
                invalidate();
                //进入循环
                startFlash();
            }
        }, 1000);
    }

    private void drawFlash(Canvas canvas) {
        if (addFlash) {
            int CircleX = 0;
            int CircleY = 0;
            float pointDistance = innerCircleRadius + outCircleSize * 1.0f / 2;
            int pos = 0;
            for (int i = 0; i <= 360; i += 20) {
                int x = (int) (pointDistance * Math.sin(change(i))) + CircleX;
                int y = (int) (pointDistance * Math.cos(change(i))) + CircleY;

                if (firstActive) {
                    if (pos++ % 2 == 0) {
                        flashPaint.setColor(flashActiveColor);
                    } else {
                        flashPaint.setColor(flashInActiveColor);
                    }
                } else {
                    if (pos++ % 2 == 1) {
                        flashPaint.setColor(flashActiveColor);
                    } else {
                        flashPaint.setColor(flashInActiveColor);
                    }
                }
                canvas.drawCircle(x, y, outCircleSize * 1.0f / 2 / 2, flashPaint);

            }
        }
    }

    private void drawOutCircle(Canvas canvas) {
        if (outCircleSize > 0) {
            outCirclePaint.setColor(outCircleColor);
            canvas.drawCircle(0, 0, width / 2, outCirclePaint);
            outCirclePaint.setColor(Color.WHITE);
            canvas.drawCircle(0, 0, width / 2 - outCircleSize, outCirclePaint);
        }
    }


    private void drawIcon(float startAngle, Bitmap bitmap, Canvas mCanvas) {
        if (bitmap == null) {
            Log.e(TAG, "未指定图标");
            return;
        }
        float imgWidth = innerCircleRadius / 4;

        //将度转为弧度
        float angle = (float) Math.toRadians(verCellRadius + startAngle);

        //确定图片在圆弧中 中心点的位置
        float dx = (float) (Math.cos(angle) * innerCircleRadius) / 2;
        float dy = (float) (Math.sin(angle) * innerCircleRadius) / 2;

        // 确定绘制图片的位置
        RectF rect = new RectF(-imgWidth / 2, -imgWidth / 2, imgWidth / 2, imgWidth / 2);


        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postTranslate(dx, dy);
        matrix.mapRect(rect);

//        flashPaint.setColor(Color.RED);
//        mCanvas.drawRect(rect, flashPaint);
//        mCanvas.drawCircle(dx, dy, 10, flashPaint);

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

        initAngle = 0;
        verCellRadius = 360 * 1.0f / sellSize;
        diffRadius = verCellRadius / 2;

        invalidate();
    }

    /**
     * 开始转动
     *
     * @param pos 如果 pos = -1 则随机，如果指定某个值，则转到某个指定区域
     */
    public void startRotate(int pos) {
        //Rotate lap.
        int lap = (int) (Math.random() * 2) + 4;
        long time = lap * 1000;
        //Rotate desRotate.
        //角度为负，顺时针旋转
        float desRotate;
        if (pos < 0) {
            desRotate = -(float) (Math.random() * 360) - 360 * lap;
        } else {
            desRotate = -getAngleByPosition(pos) - 360 * lap;
        }

        initAngle = initAngle % 360;

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
                initAngle = (updateValue);
                ViewCompat.postInvalidateOnAnimation(LuckDiskView.this);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            boolean cancel;

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onResultListener != null && !cancel) {
                    onResultListener.onSelectedResult(list.get(queryPosition()));
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                this.cancel = true;
            }

        });
        return valueAnimator;
    }


    private int getAngleByPosition(int pos) {
        int angle = (int) ((verCellRadius * pos));
        return angle;
    }


    /**
     * 查询当前选择结果
     *
     * @return
     */
    private int queryPosition() {
        int pos = (int) (Math.abs(initAngle % 360) / verCellRadius);
        if (pos >= sellSize) {
            pos = sellSize - 1;
        }
        return pos;
    }


    @Override
    protected void onDetachedFromWindow() {
        clearAnimation();
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


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static double change(double a) {
        return a * Math.PI / 180;
    }

    public static double changeAngle(double a) {
        return a * 180 / Math.PI;
    }
    // TODO ==================================== 手势处理 ===============================================================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enableRotate) {
            return super.onTouchEvent(event);
        }
        boolean consume = mDetector.onTouchEvent(event);
        if (consume) {
            //请求父控件不要拦截
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


    /**
     * Author：CL
     * 日期:2020/10/10
     * 说明：转盘item实列
     **/
    public static class Sector implements Serializable {
        private String name;
        /**
         * 绘制的背景颜色
         */
        @ColorInt
        private int bgColor;

        /**
         * 绘制的文字颜色
         */
        @ColorInt
        private int textColor;
        /**
         * 文字的大小，单位sp
         * 若未指定，这使用默认字体大小
         */
        private int textSize;
        /**
         * 对应显示的图片
         */
        public transient Bitmap bitmap;
        /**
         * 用于数据扩展的字段
         */
        private transient Object object;

        public Sector() {
        }

        public Sector(String name, @ColorInt int bgColor, @ColorInt int textColor) {
            this.name = name;
            this.bgColor = bgColor;
            this.textColor = textColor;
        }

        public Sector(String name, @ColorInt int bgColor, @ColorInt int textColor, Bitmap bitmap) {
            this.name = name;
            this.bgColor = bgColor;
            this.textColor = textColor;
            this.bitmap = bitmap;
        }

        public String getName() {
            return name;
        }

        public int getBgColor() {
            return bgColor;
        }

        public int getTextColor() {
            return textColor;
        }

        public int getTextSize() {
            return textSize;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public Object getObject() {
            return object;
        }
    }
}
