package com.hongwen.hongutils.flash;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

/**
 * ==================================================
 * Author：CL
 * 日期:2023/9/26
 * 说明：闪光灯工具类
 * 需要申请        <uses-permission android:name="android.permission.FLASHLIGHT" /> 权限
 * ==================================================
 **/
public class FlashUtils {
    private CameraManager manager;
    private Camera mCamera = null;
    private final Context context;
    private boolean isOpen = false;

    long[][] times = new long[][]{{100, 100, 100}, {1000, 1000, 1000}, {100, 100, 100}};

    private boolean isOpenSos = false;
    public FlashUtils(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        }
        this.context = context.getApplicationContext();
    }

    //打开手电筒
    public void open() {
        if (isOpen) {//如果已经是打开状态，不需要打开
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                manager.setTorchMode("0", true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PackageManager packageManager = context.getPackageManager();
            FeatureInfo[] features = packageManager.getSystemAvailableFeatures();
            for (FeatureInfo featureInfo : features) {
                if (PackageManager.FEATURE_CAMERA_FLASH.equals(featureInfo.name)) { // 判断设备是否支持闪光灯
                    if (null == mCamera) {
                        mCamera = Camera.open();
                    }
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(parameters);
                    mCamera.startPreview();
                }
            }
        }
        isOpen = true;
    }

    //关闭手电筒
    public void close() {
        if (!isOpen) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                manager.setTorchMode("0", false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
        isOpen = false;
    }


    public void startSOS() {
        isOpenSos = true;
        close();
        if (isOpenSos) {
            sos();
        }
    }

    public void stopSOS() {
        isOpenSos = false;
        close();
    }

    //关闭手电筒
    private void sos() {
        new Thread(() -> {
            while (isOpenSos) {

                for (long[] time : times) {
                    for (long pwd : time) {
                        if (!isOpenSos) {
                            return;
                        }
                        open();
                        try {
                            Thread.sleep(pwd);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        close();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //改变手电筒状态
    public void toggle() {
        if (isOpen) {
            close();
        } else {
            open();
        }
    }

    public boolean isFlashLightOpen() {
        return isOpen;
    }

    public boolean isSosOpen() {
        return isOpenSos;
    }
}
