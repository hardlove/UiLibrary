package com.hardlove.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hardlove.library.R;
import com.hardlove.library.bean.Sector;
import com.hardlove.library.utils.Util;

import java.util.Objects;

/**
 * Author：CL
 * 日期:2020/10/9
 * 说明：
 **/
public class LuckDiskLayout extends RelativeLayout implements LuckDiskView.OnResultListener {
    private static final String TAG = "LuckPanLayout";
    private final int bgColor = Color.rgb(255, 92, 93);

    private Context context;
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int radius;
    private int CircleX, CircleY;
    private Canvas canvas;
    private boolean isYellow = false;
    private int delayTime = 500;
    private LuckDiskView luckDiskView;
    private ImageView startBtn;

    /**
     * LuckPan 中间对应的Button必须设置tag为 startbtn.
     */
    private static final String START_BTN_TAG = "startView";
    public static final int DEFAULT_TIME_PERIOD = 1000;

    private LuckDiskView.OnResultListener onResultListener;

    public void setOnResultListener(LuckDiskView.OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    private LuckDiskView findLuckDiskView() {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                if (getChildAt(i) instanceof LuckDiskView) {
                    return (LuckDiskView) getChildAt(i);
                }
            }
        }
        return null;
    }


    public LuckDiskLayout(Context context) {
        this(context, null);
    }

    public LuckDiskLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckDiskLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setBackgroundColor(Color.TRANSPARENT);
        backgroundPaint.setColor(bgColor);
        whitePaint.setColor(Color.WHITE);
        yellowPaint.setColor(Color.YELLOW);
        startLuckLight();


    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Objects.requireNonNull(findLuckDiskView()).setOnResultListener(this);
    }

    @Override
    public void onSelectedResult(Sector sector) {
        setStartBtnEnable(true);
        if (onResultListener != null) {
            onResultListener.onSelectedResult(sector);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        int MinValue = Math.min(width, height);

        radius = MinValue / 2;
        CircleX = getWidth() / 2;
        CircleY = getHeight() / 2;

        canvas.drawCircle(CircleX, CircleY, radius, backgroundPaint);

        drawSmallCircle(isYellow);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int centerX = (right - left) / 2;
        int centerY = (bottom - top) / 2;
        boolean panReady = false;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof LuckDiskView) {
                luckDiskView = (LuckDiskView) child;
                int panWidth = child.getWidth();
                int panHeight = child.getHeight();
                child.layout(centerX - panWidth / 2, centerY - panHeight / 2, centerX + panWidth / 2, centerY + panHeight / 2);
                panReady = true;
            } else if (child instanceof ImageView) {
                if (TextUtils.equals((String) child.getTag(), START_BTN_TAG)) {
                    startBtn = (ImageView) child;
                    int btnWidth = child.getWidth();
                    int btnHeight = child.getHeight();
                    child.layout(centerX - btnWidth / 2, centerY - btnHeight / 2, centerX + btnWidth / 2, centerY + btnHeight / 2);
                }
            }
        }

        if (!panReady)
            throw new RuntimeException("Have you add RotatePan in LuckPanLayout element ?");
    }

    private void drawSmallCircle(boolean FirstYellow) {
        int pointDistance = radius - Util.dip2px(context, 10);
        for (int i = 0; i <= 360; i += 20) {
            int x = (int) (pointDistance * Math.sin(Util.change(i))) + CircleX;
            int y = (int) (pointDistance * Math.cos(Util.change(i))) + CircleY;

            if (FirstYellow)
                canvas.drawCircle(x, y, Util.dip2px(context, 4), yellowPaint);
            else
                canvas.drawCircle(x, y, Util.dip2px(context, 4), whitePaint);
            FirstYellow = !FirstYellow;
        }
    }


    /**
     * 开始旋转
     *
     * @param pos       转到指定的转盘，-1 则随机
     * @param delayTime 外围灯光闪烁的间隔时间
     */
    public void startRotate(int pos, int delayTime) {
        luckDiskView.startRotate(pos);
        setDelayTime(delayTime);
        setStartBtnEnable(false);
    }

    protected void setStartBtnEnable(boolean enable) {
        if (startBtn != null)
            startBtn.setEnabled(enable);
        else throw new RuntimeException("Have you add start button in LuckPanLayout element ?");
    }

    private void startLuckLight() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isYellow = !isYellow;
                invalidate();
                postDelayed(this, delayTime);
            }
        }, delayTime);
    }


    protected void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }


}
