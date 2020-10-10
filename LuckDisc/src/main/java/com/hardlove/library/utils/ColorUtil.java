package com.hardlove.library.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;

import java.util.Random;

/**
 * Created by Vector
 * on 2017/6/29 0029.
 */

public class ColorUtil {
    private static final String TAG = "ColorUtil";

    /**
     * 颜色选择器
     *
     * @param pressedColor 按下的颜色
     * @param normalColor  正常的颜色
     * @return 颜色选择器
     */
    public static ColorStateList getColorStateList(int pressedColor, int normalColor) {
        //其他状态默认为白色
        return new ColorStateList(
                new int[][]{{android.R.attr.state_enabled, android.R.attr.state_pressed}, {android.R.attr.state_enabled}, {}},
                new int[]{pressedColor, normalColor, Color.WHITE});
    }

    public static ColorStateList getColorStateList(int tintColor) {
        //其他状态默认为白色
        return ColorStateList.valueOf(tintColor);
    }


    /**
     * 加深颜色
     *
     * @param color 原色
     * @return 加深后的
     */
    public static int colorDeep(int color) {

        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        float ratio = 0.8F;

        red = (int) (red * ratio);
        green = (int) (green * ratio);
        blue = (int) (blue * ratio);

        return Color.argb(alpha, red, green, blue);
    }

    /**
     * @param color 背景颜色
     * @return 前景色是否深色
     */
    public static boolean isTextColorDark(int color) {
        float a = (Color.red(color) * 0.299f + Color.green(color) * 0.587f + Color.blue(color) * 0.114f);
        return a > 180;
    }

    /**
     * 按条件的到随机颜色
     *
     * @param alpha 透明
     * @param lower 下边界
     * @param upper 上边界
     * @return 颜色值
     */

    public static int getRandomColor(int alpha, int lower, int upper) {
        return new RandomColor(alpha, lower, upper).getColor();
    }

    /**
     * @return 获取随机色
     */
    public static int getRandomColor() {
        return new RandomColor(255, 80, 200).getColor();
    }



    /**
     * 随机颜色
     */
    public static class RandomColor {
        int alpha;
        int lower;
        int upper;

        public RandomColor(int alpha, int lower, int upper) {
            if (upper <= lower) {
                throw new IllegalArgumentException("must be lower < upper");
            }
            setAlpha(alpha);
            setLower(lower);
            setUpper(upper);
        }

        public int getColor() {

            //随机数是前闭  后开

            int red = getLower() + new Random().nextInt(getUpper() - getLower() + 1);
            int green = getLower() + new Random().nextInt(getUpper() - getLower() + 1);
            int blue = getLower() + new Random().nextInt(getUpper() - getLower() + 1);


            return Color.argb(getAlpha(), red, green, blue);
        }

        public int getAlpha() {
            return alpha;
        }

        public void setAlpha(int alpha) {
            if (alpha > 255) alpha = 255;
            if (alpha < 0) alpha = 0;
            this.alpha = alpha;
        }

        public int getLower() {
            return lower;
        }

        public void setLower(int lower) {
            if (lower < 0) lower = 0;
            this.lower = lower;
        }

        public int getUpper() {
            return upper;
        }

        public void setUpper(int upper) {
            if (upper > 255) upper = 255;
            this.upper = upper;
        }
    }

    /**
     *  替换color 的 alpha
     *
     * @param alpha
     * @param color
     * @return
     */
    public static int getAlphaColor(@FloatRange(from = 0, to = 1) float alpha, String color) {
        int c = Color.parseColor(color);
        return argb((int) (alpha * 255 + 0.5f), Color.red(c), Color.green(c), Color.blue(c));
    }

    /**
     * 替换color 的 alpha
     *
     * @param alpha
     * @param color
     * @return
     */

    public static int getAlphaColor(@FloatRange(from = 0, to = 1) float alpha, @ColorInt int color) {
        int result = argb((int) (alpha * 255 + 0.5f), Color.red(color), Color.green(color), Color.blue(color));
//        MLogUtil.d(TAG, "原始color：" + Integer.toHexString(color) + "'  result:" + Integer.toHexString(result) +
//                " r:" + Color.red(color) + "  g:" + Color.green(color) + "  b:" + Color.blue(color) + "  alpha:" + alpha);
        return result;
    }

    public static int argb(@FloatRange(from = 0, to = 1) float alpha,
                           @FloatRange(from = 0, to = 1) float red,
                           @FloatRange(from = 0, to = 1) float green,
                           @FloatRange(from = 0, to = 1) float blue) {
        return ((int) (alpha * 255.0f + 0.5f) << 24) |
                ((int) (red * 255.0f + 0.5f) << 16) |
                ((int) (green * 255.0f + 0.5f) << 8) |
                (int) (blue * 255.0f + 0.5f);
    }

    public static int argb(@IntRange(from = 0, to = 255) int alpha,
                           @IntRange(from = 0, to = 255) int red,
                           @IntRange(from = 0, to = 255) int green,
                           @IntRange(from = 0, to = 255) int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }


    /**
     * 判定颜色是否相似
     * Every RGB color consists three components: red, green and blue. That's why can we put 2 colors in a 3D coordinate
     * system and calculate distance between them. When distance is lower than COLOR_DIFF_THRESHOLD it means that these
     * colors are similar.
     * @see <a href="https://www.engineeringtoolbox.com/distance-relationship-between-two-points-d_1854.html">
     *     Distance between 2 points in 3D
     *     </a>
     * @return true for similar colors
     */
    private static final double COLOR_DIFF_THRESHOLD = 30.0;//色差阈值
    public static boolean colorSimilarCheck(int color1, int color2) {
        double colorDiff = Math.sqrt(Math.pow(Color.red(color1) - Color.red(color2), 2) +
                        Math.pow(Color.green(color1) - Color.green(color2), 2) +
                        Math.pow(Color.blue(color1) - Color.blue(color2), 2)
        );
        return colorDiff < COLOR_DIFF_THRESHOLD;
    }

}
