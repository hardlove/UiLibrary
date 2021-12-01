package com.ashokvarma.bottomnavigation.imageloader;

import android.widget.ImageView;

public class ImageLoaderManger implements ImageLoader {

    private ImageLoader imageLoader;
    private static ImageLoaderManger instance;
    private static boolean isInit;


    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public static void init(ImageLoader imageLoader) {
        getInstance().setImageLoader(imageLoader);
        isInit = true;
    }

    public static ImageLoaderManger getInstance() {
        if (instance == null) {
            synchronized (ImageLoaderManger.class) {
                if (instance == null) {
                    instance = new ImageLoaderManger();
                }
            }
        }
        return instance;
    }

    @Override
    public void load(ImageView iv, String url, int error) {
        if (!isInit) {
            throw new NullPointerException("请调用BottomNavigationBar.setImageLoader()初始化imageloader");
        }
        imageLoader.load(iv, url, error);
    }
}
