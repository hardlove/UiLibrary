package com.hardlove.library.view;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hardlove.library.view.ctoobar.R;

/*搜索*/
public class SearchLayout extends LinearLayout {
    int cornerRadius;
    int solidColor = Color.parseColor("#eeeeee");
    int strokeColor = Color.parseColor("#ff0000");
    int defColor = Color.parseColor("#333333");
    int strokeWidth = 2;


    private int searchIconSize;
    private int deleteIconSize;
    private int searchIconColor;
    private int deleteIconColor;
    private Drawable searchIcon;
    private Drawable deleteIcon;
    private String tintText;
    private String text;
    private int textColor;
    private int hitTextColor;
    private int textSize;
    private boolean enableEdit;
    private int textPaddingLR;


    public SearchLayout(Context context) {
        this(context, null);
    }

    public SearchLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setGravity(Gravity.CENTER_VERTICAL);

        initAttrs(context, attrs, defStyleAttr);
        initView(context, attrs);

    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SearchLayout, defStyleAttr, 0);

//          <attr name="c_search_layout_radius" format="dimension|reference"/>
//        <attr name="c_search_layout_solid_color" format="color|reference"/>
//        <attr name="c_search_layout_stroke_color" format="color|reference"/>
//        <attr name="c_search_layout_stroke_width" format="dimension|reference"/>
//
//        <attr name="c_search_icon_size" format="dimension|reference"/>
//        <attr name="c_search_icon_color" format="color|reference"/>
//        <attr name="c_search_icon" format="reference"/>
//
//        <attr name="c_search_delete_icon_size" format="dimension|reference"/>
//        <attr name="c_search_delete_icon_color" format="color|reference"/>
//        <attr name="c_search_delete_icon" format="reference"/>
//
//        <attr name="c_search_hint_text" format="string|reference" />
//        <attr name="c_search_hint_text_color" format="color|reference" />
//        <attr name="c_search_text_color" format="color|reference" />
//        <attr name="c_search_text_size" format="dimension|reference" />
        cornerRadius = array.getDimensionPixelOffset(R.styleable.SearchLayout_c_search_layout_radius, dip2px(context, 4));
        strokeWidth = array.getDimensionPixelOffset(R.styleable.SearchLayout_c_search_layout_stroke_width, dip2px(context, 1));
        solidColor = array.getColor(R.styleable.SearchLayout_c_search_layout_solid_color, solidColor);
        strokeColor = array.getColor(R.styleable.SearchLayout_c_search_layout_stroke_color, strokeColor);

        searchIconSize = array.getDimensionPixelSize(R.styleable.SearchLayout_c_search_icon_size, dip2px(context, 30));
        deleteIconSize = array.getDimensionPixelSize(R.styleable.SearchLayout_c_search_delete_icon_size, dip2px(context, 30));
        searchIconColor = array.getColor(R.styleable.SearchLayout_c_search_icon_color, Integer.MIN_VALUE);
        deleteIconColor = array.getColor(R.styleable.SearchLayout_c_search_delete_icon_color, Integer.MIN_VALUE);
        searchIcon = array.getDrawable(R.styleable.SearchLayout_c_search_icon);
        deleteIcon = array.getDrawable(R.styleable.SearchLayout_c_search_delete_icon);

        tintText = array.getString(R.styleable.SearchLayout_c_search_hint_text);
        text = array.getString(R.styleable.SearchLayout_c_search_text);
        textColor = array.getColor(R.styleable.SearchLayout_c_search_text_color, defColor);
        hitTextColor = array.getColor(R.styleable.SearchLayout_c_search_hint_text_color, defColor);
        textSize = array.getDimensionPixelSize(R.styleable.SearchLayout_c_search_text_size, dip2px(context, 14));
        enableEdit = array.getBoolean(R.styleable.SearchLayout_c_search_enable_edit, false);
        textPaddingLR = array.getDimensionPixelOffset(R.styleable.SearchLayout_c_search_text_padding_left, dip2px(context, 5));

        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    private void initView(Context context, AttributeSet attrs) {
        setBackground(getStrokeRectDrawable(cornerRadius, solidColor, strokeColor, strokeWidth));

        ImageView searchIconView = new ImageView(context);
        EditText editText = new EditText(context);
        ImageView deleteIconView = new ImageView(context);

        addView(searchIconView, 0, new LayoutParams(searchIconSize, searchIconSize));
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        addView(editText, 1, params);
        addView(deleteIconView, 2, new LayoutParams(deleteIconSize, deleteIconSize));

        searchIconView.setImageDrawable(searchIcon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && searchIconColor != Integer.MIN_VALUE) {
            searchIconView.setImageTintList(ColorStateList.valueOf(searchIconColor));
        }


        deleteIconView.setImageDrawable(deleteIcon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && deleteIconColor != Integer.MIN_VALUE) {
            deleteIconView.setImageTintList(ColorStateList.valueOf(deleteIconColor));
        }
        editText.setHint(tintText);
        editText.setHintTextColor(hitTextColor);
        if (text != null) {
            editText.setText(text);
            editText.setSelection(editText.getEditableText().length());
        }
        editText.setTextColor(textColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        editText.setBackground(null);
        editText.setEnabled(enableEdit);
        editText.setPadding(textPaddingLR, 0, textPaddingLR, 0);
    }

    /**
     * 得到空心的效果，一般作为默认的效果
     *
     * @param cornerRadius 圆角半径
     * @param solidColor   实心颜色
     * @param strokeColor  边框颜色
     * @param strokeWidth  边框宽度
     * @return 得到空心效果
     */
    private GradientDrawable getStrokeRectDrawable(int cornerRadius, int solidColor, int strokeColor, int strokeWidth) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        gradientDrawable.setColor(solidColor);
        gradientDrawable.setCornerRadius(cornerRadius);
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        return gradientDrawable;

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
}
