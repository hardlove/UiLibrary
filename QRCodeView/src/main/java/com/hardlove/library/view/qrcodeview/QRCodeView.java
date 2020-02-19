package com.hardlove.library.view.qrcodeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;


public class QRCodeView extends CardView {
    private static final String TAG = "QRCodeView";
    int backgroundColor = Color.WHITE;
    int innerBackgroudColor = Color.GRAY;//内部二维码背景色
    int size;//外部View大小
    int radius;//圆角半径
    int padding;//内外边框距离
    private ImageView ivQr;


    public QRCodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //使宽与高保存一致
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    private void initView(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QRCodeView, 0, 0);
        radius = typedArray.getDimensionPixelSize(R.styleable.QRCodeView_radius, dip2px(context, 10));
        padding = typedArray.getDimensionPixelSize(R.styleable.QRCodeView_padding, dip2px(context, 28));
        innerBackgroudColor = typedArray.getColor(R.styleable.QRCodeView_innerBackgroundColor, innerBackgroudColor);
        typedArray.recycle();

        setPadding(0, 0, 0, 0);
        setCardBackgroundColor(backgroundColor);
        setRadius(radius);

        addQRView(getContext());


    }

    private void addQRView(Context context) {
        ivQr = new AppCompatImageView(context);
        ivQr.setScaleType(ImageView.ScaleType.CENTER_CROP);
        CardView.LayoutParams params = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(padding, padding, padding, padding);
        ivQr.setBackgroundColor(innerBackgroudColor);

        addView(ivQr, params);
        requestLayout();
    }

    public ImageView getQRImageView() {
        return ivQr;
    }

    /**
     * 设置内部二维码图片
     *
     * @param bitmap
     */
    public void setInnerBitmap(Bitmap bitmap) {
        ivQr.setImageBitmap(bitmap);
    }

    /**
     * 创建二维码图片
     * 默认带有内边距 40dp
     *
     * @return
     */
    public Bitmap createQRCodeBitmap() {
        return createQRCodeBitmap(dip2px(getContext(), 40));
    }

    /**
     * 创建二维码图片
     *
     * @param padding 内边距 px
     * @return
     */
    public Bitmap createQRCodeBitmap(int padding) {

        int width = ivQr.getWidth();
        int height = ivQr.getHeight();

        int l = 0;
        int t = 0;
        int r = l + width + 2 * padding;
        int b = t + height + 2 * padding;

        Rect rect = new Rect(l, t, r, b);


        Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        canvas.drawRect(rect, paint);
        int count = canvas.save();

        canvas.translate(padding, padding);
        ivQr.draw(canvas);
        canvas.restoreToCount(count);
        return bitmap;

    }

    /**
     * 加载静态xml布局资源，并生成Bitmap
     *
     * @param layoutRes  生成二维码的xml布局模版
     * @param ivQrCodeID layoutRes 中用于显示二维码到 ImageView id
     * @return
     */
    public Bitmap createQRCodeBitmap(@LayoutRes int layoutRes, @IdRes int ivQrCodeID) {

        ViewGroup parent = (ViewGroup) LayoutInflater.from(getContext()).inflate(layoutRes, null);
        ImageView ivQr = parent.findViewById(ivQrCodeID);
        //设置二维码
        ivQr.setImageBitmap(createQRCodeBitmap(dip2px(getContext(), 28)));

        //测量
        int width = View.MeasureSpec.makeMeasureSpec(getScreenWidth(), View.MeasureSpec.EXACTLY);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        parent.measure(width, height);
        int measuredWidth = parent.getMeasuredWidth();
        int measuredHeight = parent.getMeasuredHeight();
        //布局
        parent.layout(0, 0, measuredWidth, measuredHeight);

        Log.d(TAG, "measuredWidth:" + measuredWidth + "   measuredHeight:" + measuredHeight + "  appWith:" + getScreenWidth() + " appHeight:" + getScreenHeight());

        Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        parent.draw(canvas);

        return bitmap;
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }
    public int getScreenHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }
}
