package com.ashokvarma.bottomnavigation.imageloader;

import android.widget.ImageView;

import androidx.annotation.DrawableRes;

public interface ImageLoader {
    void load(ImageView iv, String url, @DrawableRes int error);
}
