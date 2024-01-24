package com.hongwen.hongutils.media;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.hongwen.hongutils.proxy.ProxyFactory;


public class MediaPlayerService extends Service {
    private IMediaPlayer musicManager;
    private MyBinder binder;

    public MediaPlayerService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicManager = new MusicManager();
        binder = new MyBinder(musicManager);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicManager.release();
        musicManager = null;
        binder = null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    private static class MyBinder extends Binder {
        private final IMediaPlayer iMediaPlayer;

        public MyBinder(IMediaPlayer musicManager) {
            iMediaPlayer = (IMediaPlayer) new ProxyFactory(musicManager).getProxyInstance();
        }

        public IMediaPlayer getIMediaPlayer() {
            return iMediaPlayer;
        }

    }

}
