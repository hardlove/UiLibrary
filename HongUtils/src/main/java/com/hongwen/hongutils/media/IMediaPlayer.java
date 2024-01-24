package com.hongwen.hongutils.media;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.widget.SeekBar;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileDescriptor;
import java.util.Map;

public interface IMediaPlayer {
    void play(@NonNull String url);
    void play(@NonNull String url, @Nullable Map<String, String> headers);
    void play(@NonNull Uri uri);
    void play(@NonNull Uri uri, @Nullable Map<String, String> headers);
    void play(FileDescriptor descriptor);
    void play(AssetFileDescriptor descriptor);
    void pause();
    void resume();
    void stop();
    void seekTo(@FloatRange(from = 0.0, to = 1.0) float percent);
    void release();
    void setSeekBar(@Nullable SeekBar seekBar);

}
