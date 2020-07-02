package com.hardlove.library.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

/*搜索*/
public class SearchLayout extends LinearLayout {
    int cornerRadius;
    int solidColor = Color.parseColor("#eeeeee");
    int strokeColor = Color.parseColor("#ff0000");
    int strokeWidth = 2;




    private int measuredHeight;
    private int measuredWidth;

    public SearchLayout(Context context) {
        super(context, null);
    }

    public SearchLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public SearchLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredHeight = getMeasuredHeight();
        measuredWidth = getMeasuredWidth();

        cornerRadius = measuredHeight / 2;

        setBackground(getStrokeRectDrawable(cornerRadius, solidColor, strokeColor, strokeWidth));
    }

    private void initView(Context context, AttributeSet attrs) {

        ImageView searchIcon = new ImageView(context);
        EditText editText = new EditText(context);
        ImageView deleteIcon = new ImageView(context);

        addView(searchIcon, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        addView(editText, 1,params);
        addView(deleteIcon, 2);
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
}
