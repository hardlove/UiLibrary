package com.hardlove.library.bean;

import android.graphics.Bitmap;

import androidx.annotation.ColorInt;

import java.io.Serializable;

/**
 * Author：CL
 * 日期:2020/10/10
 * 说明：转盘item实列
 **/
public class Sector implements Serializable {
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
     */
    private int textSize = 16;
    /**
     * 对应显示的图片
     */
    private transient Bitmap bitmap;
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
