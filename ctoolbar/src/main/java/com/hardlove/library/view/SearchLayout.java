package com.hardlove.library.view;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hardlove.library.view.ctoobar.R;

/*搜索*/
public class SearchLayout extends LinearLayout {
    private static final int GRAVITY_START = 1;
    private static final int GRAVITY_CENTER = 2;
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
    private String hintText;
    private String text;
    private int textColor;
    private int hitTextColor;
    private int textSize;
    private boolean enableEdit;
    private int textPaddingLR;
    private int searchGravity;
    private ImageView searchIconView;
    private EditText editText;
    private ImageView deleteIconView;


    public SearchLayout(Context context) {
        this(context, null);
    }

    public SearchLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setGravity(Gravity.CENTER);

        initAttrs(context, attrs, defStyleAttr);
        initView(context, attrs);

    }


    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SearchLayout, defStyleAttr, 0);

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

        hintText = array.getString(R.styleable.SearchLayout_c_search_hint_text);
        text = array.getString(R.styleable.SearchLayout_c_search_text);
        textColor = array.getColor(R.styleable.SearchLayout_c_search_text_color, defColor);
        hitTextColor = array.getColor(R.styleable.SearchLayout_c_search_hint_text_color, defColor);
        textSize = array.getDimensionPixelSize(R.styleable.SearchLayout_c_search_text_size, dip2px(context, 14));
        enableEdit = array.getBoolean(R.styleable.SearchLayout_c_search_enable_edit, true);
        textPaddingLR = array.getDimensionPixelOffset(R.styleable.SearchLayout_c_search_text_padding_left_right, dip2px(context, 5));
        searchGravity = array.getInt(R.styleable.SearchLayout_c_search_gravity, 1);

        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    private void initView(Context context, AttributeSet attrs) {
        setBackground(getStrokeRectDrawable(cornerRadius, solidColor, strokeColor, strokeWidth));

        searchIconView = new ImageView(context);
        editText = new EditText(context);
        deleteIconView = new ImageView(context);

        addView(searchIconView, 0, new LayoutParams(searchIconSize, searchIconSize));
        LayoutParams params = null;
        if (searchGravity == GRAVITY_START) {
            params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
        } else if (searchGravity == GRAVITY_CENTER) {
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

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
        if (TextUtils.isEmpty(hintText)) {
            hintText = getContext().getString(R.string.search);
        }
        editText.setHint(hintText);
        editText.setHintTextColor(hitTextColor);
        if (text != null) {
            editText.setText(text);
            editText.setSelection(editText.getEditableText().length());
        }
        editText.setTextColor(textColor);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        editText.setBackgroundColor(0x00000000);
        editText.setSingleLine();
        editText.setEllipsize(TextUtils.TruncateAt.END);
        setEnableEdit(enableEdit);
        setTextPaddingLR(textPaddingLR);

    }

    public ImageView getSearchIconView() {
        return searchIconView;
    }

    public EditText getEditText() {
        return editText;
    }

    public ImageView getDeleteIconView() {
        return deleteIconView;
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

    public void setSolidColor(int searchSolidColor) {
        this.solidColor = searchSolidColor;
        setBackground(getStrokeRectDrawable(this.cornerRadius, this.solidColor, this.strokeColor, this.strokeWidth));

    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        setBackground(getStrokeRectDrawable(this.cornerRadius, this.solidColor, this.strokeColor, this.strokeWidth));
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setBackground(getStrokeRectDrawable(this.cornerRadius, this.solidColor, this.strokeColor, this.strokeWidth));
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        setBackground(getStrokeRectDrawable(this.cornerRadius, this.solidColor, this.strokeColor, this.strokeWidth));
    }

    public void setSearchIconSize(int searchIconSize) {
        this.searchIconSize = searchIconSize;
        ViewGroup.LayoutParams layoutParams = searchIconView.getLayoutParams();
        layoutParams.width = this.searchIconSize;
        layoutParams.height = this.searchIconSize;
        searchIconView.setLayoutParams(layoutParams);
    }

    public void setDeleteIconSize(int deleteIconSize) {
        this.deleteIconSize = deleteIconSize;
        ViewGroup.LayoutParams layoutParams = deleteIconView.getLayoutParams();
        layoutParams.width = this.searchIconSize;
        layoutParams.height = this.searchIconSize;
        deleteIconView.setLayoutParams(layoutParams);
    }

    public void setSearchIcon(Drawable searchIcon) {
        this.searchIcon = searchIcon;
        searchIconView.setImageDrawable(this.searchIcon);
    }

    public void setDeleteIcon(Drawable deleteIcon) {
        this.deleteIcon = deleteIcon;
        deleteIconView.setImageDrawable(this.deleteIcon);
    }

    public void setSearchGravity(int searchGravity) {
        this.searchGravity = searchGravity;
        LinearLayout.LayoutParams params = (LayoutParams) editText.getLayoutParams();
        if (searchGravity == GRAVITY_START) {
            params.width = 0;
            params.weight = 1;
        } else if (searchGravity == GRAVITY_CENTER) {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.weight = 0;
        } else {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.weight = 0;
        }
        editText.setLayoutParams(params);
    }

    public void setEnableEdit(boolean enableEdit) {
        this.enableEdit = enableEdit;
        editText.setEnabled(this.enableEdit);
        if (!enableEdit) {
            editText.setCursorVisible(false);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
        } else {
            editText.setCursorVisible(true);
        }
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.textSize);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        editText.setTextColor(this.textColor);
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
        editText.setHint(this.hintText);
    }

    public void setText(String text) {
        this.text = text;
        editText.setText(this.text);
    }

    public void setHintTextColor(int hitTextColor) {
        this.hitTextColor = hitTextColor;
        editText.setHintTextColor(this.hitTextColor);
    }

    public void setTextPaddingLR(int textPaddingLR) {
        this.textPaddingLR = textPaddingLR;
        editText.setPadding(this.textPaddingLR, 0, this.textPaddingLR, 0);
    }

    public void setSearchIconColor(int searchIconColor) {
        this.searchIconColor = searchIconColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this.searchIconColor != Integer.MIN_VALUE) {
            searchIconView.setImageTintList(ColorStateList.valueOf(this.searchIconColor));
        }
    }

    public void setDeleteIconColor(int deleteIconColor) {
        this.deleteIconColor = deleteIconColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this.deleteIconColor != Integer.MIN_VALUE) {
            deleteIconView.setImageTintList(ColorStateList.valueOf(this.deleteIconColor));
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!enableEdit) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

}
