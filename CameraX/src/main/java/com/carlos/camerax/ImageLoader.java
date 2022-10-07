package com.carlos.camerax;

import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.NonNull;

public interface ImageLoader {
    void load(@NonNull ImageView iv, @NonNull Uri uri);
}
