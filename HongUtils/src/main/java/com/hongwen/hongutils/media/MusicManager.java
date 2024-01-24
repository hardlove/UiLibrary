package com.hongwen.hongutils.media;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hongwen.hongutils.provider.InitProvider;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

public class MusicManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, IMediaPlayer {
    private static final String TAG = "MusicManager";
    private MediaPlayer mediaPlayer;
    private boolean initialized;
    private boolean isPrepared;
    private SeekBar seekBar;

    private int duration;
    private Handler handler;

    public MusicManager() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (isPrepared && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    if (seekBar != null) {
                        seekBar.setProgress((int) (seekBar.getMax() * (currentPosition * 1.0f / duration)));
                        switch (msg.what) {
                            case State.Play:
                                handler.sendEmptyMessageDelayed(State.Play, 1000);
                                break;
                            case State.pause:
                                break;
                            case State.stop:
                            case State.completed:
                                handler.removeCallbacksAndMessages(null);
                                break;

                        }


                    }
                }
            }
        };

    }


    private void resetSeekBar() {
        if (seekBar != null) {
            seekBar.setProgress(0);
            seekBar.setSecondaryProgress(0);
        }
    }

    private void setDataSource(@NonNull String path) {
        try {
            mediaPlayer.setDataSource(path);
            this.initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, String.format("音频资源初始化失败。error:%s", e.getMessage()));
        }
    }

    private void setDataSource(FileDescriptor descriptor) {
        try {
            mediaPlayer.setDataSource(descriptor);
            this.initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, String.format("音频资源初始化失败。error:%s", e.getMessage()));
        }
    }


    private void setDataSource(AssetFileDescriptor descriptor) {
        try {
            if (descriptor.getDeclaredLength() < 0) {
                mediaPlayer.setDataSource(descriptor.getFileDescriptor());
            } else {
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getDeclaredLength());
            }
            descriptor.close();
            this.initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, String.format("音频资源初始化失败 error:%s", e.getMessage()));
        }
    }

    private void setDataSource(@NonNull Uri uri, @Nullable Map<String, String> headers) {
        try {
            mediaPlayer.setDataSource(InitProvider.getApplicationContext(), uri, headers);
            this.initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, String.format("音频资源初始化失败 error: %s", e.getMessage()));
        }
    }


    private void prepare() {
        try {
            isPrepared = false;
            mediaPlayer.prepareAsync();
            Log.d(TAG, "开始准备资源...");
        } catch (Exception e) {
            Log.e(TAG, String.format("音频资源加载失败 error:%s", e.getMessage()));
        }
    }

    private void start() {
        if (isPrepared) {
            try {
                mediaPlayer.start();
                handler.sendEmptyMessageDelayed(State.Play, 1000);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, String.format("onBufferingUpdate~~~ percent：%d", percent));
        if (isPrepared && seekBar != null) {
            handler.post(() -> seekBar.setSecondaryProgress(seekBar.getMax() * percent / 100));
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion~~~");
        handler.sendEmptyMessageDelayed(State.completed, 1000);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, String.format("onError~~~ what:%d, extra:%d", what, extra));
        handler.sendEmptyMessageDelayed(State.stop, 1000);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared~~~");
        isPrepared = true;
        //开始播放
        this.start();
        duration = mediaPlayer.getDuration();
    }

    /**
     * 播放音频文件
     *
     * @param url 本地音频文件或是网络音频文件
     */
    @Override
    public void play(@NonNull String url) {
        this.play(url, null);
    }

    @Override
    public void play(@NonNull String url, @Nullable Map<String, String> headers) {
        this.play(Uri.parse(url), headers);
    }

    @Override
    public void play(@NonNull Uri uri) {
        this.play(uri, null);
    }

    @Override
    public void play(@NonNull Uri uri, @Nullable Map<String, String> headers) {
        //重置
        mediaPlayer.reset();
        //初始化
        this.setDataSource(uri, headers);
        //准备
        this.prepare();
    }

    @Override
    public void play(FileDescriptor descriptor) {
        //重置
        mediaPlayer.reset();
        //初始化
        this.setDataSource(descriptor);
        //准备
        this.prepare();
    }

    @Override
    public void play(AssetFileDescriptor descriptor) {
        //重置
        mediaPlayer.reset();
        //初始化
        this.setDataSource(descriptor);
        //准备
        this.prepare();
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        if (initialized && isPrepared) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                handler.sendEmptyMessageDelayed(State.pause, 1000);
                Log.d(TAG, "暂停播放。。。");
            } else {
                Log.d(TAG, "已经暂停。。。");
            }
        } else {
            Log.d(TAG, "资源未准备。。。");
        }

    }

    /**
     * 恢复播放
     */
    @Override
    public void resume() {
        if (initialized && isPrepared) {
            if (!mediaPlayer.isPlaying()) {
                this.start();
            } else {
                Log.d(TAG, "正在播放。。。");
            }
        } else {
            Log.d(TAG, "资源未准备。。。");
        }
    }

    /**
     * 停止播放
     */
    @Override
    public void stop() {
        if (initialized && isPrepared) {
            try {
                isPrepared = false;
                mediaPlayer.stop();
                resetSeekBar();
                handler.sendEmptyMessageDelayed(State.stop, 1000);
                Log.d(TAG, "停止播放。。。");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "资源未准备。。。,不能停止");
        }

    }

    @Override
    public void seekTo(float percent) {
        if (initialized) {
            try {
                mediaPlayer.seekTo((int) (percent * duration));
                Log.d(TAG, String.format("移动到%d 位置", (int) (percent * duration)));
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {
        try {
            mediaPlayer.release();
            resetSeekBar();
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            initialized = false;
            isPrepared = false;
            mediaPlayer = null;
            handler = null;
            seekBar = null;

            instance = null;
        }
    }

    @Override
    public void setSeekBar(@Nullable SeekBar seekBar) {
        this.seekBar = seekBar;
    }


    private static class State {
        private final static int Play = 1;
        private final static int pause = 2;
        private final static int stop = 3;
        private final static int completed = 4;
    }


    private static MusicManager instance;

    public static MusicManager getInstance() {
        if (instance == null) {
            synchronized (MusicManager.class) {
                if (instance == null) {
                    instance = new MusicManager();
                }
            }
        }
        return instance;
    }
}
