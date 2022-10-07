package com.carlos.camerax;

import android.net.Uri;
import android.widget.ImageView;

public interface ImageLoader {
    void load(ImageView iv, Uri uri);
}
