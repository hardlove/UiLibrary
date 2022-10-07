package com.carlos.camera;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

public interface ImageLoader {
    void load(ImageView iv, Uri uri);
}
