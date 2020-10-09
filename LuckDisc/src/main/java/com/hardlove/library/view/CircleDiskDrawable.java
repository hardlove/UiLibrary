package com.hardlove.library.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 转盘实现方式二:
 * 自定义Drawable
 */
public class CircleDiskDrawable extends Drawable {
    /**
     * Draw in its bounds (set via setBounds) respecting optional effects such
     * as alpha (set via setAlpha) and color filter (set via setColorFilter).
     *
     * @param canvas The canvas to draw into
     */
    @Override
    public void draw(@NonNull Canvas canvas) {

    }

    /**
     * Specify an alpha value for the drawable. 0 means fully transparent, and
     * 255 means fully opaque.
     *
     * @param alpha
     */
    @Override
    public void setAlpha(int alpha) {

    }

    /**
     * Specify an optional color filter for the drawable.
     * <p>
     * If a Drawable has a ColorFilter, each output pixel of the Drawable's
     * drawing contents will be modified by the color filter before it is
     * blended onto the render target of a Canvas.
     * </p>
     * <p>
     * Pass {@code null} to remove any existing color filter.
     * </p>
     * <p class="note"><strong>Note:</strong> Setting a non-{@code null} color
     * filter disables {@link #setTintList(ColorStateList) tint}.
     * </p>
     *
     * @param colorFilter The color filter to apply, or {@code null} to remove the
     *                    existing color filter
     */
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    /**
     * Return the opacity/transparency of this Drawable.  The returned value is
     * one of the abstract format constants in
     * {@link PixelFormat}:
     * {@link PixelFormat#UNKNOWN},
     * {@link PixelFormat#TRANSLUCENT},
     * {@link PixelFormat#TRANSPARENT}, or
     * {@link PixelFormat#OPAQUE}.
     *
     * <p>An OPAQUE drawable is one that draws all all content within its bounds, completely
     * covering anything behind the drawable. A TRANSPARENT drawable is one that draws nothing
     * within its bounds, allowing everything behind it to show through. A TRANSLUCENT drawable
     * is a drawable in any other state, where the drawable will draw some, but not all,
     * of the content within its bounds and at least some content behind the drawable will
     * be visible. If the visibility of the drawable's contents cannot be determined, the
     * safest/best return value is TRANSLUCENT.
     *
     * <p>Generally a Drawable should be as conservative as possible with the
     * value it returns.  For example, if it contains multiple child drawables
     * and only shows one of them at a time, if only one of the children is
     * TRANSLUCENT and the others are OPAQUE then TRANSLUCENT should be
     * returned.  You can use the method {@link #resolveOpacity} to perform a
     * standard reduction of two opacities to the appropriate single output.
     *
     * <p>Note that the returned value does not necessarily take into account a
     * custom alpha or color filter that has been applied by the client through
     * the {@link #setAlpha} or {@link #setColorFilter} methods. Some subclasses,
     * such as {@link BitmapDrawable}, {@link ColorDrawable}, and {@link GradientDrawable},
     * do account for the value of {@link #setAlpha}, but the general behavior is dependent
     * upon the implementation of the subclass.
     *
     * @return int The opacity class of the Drawable.
     * @see PixelFormat
     * @deprecated This method is no longer used in graphics optimizations
     */
    @Override
    public int getOpacity() {
        return 0;
    }
}
