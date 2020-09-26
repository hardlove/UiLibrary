package com.hardlove.wallpaper.service;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.io.IOException;


/**
 * Author：CL
 * 日期:2020/9/24
 * 说明：视频动态壁纸WallpaperService
 **/
public class VideoWallpaperService extends WallpaperService {
    private static final String TAG = "VideoWallpaperService";
    public static final String VIDEO_PARAMS_CONTROL_ACTION = "com.corloso.videowallpager";
    public static final String ACTION = "action_video_wallpaper_service";
    private static final String VIDEO_WALLPAPER_PATH = "video_wallpaper_path";
    public static final int ACTION_VOICE_SILENCE = 0x101;
    public static final int ACTION_VOICE_NORMAL = 0x102;

    private String sVideoPath;

    /**
     * 设置静音
     *
     * @param context
     */
    public static void setVoiceSilence(@NonNull Context context) {
        Intent intent = new Intent(VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(ACTION, ACTION_VOICE_SILENCE);
        context.sendBroadcast(intent);
    }

    /**
     * 设置有声音
     *
     * @param context
     */
    public static void setVoiceNormal(@NonNull Context context) {
        Intent intent = new Intent(VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(ACTION, ACTION_VOICE_NORMAL);
        context.sendBroadcast(intent);
    }

    /**
     * 设置视频动态壁纸
     *
     * @param context
     */
    public static void setToWallPaper(@NonNull Context context, @NonNull String videoPath) {
        try {
            WallpaperManager.getInstance(context).clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*此处跨进程访问数据，使用Context.MODE_MULTI_PROCESS*/
        context.getApplicationContext().getSharedPreferences(TAG, Context.MODE_MULTI_PROCESS).edit().putString(VIDEO_WALLPAPER_PATH, videoPath).apply();
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, VideoWallpaperService.class));
        context.startActivity(intent);
    }


    @Override
    public Engine onCreateEngine() {
        return new VideoWallpaperEngine();
    }

    class VideoWallpaperEngine extends Engine {
        private MediaPlayer mMediaPlayer;
        private BroadcastReceiver mVideoVoiceControlReceiver;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            IntentFilter intentFilter = new IntentFilter(VIDEO_PARAMS_CONTROL_ACTION);
            mVideoVoiceControlReceiver = new VideoWallpaperEngine.VideoVoiceControlReceiver();
            registerReceiver(mVideoVoiceControlReceiver, intentFilter);
        }

        @Override
        public void onDestroy() {
            unregisterReceiver(mVideoVoiceControlReceiver);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (mMediaPlayer != null) {
                if (visible) {
                    mMediaPlayer.start();
                } else {
                    mMediaPlayer.pause();
                }
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            /*此处跨进程访问数据，使用Context.MODE_MULTI_PROCESS*/
            sVideoPath = getApplicationContext().getSharedPreferences(TAG, Context.MODE_MULTI_PROCESS).getString(VIDEO_WALLPAPER_PATH, null);
            Log.d(TAG, "视频壁纸URL:" + sVideoPath);
            if (TextUtils.isEmpty(sVideoPath)) {
                throw new NullPointerException("videoPath must not be null !");
            } else {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setSurface(holder.getSurface());

                try {
                    mMediaPlayer.setDataSource(sVideoPath);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.setVolume(0f, 0f);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            if (mMediaPlayer != null) {
                mMediaPlayer.setSurface(holder.getSurface());
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        class VideoVoiceControlReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                int action = intent.getIntExtra(ACTION, -1);
                switch (action) {
                    case ACTION_VOICE_NORMAL:
                        if (mMediaPlayer != null) {
                            mMediaPlayer.setVolume(1.0f, 1.0f);
                        }
                        break;
                    case ACTION_VOICE_SILENCE:
                        if (mMediaPlayer != null) {
                            mMediaPlayer.setVolume(0, 0);
                        }
                        break;
                }
            }
        }
    }

}
