package com.hardlove.library.view;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.hardlove.library.view.ctoobar.R;


/**
 * Created by CL on 2016/12/15.
 * 通用toolbar
 */

public class CToolBar extends FrameLayout implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "CToolBar";
    private int DEFAULT_BOTTOM_LINE_COLOR = Color.parseColor("#F7F9FA");
    private final float defaultTextSize = 17;//sp
    int DEFAULT_TEXT_COLOR = Color.BLACK;
    int DEFAULT_ICON_COLOR = Color.BLACK;
    int DEFAULT_TEXT_SIZE;
    float DEFAULT_ALPHA_NORMAL = 1.0f;
    float DEFAULT_ALPHA_PRESS = 0.5f;
    int DEFAULT_LEFT_BACK_MIN_WIDTH = 90;//返回鍵的最小width
    int DEFAULT_PADDING_LEFT = 20;
    int DEFAULT_PADDING_RIGHT = 20;
    int DEFAULT_PADDING_DRAWABLE = 20;

    private View root;
    private View bottom_line;//底部分割线
    private View statusBar;//顶部状态栏
    private ViewGroup custom_layer;//title内容部分

    TextView tv_left_back;//返回键

    TextView left_tv;
    ImageView left_iv;

    TextView center_tv;
    ImageView center_iv;

    TextView right_tv1;
    ImageView right_iv1;
    TextView right_tv2;
    ImageView right_iv2;
    TextView right_tv3;
    ImageView right_iv3;

    LinearLayout left_layout, center_layout, right_layout;
    SearchLayout search_layout;
    private float c_bar_alpha_press;

    private int c_left_back_min_width;
    private Drawable c_left_back_icon;
    private int c_left_back_icon_color;
    private float c_left_back_alpha_press;
    private String c_left_back_text;
    private int c_left_back_text_color;
    private int c_left_back_text_size;
    private String c_left_tv_text;
    private int c_left_tv_text_color;
    private int c_left_tv_text_size;
    private float c_left_tv_text_alpha_press;
    private int c_left_iv_icon;
    private int c_left_iv_icon_color;
    private float c_right_iv2_icon_alpha_press;
    private float c_left_iv_icon_alpha_press;
    private String c_center_tv_text;
    private int c_center_tv_text_color;
    private int c_center_tv_text_size;
    private float c_center_tv_text_alpha_press;
    private int c_center_iv_icon;
    private int c_center_iv_icon_color;
    private float c_center_iv_icon_alpha_press;
    private String c_right_tv1_text;
    private int c_right_tv1_text_color;
    private int c_right_tv1_text_size;
    private float c_right_tv1_text_alpha_press;
    private int c_right_iv1_icon;
    private int c_right_iv1_icon_color;
    private float c_right_iv1_icon_alpha_press;
    private String c_right_tv2_text;
    private int c_right_tv2_text_color;
    private int c_right_tv2_text_size;
    private float c_right_tv2_text_alpha_press;
    private int c_right_iv2_icon;
    private int c_right_iv2_icon_color;
    private String c_right_tv3_text;
    private int c_right_tv3_text_color;
    private int c_right_tv3_text_size;
    private float c_right_tv3_text_alpha_press;
    private int c_right_iv3_icon;
    private int c_right_iv3_icon_color;
    private float c_right_iv3_icon_alpha_press;

    //控制对应view的显示隐藏
    private boolean c_show_back;
    private boolean c_back_finish;
    private boolean c_show_left_tv;
    private boolean c_show_left_iv;
    private boolean c_show_center_tv;
    private boolean c_show_center_iv;
    private boolean c_show_right_tv1;
    private boolean c_show_right_tv2;
    private boolean c_show_right_tv3;
    private boolean c_show_right_iv1;
    private boolean c_show_right_iv2;
    private boolean c_show_right_iv3;
    private boolean c_show_bottom_line;


    private TextViewSettings tv_left_back_settings, left_tv_settings, center_tv_settings, right_tv1_settings, right_tv2_settings, right_tv3_settings;
    private ImageViewSettings left_iv_settings, center_iv_settings, right_iv1_settings, right_iv2_settings, right_iv3_settings;
    private int c_left_back_paddingLeft;
    private int c_left_back_paddingRight;
    private int c_left_back_drawable_padding;
    private int c_left_tv_paddingLeft;
    private int c_left_tv_paddingRight;
    private int c_left_iv_icon_paddingLeft;
    private int c_left_iv_icon_paddingRight;
    private int c_right_tv1_text_paddingLeft;
    private int c_right_tv1_text_paddingRight;
    private int c_right_iv1_icon_paddingLeft;
    private int c_right_iv1_icon_paddingRight;
    private int c_right_tv2_text_paddingLeft;
    private int c_right_tv2_text_paddingRight;
    private int c_right_iv2_icon_paddingLeft;
    private int c_right_iv2_icon_paddingRight;
    private int c_right_tv3_text_paddingLeft;
    private int c_right_tv3_text_paddingRight;
    private int c_right_iv3_icon_paddingLeft;
    private int c_right_iv3_icon_paddingRight;
    private RecyclerView.OnScrollListener onRlScrollListener;
    private int c_bar_background;
    private View[] views;
    private Object[] settings;
    private boolean addStatusBar = true;//是否是顶部添加状态栏填充位置(默认添加)
    private int status_bar_color;
    private int c_bottom_line_color;
    private int custom_layer_color;
    /*searchLayout相关*/
    private boolean showSearchLayout;
    private int searchLayoutMarginTop;
    private int searchLayoutMarginBottom;
    private int searchLayoutMarginLeft;
    private int searchLayoutMarginRight;
    private int searchLayoutPaddingLR;

    private int search_cornerRadius;
    private int search_strokeWidth;
    private int search_solidColor = Color.parseColor("#eeeeee");
    private int search_strokeColor = Color.parseColor("#eeeeee");
    private int search_searchIconSize;
    private int search_deleteIconSize;
    private int search_searchIconColor;
    private int search_deleteIconColor;
    private Drawable search_searchIcon;
    private Drawable search_deleteIcon;
    private String search_hintText;
    private String search_text;
    private int search_textColor;
    private int search_hitTextColor;
    private int search_textSize;
    private boolean search_enableEdit;
    private int search_textPaddingLR;
    private int search_searchGravity;
    private static final int GRAVITY_START = 1;
    private static final int GRAVITY_CENTER = 2;
    private boolean showSearchIcon;
    private boolean showDeleteIcon;


    public CToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.CToolBar);
    }

    public CToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);

    }


    @Override
    public void setBackgroundColor(int color) {
        root.setBackgroundColor(color);
        statusBar.setBackgroundColor(color);
        custom_layer.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resid) {
        root.setBackgroundResource(resid);
        statusBar.setBackgroundResource(resid);
        custom_layer.setBackgroundResource(resid);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CToolBar, 0, defStyleAttr);
        getAttrs(typedArray);

        ViewGroup title = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.love_c_toolbar, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(title, params);
        findViews(title);
        initViews();
        views = new View[]{tv_left_back, left_tv, left_iv, center_tv, center_iv, right_tv1, right_iv1, right_tv2, right_iv2, right_tv3, right_iv3};
        settings = new Object[]{tv_left_back_settings, left_tv_settings, left_iv_settings, center_tv_settings, center_iv_settings, right_tv1_settings, right_iv1_settings, right_tv2_settings, right_iv2_settings, right_tv3_settings, right_iv3_settings};

        for (int i = 0; i < views.length; i++) {
            views[i].setTag(settings[i]);//绑定属性
            if (views[i] instanceof TextView) {
                adjustTextView((TextView) views[i], (TextViewSettings) settings[i]);
            } else if (views[i] instanceof ImageView) {
                adjustImageView((ImageView) views[i], (ImageViewSettings) settings[i]);
            }
            //监听点击事件
            views[i].setOnClickListener(this);
            views[i].setOnTouchListener(this);
            views[i].setOnLongClickListener(this);

        }
        typedArray.recycle();

    }

    // adjust topMargin
    private void adjustChildMargins() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child == root) {
                //排除 toolbar_root
                continue;
            }
            if (addStatusBar && statusBar.getVisibility() == VISIBLE) {
                int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                custom_layer.measure(widthSpec, heightSpec);
                int measuredHeight = custom_layer.getMeasuredHeight();//测量得到custom_layer的高
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) child.getLayoutParams();
                params.height = measuredHeight;
                params.gravity |= Gravity.BOTTOM;
                child.setLayoutParams(params);
            }
        }
    }

    private void initViews() {
        tv_left_back.setMinWidth(c_left_back_min_width);
        tv_left_back.setGravity(Gravity.CENTER);
        showBackView(c_show_back);
        bottom_line.setVisibility(c_show_bottom_line ? VISIBLE : GONE);
        bottom_line.setBackgroundColor(c_bottom_line_color);
        statusBar.setBackgroundColor(status_bar_color);
        custom_layer.setBackgroundColor(custom_layer_color);
        root.setBackgroundColor(c_bar_background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4及以上支持
            statusBar.setVisibility(addStatusBar ? VISIBLE : GONE);
        } else {
            statusBar.setVisibility(GONE);//4.4以下不支持沉浸式直接隐藏
        }
        initSearchLayout();
    }

    private void initSearchLayout() {
        search_layout.setOnClickListener(this);
        search_layout.setOnLongClickListener(this);
        if (showSearchLayout) {
            search_layout.setVisibility(VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) search_layout.getLayoutParams();
            params.topMargin = searchLayoutMarginTop;
            params.bottomMargin = searchLayoutMarginBottom;
            params.leftMargin = searchLayoutMarginLeft;
            params.rightMargin = searchLayoutMarginRight;
            search_layout.setLayoutParams(params);
        } else {
            search_layout.setVisibility(GONE);
        }
        search_layout.setPadding(searchLayoutPaddingLR, 0, searchLayoutPaddingLR, 0);
        search_layout.setSolidColor(search_solidColor);
        search_layout.setStrokeColor(search_strokeColor);
        search_layout.setCornerRadius(search_cornerRadius);
        search_layout.setStrokeWidth(search_strokeWidth);
        search_layout.setSearchGravity(search_searchGravity);
        search_layout.setSearchIcon(search_searchIcon);
        search_layout.setDeleteIcon(search_deleteIcon);
        search_layout.setSearchIconSize(search_searchIconSize);
        search_layout.setDeleteIconSize(search_deleteIconSize);
        search_layout.setEnableEdit(search_enableEdit);
        search_layout.setTextSize(search_textSize);
        search_layout.setTextColor(search_textColor);
        search_layout.setHintText(search_hintText);
        search_layout.setText(search_text);
        search_layout.setHintTextColor(search_hitTextColor);
        search_layout.setTextPaddingLR(search_textPaddingLR);
        if (search_searchIconColor != Integer.MIN_VALUE) {
            search_layout.setSearchIconColor(search_searchIconColor);
        }
        if (search_deleteIconColor != Integer.MIN_VALUE) {
            search_layout.setDeleteIconColor(search_deleteIconColor);
        }
        search_layout.setShowSearchIcon(showSearchIcon);
        search_layout.setShowDeleteIcon(showDeleteIcon);


    }

    /**
     * 显示或隐藏BackView
     */
    public void showBackView(boolean showBack) {
        if (showBack) {
            tv_left_back.setVisibility(VISIBLE);
        } else {
            tv_left_back.setVisibility(GONE);
        }
    }

    /*是否显示BackView*/
    public boolean isShowBackView() {
        return c_show_back;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 图片上色
     */
    public void setDrawableColor(Drawable drawable, int color) {
        if (drawable != null) {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    private void adjustTextView(TextView v, TextViewSettings settings) {
        if (v == null || settings == null) return;
        v.setText(settings.text);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, settings.textSize);
        v.setTextColor(settings.textColor);
        v.setPadding(settings.paddingLeft, settings.paddingTop, settings.paddingRight, settings.paddingBottom);
        v.setAlpha(settings.alpha);
        v.setVisibility(settings.isShow ? VISIBLE : GONE);
        if (settings.leftDrawable != null) {
            //图片shangse
            setDrawableColor(settings.leftDrawable, settings.drawableColor);

            v.setCompoundDrawablePadding(settings.drawablePadding);
            v.setCompoundDrawablesRelativeWithIntrinsicBounds(settings.leftDrawable, null, null, null);
        }
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    private OnCToolBarClickListener onCToolBarClickListener;

    public void setOnCToolBarClickListener(OnCToolBarClickListener onCToolBarClickListener) {
        this.onCToolBarClickListener = onCToolBarClickListener;
    }

    private OnCToolBarLongClickListener onCToolBarLongClickListener;

    public void setOnCToolBarLongClickListener(OnCToolBarLongClickListener onCToolBarLongClickListener) {
        this.onCToolBarLongClickListener = onCToolBarLongClickListener;
    }


    /**
     * 短按监听
     */
    public static class OnCToolBarClickListener {
        public void onLeftBackClick() {
        }

        public void onLeftTvClick() {
        }

        public void onLeftIvClick() {
        }

        public void onCenterTvClick() {
        }

        public void onCenterIvClick() {
        }

        public void onRightTv1Click() {
        }

        public void onRightIV1Click() {
        }

        public void onRightTv2Click() {
        }

        public void onRightIv2Click() {
        }

        public void onRightTv3Click() {
        }

        public void onRightIv3Click() {
        }

        public void onSearchLayoutClick() {
        }
    }

    /**
     * 长按监听
     */
    public static class OnCToolBarLongClickListener {
        public boolean onLeftBackLongClick() {
            return false;
        }

        public boolean onLeftTvLongClick() {
            return false;
        }

        public boolean onLeftIvLongClick() {
            return false;
        }

        public boolean onCenterTvLongClick() {
            return false;
        }

        public boolean onCenterIvLongClick() {
            return false;
        }

        public boolean onRightTv1LongClick() {
            return false;
        }

        public boolean onRightIV1LongClick() {
            return false;
        }

        public boolean onRightTv2LongClick() {
            return false;
        }

        public boolean onRightIv2LongClick() {
            return false;
        }

        public boolean onRightTv3LongClick() {
            return false;
        }

        public boolean onRightIv3LongClick() {
            return false;
        }

        public boolean onSearchLayoutLongClick() {
            return false;
        }
    }

    /**
     * 需要有初始值
     */
    class TextViewSettings {
        public TextViewSettings(String text, int textSize, int textColor, float alpha) {
            this.text = text;
            this.textSize = textSize;
            this.textColor = textColor;
            this.alpha = alpha;
        }

        String text;
        int textSize;
        int textColor;
        int drawableColor;
        float alpha;
        boolean isShow;

        public void setLeftDrawable(Drawable leftDrawable) {
            this.leftDrawable = leftDrawable;
        }

        Drawable leftDrawable;

        int paddingLeft;
        int paddingTop;
        int paddingRight;
        int paddingBottom;

        int drawablePadding;

        int margingLeft;
        int margingTop;
        int margingRight;
        int margingBottom;


        public void setPadding(int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
            this.paddingLeft = paddingLeft;
            this.paddingRight = paddingRight;
            this.paddingTop = paddingTop;
            this.paddingBottom = paddingBottom;
        }

        public void setDrawablePadding(int drawablePadding) {
            this.drawablePadding = drawablePadding;
        }

        public void setMarging(int margingLeft, int margingRight, int margingTop, int margingBottom) {
            this.margingLeft = margingLeft;
            this.margingRight = margingRight;
            this.margingTop = margingTop;
            this.margingBottom = margingBottom;
        }


        public void setDrawableColor(int drawableColor) {
            this.drawableColor = drawableColor;
        }

        public void setIsShow(boolean isShow) {
            this.isShow = isShow;
        }


    }

    class ImageViewSettings {


        int iconResId;
        int iconColor;
        float alpha;

        int paddingLeft;
        int paddingTop;
        int paddingRight;
        int paddingBottom;


        int margingLeft;
        int margingTop;
        int margingRight;
        int margingBottom;
        boolean isShow;


        public ImageViewSettings(int iconResId, int iconColor, float alpha) {
            this.iconResId = iconResId;
            this.iconColor = iconColor;
            this.alpha = alpha;
        }

        public void setPadding(int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
            this.paddingLeft = paddingLeft;
            this.paddingRight = paddingRight;
            this.paddingTop = paddingTop;
            this.paddingBottom = paddingBottom;
        }

        public void setMarging(int margingLeft, int margingRight, int margingTop, int margingBottom) {
            this.margingLeft = margingLeft;
            this.margingRight = margingRight;
            this.margingTop = margingTop;
            this.margingBottom = margingBottom;
        }

        public void setIsShow(boolean isShow) {
            this.isShow = isShow;
        }
    }

    private void findViews(ViewGroup view) {
        root = view.findViewById(R.id.toolbar_root);
        statusBar = view.findViewById(R.id.system_status_bar);
        custom_layer = view.findViewById(R.id.custom_layer);

        bottom_line = view.findViewById(R.id.line);
        tv_left_back = view.findViewById(R.id.tv_left_back);
        left_tv = view.findViewById(R.id.left_tv);
        left_iv = view.findViewById(R.id.left_iv);
        center_tv = view.findViewById(R.id.center_tv);
        center_iv = view.findViewById(R.id.center_iv);
        right_tv1 = view.findViewById(R.id.right_tv1);
        right_iv1 = view.findViewById(R.id.right_iv1);
        right_tv2 = view.findViewById(R.id.right_tv2);
        right_iv2 = view.findViewById(R.id.right_iv2);
        right_tv3 = view.findViewById(R.id.right_tv3);
        right_iv3 = view.findViewById(R.id.right_iv3);

        left_layout = view.findViewById(R.id.left_layout);
        center_layout = view.findViewById(R.id.center_layout);
        right_layout = view.findViewById(R.id.right_layout);
        search_layout = view.findViewById(R.id.search_layout);


    }

    private void goneViews(View... views) {
        if (views == null) return;
        for (View v : views) {
            v.setVisibility(GONE);
        }
    }

    private void VisibleViews(View... views) {
        if (views == null) return;
        for (View v : views) {
            v.setVisibility(VISIBLE);
        }
    }


    public void onBackViewClick() {
        if (isShowBackView() && c_back_finish) {
            Context ctx = getContext();
            if (ctx instanceof Activity) {
                //模拟back键
                ((Activity) ctx).onBackPressed();
            } else if (ctx instanceof ContextWrapper) {
                Context context = ((ContextWrapper) ctx).getBaseContext();
                if (context instanceof Activity) {
                    //模拟back键
                    ((Activity) context).onBackPressed();
                }
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v == tv_left_back) {
            onBackViewClick();
        }

        if (onCToolBarClickListener == null) return;
        if (v == tv_left_back) {
            onCToolBarClickListener.onLeftBackClick();
        } else if (v == left_tv) {
            onCToolBarClickListener.onLeftTvClick();
        } else if (v == left_iv) {
            onCToolBarClickListener.onLeftIvClick();
        } else if (v == center_tv) {
            onCToolBarClickListener.onCenterTvClick();
        } else if (v == center_iv) {
            onCToolBarClickListener.onCenterIvClick();
        } else if (v == right_tv1) {
            onCToolBarClickListener.onRightTv1Click();
        } else if (v == right_iv1) {
            onCToolBarClickListener.onRightIV1Click();
        } else if (v == right_tv2) {
            onCToolBarClickListener.onRightTv2Click();
        } else if (v == right_iv2) {
            onCToolBarClickListener.onRightIv2Click();
        } else if (v == right_tv3) {
            onCToolBarClickListener.onRightTv3Click();
        } else if (v == right_iv3) {
            onCToolBarClickListener.onRightIv3Click();
        } else if (v == search_layout) {
            onCToolBarClickListener.onSearchLayoutClick();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onCToolBarLongClickListener == null) return false;
        if (v == tv_left_back) {
            return onCToolBarLongClickListener.onLeftBackLongClick();
        } else if (v == left_tv) {
            return onCToolBarLongClickListener.onLeftTvLongClick();
        } else if (v == left_iv) {
            return onCToolBarLongClickListener.onLeftIvLongClick();
        } else if (v == center_tv) {
            return onCToolBarLongClickListener.onCenterTvLongClick();
        } else if (v == center_iv) {
            return onCToolBarLongClickListener.onCenterIvLongClick();
        } else if (v == right_tv1) {
            return onCToolBarLongClickListener.onRightTv1LongClick();
        } else if (v == right_iv1) {
            return onCToolBarLongClickListener.onRightIV1LongClick();
        } else if (v == right_tv2) {
            return onCToolBarLongClickListener.onRightTv2LongClick();
        } else if (v == right_iv2) {
            return onCToolBarLongClickListener.onRightIv2LongClick();
        } else if (v == right_tv3) {
            return onCToolBarLongClickListener.onRightTv3LongClick();
        } else if (v == right_iv3) {
            return onCToolBarLongClickListener.onRightIv3LongClick();
        } else if (v == search_layout) {
            onCToolBarLongClickListener.onSearchLayoutLongClick();
        }
        return false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setAlpha(c_bar_alpha_press);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setAlpha(1.0f);
        }
        return false;
    }


    private ListView mListView;
    private RecyclerView mRecyclerView;
    private float scrollStep = 500;//默认toolbar背景透明时的滑动距离
    private float totalDy = 0;


    public void setupWithListView(ListView listView, final View headView, int reverseColor) {
        this.mListView = listView;
        this.reverseColor = reverseColor;
        if (mListView == null) {
            return;
        }
        if (headView != null) {
            //测量下高度
            headView.measure(View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            scrollStep = headView.getMeasuredHeight() - this.getMeasuredHeight();
            Log.d(TAG, "scrollStep:" + scrollStep + "   headView Height:" + headView.getMeasuredHeight() + "   toolbar Height:" + this.getMeasuredHeight());
        }
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private SparseArray recordSp = new SparseArray(0);
            private int mCurrentfirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mCurrentfirstVisibleItem = firstVisibleItem;
                View firstView = view.getChildAt(0);
                if (null != firstView) {
                    ItemRecod itemRecord = (ItemRecod) recordSp.get(firstVisibleItem);
                    if (null == itemRecord) {
                        itemRecord = new ItemRecod();
                    }
                    itemRecord.height = firstView.getHeight();
                    itemRecord.top = firstView.getTop();
                    recordSp.append(firstVisibleItem, itemRecord);

                    try {
                        totalDy = getScrollY();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //在此进行你需要的操作
                    changeBackgroundAlpha();
                    changeViewBackground();


                }

            }

            private int getScrollY() {
                int height = 0;
                for (int i = 0; i < mCurrentfirstVisibleItem; i++) {
                    ItemRecod itemRecod = (ItemRecod) recordSp.get(i);
                    height += itemRecod.height;//报空指针，未找到原因
                }
                ItemRecod itemRecod = (ItemRecod) recordSp.get(mCurrentfirstVisibleItem);
                if (null == itemRecod) {
                    itemRecod = new ItemRecod();
                }
                return height - itemRecod.top;
            }

            class ItemRecod {
                int height = 0;
                int top = 0;
            }
        });


    }


    /**
     * @param recyclerView
     * @param headView
     */
    public void setupWithRecyclerView(RecyclerView recyclerView, View headView, int reverseColor) {
        this.mRecyclerView = recyclerView;
        this.reverseColor = reverseColor;
        if (mRecyclerView == null) {
            return;
        }
        if (headView != null) {
            //测量下高度
            headView.measure(View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            scrollStep = headView.getMeasuredHeight() - this.getMeasuredHeight();
            Log.d(TAG, "scrollStep:" + scrollStep + "   headView Height:" + headView.getMeasuredHeight() + "   toolbar Height:" + this.getMeasuredHeight());
        }
        onRlScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalDy -= dy;
                //在此进行你需要的操作
                changeBackgroundAlpha();
                changeViewBackground();

            }


        };
        mRecyclerView.addOnScrollListener(onRlScrollListener);

    }

    public void setupWithScrollView(NestedScrollView nestedScrollView, View headView, int reverseColor) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        adjustChildMargins();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRecyclerView != null && onRlScrollListener != null) {
            mRecyclerView.removeOnScrollListener(onRlScrollListener);
        }

    }

    private void changeBackgroundAlpha() {
        float scale = totalDy * 1.0f / scrollStep;
        if (scale >= 1) {
            scale = 1;
        }

        int alpha = (int) (scale * 255);
        if (root.getBackground() != null) {
            root.getBackground().setAlpha(alpha);
        }
        Log.e(TAG, "scale=" + scale + "  totalDy=" + totalDy + "  scrollStep=" + scrollStep + " alpha=" + alpha);


    }

    //渐变后的颜色
    private int reverseColor = Color.BLACK;

    private void changeViewBackground() {
        float scale = totalDy * 1.0f / scrollStep;
        for (View v : views) {
            if (scale < 0.5) {
                if (v.isShown()) {
                    if (v instanceof TextView) {
                        TextViewSettings settings = (TextViewSettings) v.getTag();
                        ((TextView) v).setTextColor(settings.textColor);
                        Drawable[] drawables = ((TextView) v).getCompoundDrawables();
                        if (drawables != null && drawables.length > 0) {
                            for (int i = 0; i < drawables.length; i++) {
                                setDrawableColor(drawables[i], settings.drawableColor);
                            }
                        }
                    } else if (v instanceof ImageView) {
                        ((ImageView) v).clearColorFilter();
                    }
                }
            } else {
                if (v.isShown()) {
                    if (v instanceof TextView) {
                        ((TextView) v).setTextColor(reverseColor);
                        Drawable[] drawables = ((TextView) v).getCompoundDrawables();
                        if (drawables != null && drawables.length > 0) {
                            for (int i = 0; i < drawables.length; i++) {
                                setDrawableColor(drawables[i], reverseColor);
                            }
                        }
                    } else if (v instanceof ImageView) {
                        ((ImageView) v).setColorFilter(reverseColor, PorterDuff.Mode.SRC_IN);
                    }
                }
            }
        }
    }

    public void setCenterText(String title) {
        center_tv.setText(title);
        if (!center_tv.isShown()) {
            center_tv.setVisibility(VISIBLE);
        }
    }


    public TextView getTv_left_back() {
        return tv_left_back;
    }

    public TextView getLeft_tv() {
        return left_tv;
    }

    public ImageView getLeft_iv() {
        return left_iv;
    }

    public TextView getCenter_tv() {
        return center_tv;
    }

    public ImageView getCenter_iv() {
        return center_iv;
    }

    public TextView getRight_tv1() {
        return right_tv1;
    }

    public ImageView getRight_iv1() {
        return right_iv1;
    }

    public TextView getRight_tv2() {
        return right_tv2;
    }

    public ImageView getRight_iv2() {
        return right_iv2;
    }

    public TextView getRight_tv3() {
        return right_tv3;
    }

    public ImageView getRight_iv3() {
        return right_iv3;
    }

    public LinearLayout getLeft_layout() {
        return left_layout;
    }

    public LinearLayout getCenter_layout() {
        return center_layout;
    }

    public LinearLayout getRight_layout() {
        return right_layout;
    }

    public View getBottom_line() {
        return bottom_line;
    }

    public ViewGroup getCustom_layer() {
        return custom_layer;
    }

    public SearchLayout getSearch_layout() {
        return search_layout;
    }

    public boolean isShowSearchLayout() {
        return showSearchLayout;
    }

    public View getStatusBar() {
        return statusBar;
    }

    public boolean isAddStatusBar() {
        return addStatusBar;
    }

    public void showView(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(VISIBLE);
            }
        }
    }

    public void hideView(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(GONE);
            }
        }
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

    private void getAttrs(TypedArray array) {
        DEFAULT_TEXT_SIZE = sp2px(getContext(), defaultTextSize);

        c_bar_alpha_press = array.getFloat(R.styleable.CToolBar_c_bar_alpha_press, DEFAULT_ALPHA_PRESS);
        c_bar_background = array.getColor(R.styleable.CToolBar_c_bar_background, Color.TRANSPARENT);


        DEFAULT_TEXT_COLOR = array.getColor(R.styleable.CToolBar_c_bar_text_color, DEFAULT_TEXT_COLOR);
        DEFAULT_ICON_COLOR = array.getColor(R.styleable.CToolBar_c_bar_icon_color, DEFAULT_ICON_COLOR);
        status_bar_color = array.getColor(R.styleable.CToolBar_c_status_bar_color, Color.TRANSPARENT);
        custom_layer_color = array.getColor(R.styleable.CToolBar_c_custom_layer_color, Color.TRANSPARENT);

        c_show_back = array.getBoolean(R.styleable.CToolBar_c_show_back, true);
        c_back_finish = array.getBoolean(R.styleable.CToolBar_c_back_finish_enable, true);

        //控制对应view的显示|隐藏
        c_show_bottom_line = array.getBoolean(R.styleable.CToolBar_c_show_bottom_line, false);
        c_bottom_line_color = array.getColor(R.styleable.CToolBar_c_bottom_line_color, DEFAULT_BOTTOM_LINE_COLOR);

        c_show_left_tv = array.getBoolean(R.styleable.CToolBar_c_show_left_tv, false);
        c_show_left_iv = array.getBoolean(R.styleable.CToolBar_c_show_left_iv, false);

        c_show_center_tv = array.getBoolean(R.styleable.CToolBar_c_show_center_tv, true);
        c_show_center_iv = array.getBoolean(R.styleable.CToolBar_c_show_center_iv, false);

        c_show_right_tv1 = array.getBoolean(R.styleable.CToolBar_c_show_right_tv1, false);
        c_show_right_tv2 = array.getBoolean(R.styleable.CToolBar_c_show_right_tv2, false);
        c_show_right_tv3 = array.getBoolean(R.styleable.CToolBar_c_show_right_tv3, false);

        c_show_right_iv1 = array.getBoolean(R.styleable.CToolBar_c_show_right_iv1, false);
        c_show_right_iv2 = array.getBoolean(R.styleable.CToolBar_c_show_right_iv2, false);
        c_show_right_iv3 = array.getBoolean(R.styleable.CToolBar_c_show_right_iv3, false);

        c_left_back_min_width = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_back_min_width, DEFAULT_LEFT_BACK_MIN_WIDTH);
        c_left_back_icon = array.getDrawable(R.styleable.CToolBar_c_left_back_icon);
        if (c_left_back_icon == null) {
            c_left_back_icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_back_arrow);
        }
        c_left_back_icon_color = array.getColor(R.styleable.CToolBar_c_left_back_icon_color, DEFAULT_ICON_COLOR);
        c_left_back_alpha_press = array.getFloat(R.styleable.CToolBar_c_left_back_alpha_press, DEFAULT_ALPHA_NORMAL);
        c_left_back_text = array.getString(R.styleable.CToolBar_c_left_back_text);
        c_left_back_text_color = array.getColor(R.styleable.CToolBar_c_left_back_text_color, DEFAULT_TEXT_COLOR);
        c_left_back_text_size = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_back_text_size, DEFAULT_TEXT_SIZE);
        c_left_back_paddingLeft = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_back_paddingLeft, DEFAULT_PADDING_LEFT);
        c_left_back_paddingRight = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_back_paddingRight, DEFAULT_PADDING_RIGHT);
        c_left_back_drawable_padding = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_back_drawable_padding, DEFAULT_PADDING_DRAWABLE);
        tv_left_back_settings = new TextViewSettings(c_left_back_text, c_left_back_text_size, c_left_back_text_color, c_left_back_alpha_press);
        tv_left_back_settings.setLeftDrawable(c_left_back_icon);
        tv_left_back_settings.setDrawableColor(c_left_back_icon_color);
        tv_left_back_settings.setPadding(c_left_back_paddingLeft, c_left_back_paddingRight, 0, 0);
        tv_left_back_settings.setMarging(0, 0, 0, 0);
        tv_left_back_settings.setDrawablePadding(c_left_back_drawable_padding);
        tv_left_back_settings.setIsShow(c_show_back);


        c_left_tv_text = array.getString(R.styleable.CToolBar_c_left_tv_text);
        c_left_tv_text_color = array.getColor(R.styleable.CToolBar_c_left_tv_text_color, DEFAULT_TEXT_COLOR);
        c_left_tv_text_size = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_tv_text_size, DEFAULT_TEXT_SIZE);
        c_left_tv_text_alpha_press = array.getFloat(R.styleable.CToolBar_c_left_tv_text_alpha_press, DEFAULT_ALPHA_NORMAL);
        c_left_tv_paddingLeft = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_tv_text_paddingLeft, DEFAULT_PADDING_LEFT);
        c_left_tv_paddingRight = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_tv_text_paddingRight, DEFAULT_PADDING_RIGHT);
        left_tv_settings = new TextViewSettings(c_left_tv_text, c_left_tv_text_size, c_left_tv_text_color, c_left_tv_text_alpha_press);
        left_tv_settings.setPadding(c_left_tv_paddingLeft, c_left_tv_paddingRight, 0, 0);
        left_tv_settings.setMarging(0, 0, 0, 0);
        left_tv_settings.setIsShow(c_show_left_tv);
        c_left_iv_icon = array.getResourceId(R.styleable.CToolBar_c_left_iv_icon, -1);
        c_left_iv_icon_color = array.getColor(R.styleable.CToolBar_c_left_iv_icon_color, DEFAULT_ICON_COLOR);
        c_left_iv_icon_alpha_press = array.getFloat(R.styleable.CToolBar_c_left_iv_icon_alpha_press, DEFAULT_ALPHA_NORMAL);
        c_left_iv_icon_paddingLeft = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_iv_icon_paddingLeft, DEFAULT_PADDING_LEFT);
        c_left_iv_icon_paddingRight = array.getDimensionPixelSize(R.styleable.CToolBar_c_left_iv_icon_paddingRight, DEFAULT_PADDING_RIGHT);
        left_iv_settings = new ImageViewSettings(c_left_iv_icon, c_left_iv_icon_color, c_left_iv_icon_alpha_press);
        left_iv_settings.setPadding(c_left_iv_icon_paddingLeft, c_left_iv_icon_paddingRight, 0, 0);
        left_iv_settings.setMarging(0, 0, 0, 0);
        left_iv_settings.setIsShow(c_show_left_iv);

        c_center_tv_text = array.getString(R.styleable.CToolBar_c_center_tv_text);
        c_center_tv_text_color = array.getColor(R.styleable.CToolBar_c_center_tv_text_color, DEFAULT_TEXT_COLOR);
        c_center_tv_text_size = array.getDimensionPixelSize(R.styleable.CToolBar_c_center_tv_text_size, DEFAULT_TEXT_SIZE);
        c_center_tv_text_alpha_press = array.getFloat(R.styleable.CToolBar_c_center_tv_text_alpha_press, DEFAULT_ALPHA_NORMAL);
        center_tv_settings = new TextViewSettings(c_center_tv_text, c_center_tv_text_size, c_center_tv_text_color, c_center_tv_text_alpha_press);
        center_tv_settings.setPadding(0, 0, 0, 0);
        center_tv_settings.setMarging(0, 0, 0, 0);
        center_tv_settings.setIsShow(c_show_center_tv);

        c_center_iv_icon = array.getResourceId(R.styleable.CToolBar_c_center_iv_icon, -1);
        c_center_iv_icon_color = array.getColor(R.styleable.CToolBar_c_center_iv_icon_color, DEFAULT_ICON_COLOR);
        c_center_iv_icon_alpha_press = array.getFloat(R.styleable.CToolBar_c_center_iv_icon_alpha_press, DEFAULT_ALPHA_NORMAL);
        center_iv_settings = new ImageViewSettings(c_center_iv_icon, c_center_iv_icon_color, c_center_iv_icon_alpha_press);
        center_iv_settings.setPadding(0, 0, 0, 0);
        center_iv_settings.setMarging(0, 0, 0, 0);
        center_iv_settings.setIsShow(c_show_center_iv);

        c_right_tv1_text = array.getString(R.styleable.CToolBar_c_right_tv1_text);
        c_right_tv1_text_color = array.getColor(R.styleable.CToolBar_c_right_tv1_text_color, DEFAULT_TEXT_COLOR);
        c_right_tv1_text_size = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_tv1_text_size, DEFAULT_TEXT_SIZE);
        c_right_tv1_text_alpha_press = array.getFloat(R.styleable.CToolBar_c_right_tv1_text_alpha_press, DEFAULT_ALPHA_NORMAL);
        c_right_iv1_icon = array.getResourceId(R.styleable.CToolBar_c_right_iv1_icon, -1);
        c_right_iv1_icon_color = array.getColor(R.styleable.CToolBar_c_right_iv1_icon_color, DEFAULT_ICON_COLOR);
        c_right_iv1_icon_alpha_press = array.getFloat(R.styleable.CToolBar_c_right_iv1_icon_alpha_press, DEFAULT_ALPHA_NORMAL);
        c_right_tv1_text_paddingLeft = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_tv1_text_paddingLeft, DEFAULT_PADDING_LEFT);
        c_right_tv1_text_paddingRight = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_tv1_text_paddingRight, DEFAULT_PADDING_RIGHT);
        c_right_iv1_icon_paddingLeft = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_iv1_icon_paddingLeft, DEFAULT_PADDING_LEFT);
        c_right_iv1_icon_paddingRight = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_iv1_icon_paddingRight, DEFAULT_PADDING_RIGHT);

        right_tv1_settings = new TextViewSettings(c_right_tv1_text, c_right_tv1_text_size, c_right_tv1_text_color, c_right_tv1_text_alpha_press);
        right_tv1_settings.setPadding(c_right_tv1_text_paddingLeft, c_right_tv1_text_paddingRight, 0, 0);
        right_tv1_settings.setMarging(0, 0, 0, 0);
        right_tv1_settings.setIsShow(c_show_right_tv1);
        right_iv1_settings = new ImageViewSettings(c_right_iv1_icon, c_right_iv1_icon_color, c_right_iv1_icon_alpha_press);
        right_iv1_settings.setPadding(c_right_iv1_icon_paddingLeft, c_right_iv1_icon_paddingRight, 0, 0);
        right_iv1_settings.setMarging(0, 0, 0, 0);
        right_iv1_settings.setIsShow(c_show_right_iv1);

        c_right_tv2_text = array.getString(R.styleable.CToolBar_c_right_tv2_text);
        c_right_tv2_text_color = array.getColor(R.styleable.CToolBar_c_right_tv2_text_color, DEFAULT_TEXT_COLOR);
        c_right_tv2_text_size = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_tv2_text_size, DEFAULT_TEXT_SIZE);
        c_right_tv2_text_alpha_press = array.getFloat(R.styleable.CToolBar_c_right_tv2_text_alpha_press, DEFAULT_ALPHA_NORMAL);
        c_right_iv2_icon = array.getResourceId(R.styleable.CToolBar_c_right_iv2_icon, -1);
        c_right_iv2_icon_color = array.getColor(R.styleable.CToolBar_c_right_iv2_icon_color, DEFAULT_ICON_COLOR);
        c_right_iv2_icon_alpha_press = array.getFloat(R.styleable.CToolBar_c_right_iv2_icon_alpha_press, DEFAULT_ALPHA_NORMAL);
        c_right_tv2_text_paddingLeft = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_tv2_text_paddingLeft, DEFAULT_PADDING_LEFT);
        c_right_tv2_text_paddingRight = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_tv2_text_paddingRight, DEFAULT_PADDING_RIGHT);
        c_right_iv2_icon_paddingLeft = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_iv2_icon_paddingLeft, DEFAULT_PADDING_LEFT);
        c_right_iv2_icon_paddingRight = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_iv2_icon_paddingRight, DEFAULT_PADDING_RIGHT);

        right_tv2_settings = new TextViewSettings(c_right_tv2_text, c_right_tv2_text_size, c_right_tv2_text_color, c_right_tv2_text_alpha_press);
        right_tv2_settings.setPadding(c_right_tv2_text_paddingLeft, c_right_tv2_text_paddingRight, 0, 0);
        right_tv2_settings.setMarging(0, 0, 0, 0);
        right_tv2_settings.setIsShow(c_show_right_tv2);
        right_iv2_settings = new ImageViewSettings(c_right_iv2_icon, c_right_iv2_icon_color, c_right_iv2_icon_alpha_press);
        right_iv2_settings.setPadding(c_right_iv2_icon_paddingLeft, c_right_iv2_icon_paddingRight, 0, 0);
        right_iv2_settings.setMarging(0, 0, 0, 0);
        right_iv2_settings.setIsShow(c_show_right_iv2);

        c_right_tv3_text = array.getString(R.styleable.CToolBar_c_right_tv3_text);
        c_right_tv3_text_color = array.getColor(R.styleable.CToolBar_c_right_tv3_text_color, DEFAULT_TEXT_COLOR);
        c_right_tv3_text_size = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_tv3_text_size, DEFAULT_TEXT_SIZE);
        c_right_tv3_text_alpha_press = array.getFloat(R.styleable.CToolBar_c_right_tv3_text_alpha_press, DEFAULT_ALPHA_NORMAL);
        c_right_iv3_icon = array.getResourceId(R.styleable.CToolBar_c_right_iv3_icon, -1);
        c_right_iv3_icon_color = array.getColor(R.styleable.CToolBar_c_right_iv3_icon_color, DEFAULT_ICON_COLOR);
        c_right_iv3_icon_alpha_press = array.getFloat(R.styleable.CToolBar_c_right_iv3_icon_alpha_press, DEFAULT_ALPHA_NORMAL);
        c_right_tv3_text_paddingLeft = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_tv3_text_paddingLeft, DEFAULT_PADDING_LEFT);
        c_right_tv3_text_paddingRight = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_tv3_text_paddingRight, DEFAULT_PADDING_RIGHT);
        c_right_iv3_icon_paddingLeft = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_iv3_icon_paddingLeft, DEFAULT_PADDING_LEFT);
        c_right_iv3_icon_paddingRight = array.getDimensionPixelSize(R.styleable.CToolBar_c_right_iv3_icon_paddingRight, DEFAULT_PADDING_RIGHT);

        right_tv3_settings = new TextViewSettings(c_right_tv3_text, c_right_tv3_text_size, c_right_tv3_text_color, c_right_tv3_text_alpha_press);
        right_tv3_settings.setPadding(c_right_tv3_text_paddingLeft, c_right_tv3_text_paddingRight, 0, 0);
        right_tv3_settings.setMarging(0, 0, 0, 0);
        right_tv3_settings.setIsShow(c_show_right_tv3);
        right_iv3_settings = new ImageViewSettings(c_right_iv3_icon, c_right_iv3_icon_color, c_right_iv3_icon_alpha_press);
        right_iv3_settings.setPadding(c_right_iv3_icon_paddingLeft, c_right_iv3_icon_paddingRight, 0, 0);
        right_iv3_settings.setMarging(0, 0, 0, 0);
        right_iv3_settings.setIsShow(c_show_right_iv3);

        addStatusBar = array.getBoolean(R.styleable.CToolBar_c_add_status_bar, addStatusBar);

        /*searchLayout相关*/
        initSearchLayoutAttrs(array);

    }

    private void initSearchLayoutAttrs(TypedArray array) {
        showSearchLayout = array.getBoolean(R.styleable.CToolBar_c_show_search_layout, false);
        searchLayoutMarginTop = array.getDimensionPixelOffset(R.styleable.CToolBar_c_search_layout_margin_top, dip2px(getContext(), 5));
        searchLayoutMarginBottom = array.getDimensionPixelOffset(R.styleable.CToolBar_c_search_layout_margin_bottom, dip2px(getContext(), 5));
        searchLayoutMarginLeft = array.getDimensionPixelOffset(R.styleable.CToolBar_c_search_layout_margin_left, dip2px(getContext(), 5));
        searchLayoutMarginRight = array.getDimensionPixelOffset(R.styleable.CToolBar_c_search_layout_margin_right, dip2px(getContext(), 5));
        searchLayoutPaddingLR = array.getDimensionPixelOffset(R.styleable.CToolBar_c_search_layout_padding_left_right, dip2px(getContext(), 5));


        search_cornerRadius = array.getDimensionPixelOffset(R.styleable.CToolBar_c_search_layout_radius, dip2px(getContext(), 4));
        search_strokeWidth = array.getDimensionPixelOffset(R.styleable.CToolBar_c_search_layout_stroke_width, dip2px(getContext(), 1));
        search_solidColor = array.getColor(R.styleable.CToolBar_c_search_layout_solid_color, search_solidColor);
        search_strokeColor = array.getColor(R.styleable.CToolBar_c_search_layout_stroke_color, search_strokeColor);

        showSearchIcon = array.getBoolean(R.styleable.CToolBar_c_show_search_search_icon, true);
        showDeleteIcon = array.getBoolean(R.styleable.CToolBar_c_show_search_delete_icon, false);

        search_searchIconSize = array.getDimensionPixelSize(R.styleable.CToolBar_c_search_icon_size, dip2px(getContext(), 16));
        search_deleteIconSize = array.getDimensionPixelSize(R.styleable.CToolBar_c_search_delete_icon_size, dip2px(getContext(), 16));
        search_searchIconColor = array.getColor(R.styleable.CToolBar_c_search_icon_color, Integer.MIN_VALUE);
        search_deleteIconColor = array.getColor(R.styleable.CToolBar_c_search_delete_icon_color, Integer.MIN_VALUE);
        search_searchIcon = array.getDrawable(R.styleable.CToolBar_c_search_icon);
        search_deleteIcon = array.getDrawable(R.styleable.CToolBar_c_search_delete_icon);
        if (search_searchIcon == null) {
            search_searchIcon = ContextCompat.getDrawable(getContext(), R.mipmap.icon_search);
        }
        if (search_deleteIcon == null) {
            search_deleteIcon = ContextCompat.getDrawable(getContext(), R.mipmap.icon_back_delete);
        }

        search_hintText = array.getString(R.styleable.CToolBar_c_search_hint_text);
        search_text = array.getString(R.styleable.CToolBar_c_search_text);
        search_textColor = array.getColor(R.styleable.CToolBar_c_search_text_color, DEFAULT_TEXT_COLOR);
        search_hitTextColor = array.getColor(R.styleable.CToolBar_c_search_hint_text_color, DEFAULT_TEXT_COLOR);
        search_textSize = array.getDimensionPixelSize(R.styleable.CToolBar_c_search_text_size, dip2px(getContext(), 14));
        search_enableEdit = array.getBoolean(R.styleable.CToolBar_c_search_enable_edit, true);
        search_textPaddingLR = array.getDimensionPixelOffset(R.styleable.CToolBar_c_search_text_padding_left_right, dip2px(getContext(), 5));
        search_searchGravity = array.getInt(R.styleable.CToolBar_c_search_gravity, 1);
    }

    private void adjustImageView(ImageView v, ImageViewSettings settings) {
        if (v == null || settings == null) return;
        if (settings.iconResId > 0) {
            v.setImageResource(settings.iconResId);
        }
        v.setPadding(settings.paddingLeft, settings.paddingTop, settings.paddingRight, settings.paddingBottom);
        v.setAlpha(settings.alpha);
        v.setVisibility(settings.isShow ? VISIBLE : GONE);
        //设置icon颜色(颜色透明时，不改变icon颜色)
        if (settings.iconColor != Color.TRANSPARENT) {
            v.setColorFilter(settings.iconColor, PorterDuff.Mode.SRC_IN);
        }

    }


    /**
     * 状态栏高度标识位
     */
    public static final String STATUS_BAR_HEIGHT = "status_bar_height";
    /**
     * 导航栏竖屏高度标识位
     */
    public static final String NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    /**
     * 导航栏横屏高度标识位
     */
    public static final String NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";

    /**
     * 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context, STATUS_BAR_HEIGHT);
    }

    private static int getInternalDimensionSize(Context context, String key) {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId);

                if (sizeTwo >= sizeOne) {
                    return sizeTwo;
                } else {
                    float densityOne = context.getResources().getDisplayMetrics().density;
                    float densityTwo = Resources.getSystem().getDisplayMetrics().density;
                    float f = sizeOne * densityTwo / densityOne;
                    return (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
                }
            }
        } catch (Exception ignored) {
            return 0;
        }
        return result;
    }


}
