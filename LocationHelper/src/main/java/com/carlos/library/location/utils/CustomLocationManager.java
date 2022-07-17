package com.carlos.library.location.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import com.carlos.library.location.InitProvider;
import com.carlos.library.location.XLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 地位服务管理者
 * <p>
 * 1）GPS_PROVIDER：通过 GPS 来获取地理位置的经纬度信息； 优点：获取地理位置信息精确度高； 缺点：只能在户外使用，获取经纬度信息耗时，耗电；
 * <p>
 * （2）NETWORK_PROVIDER：通过移动网络的基站或者 Wi-Fi 来获取地理位置； 优点：只要有网络，就可以快速定位，室内室外都可； 缺点：精确度不高；
 * <p>
 * （3）PASSIVE_PROVIDER：被动接收更新地理位置信息，而不用自己请求地理位置信息。
 * <p>
 * PASSIVE_PROVIDER 返回的位置是通过其他 providers 产生的，可以查询 getProvider() 方法决定位置更新的由来，需要 ACCESS_FINE_LOCATION 权限，但是如果未启用 GPS，则此 provider 可能只返回粗略位置匹配；
 * <p>
 * （4）FUSED_PROVIDER：这个本来已经被废弃了，但是目前在Android12（即android api 31）上又重新使用了起来，但是它依赖GMS，所以国内暂时无法使用。
 */
public class CustomLocationManager {
    private static CustomLocationManager instance;
    private final Context context;
    private LocationManager locationManager;

    private CustomLocationManager() {
        context = InitProvider.getAppContext();
        init();
    }

    public static CustomLocationManager getInstance() {
        if (instance == null) {
            synchronized (CustomLocationManager.class) {
                if (instance == null) {
                    instance = new CustomLocationManager();
                }
            }
        }
        return instance;
    }

    HandlerThread mHandlerThread = new HandlerThread(this.getClass().getSimpleName());

    /*初始化服务*/
    private void init() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "获取定位服务失败！" + e.getLocalizedMessage());
        }
        mHandlerThread.start();

    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                XLocation xLocation = new XLocation(location);
                //该方法地理编码是耗时任务，需在非UI线程中执行，避免阻塞UI线程
                xLocation.doGeocoder();

                mHandler.post(() -> {
                    //在主线程中去回调
                    notifySucceed(xLocation);
                });


            } else {
                mHandler.post(() -> {
                    //在主线程中去回调
                    notifyFailed(-1, "定位信息返回空");
                });
            }

            // 如果没有观察者注册了持久性监听位置变化监听，就不需要停止定位服务
            if (listeners == null || listeners.size() == 0) {
                //停止定位
                stopLocation();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled~~~~~~provider:" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled~~~~~~provider:" + provider);
        }
    };


    /*开始定位服务*/
    private void startLocation() {
        boolean enabled = LocationManagerCompat.isLocationEnabled(locationManager);
        if (!enabled) {
            //1. 通知查询当前位置失败
            notifyFailed(1, "未开启GPS定位");
            return;
        }


        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        //如果设置要求速度，那么就可以用:
        //criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH)来设置速度的精度；
        //Criteria.ACCURACY_HIGH:精度高，误差在100米内
        //ACCURACY_MEDIUM精度中等，误差在100-500米之间
        //ACCURACY_LOW精度低，误差大于500米
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        //Criteria.POWER_LOW:耗电低，Criteria.POWER_MEDIUM:中度耗电
        //Criteria.POWER_HIGH：耗电高，但是精确度也高
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String bestProvider = locationManager.getBestProvider(criteria, true);
        /**
         * gps定位：java.lang.SecurityException: "gps" location provider requires ACCESS_FINE_LOCATION permission. 需要两个权限
         * network定位：只需要ACCESS_COARSE_LOCATION即可
         */
        if (LocationManager.GPS_PROVIDER.equals(bestProvider)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //需要权限才能调用
                notifyFailed(2, "未开启定位权限");
                return;
            }
        } else if (LocationManager.NETWORK_PROVIDER.equals(bestProvider)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //需要权限才能调用
                notifyFailed(2, "未开启定位权限");
                return;
            }
        }
        if (bestProvider == null) {
            notifyFailed(3, "你的设备当前不支持定位，请检查网络或GPS定位是否开启");
            return;
        }

//        locationManager.requestLocationUpdates(bestProvider, 1000 * 10, 0, locationListener);
        //在异步线程mHandlerThread中回调
        locationManager.requestLocationUpdates(bestProvider, 1000 * 10, 0, locationListener, mHandlerThread.getLooper());
    }


    private void notifySucceed(XLocation xLocation) {
        //1. 通知查询当前位置成功
        if (singleQueryCallBacks != null && singleQueryCallBacks.size() > 0) {
            for (OnResultCallBack callBack : singleQueryCallBacks) {
                callBack.onSucceed(xLocation);
            }
            //清空所有callback，避免多次回调
            singleQueryCallBacks.clear();
        }

        //2.通知持久监听器位置变更
        if (listeners != null && listeners.size() > 0) {
            for (OnResultCallBack callBack : listeners) {
                callBack.onSucceed(xLocation);
            }
        }
    }

    private void notifyFailed(int code, String msg) {
        if (singleQueryCallBacks != null && singleQueryCallBacks.size() > 0) {
            for (OnResultCallBack queryCallBack : singleQueryCallBacks) {
                queryCallBack.onFailed(code, msg);
            }
            //清空所有callback，避免多次回调
            singleQueryCallBacks.clear();
        }
        //持久监听
        if (listeners != null && listeners.size() > 0) {
            for (OnResultCallBack queryCallBack : listeners) {
                queryCallBack.onFailed(code, msg);
            }
        }
    }

    /*停止定位服务*/
    private void stopLocation() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            mHandler.removeCallbacks(null);
            Log.d(TAG, "停止定位服务。。。。");
        }

    }


    private static final String TAG = "CustomLocationManager";

    List<OnResultCallBack> singleQueryCallBacks = new ArrayList<>();
    List<OnResultCallBack> listeners = new ArrayList<>();


    /*请求定位当前最新的地理位置(只定位一次)*/
    public void queryCurrentLocation(OnResultCallBack onLocationCallBack) {
        singleQueryCallBacks.add(onLocationCallBack);
        startLocation();
    }


    /*注册地理位置变化监听器*/
    public void registerLocationChangerListener(OnResultCallBack onResultCallBack) {
        listeners.add(onResultCallBack);
        startLocation();
    }

    /*移除地理位置变化监听器*/
    public void unRegisterListener(OnResultCallBack onResultCallBack) {
        if (listeners != null && onResultCallBack != null) {
            listeners.remove(onResultCallBack);
        }
    }


    /*请求查询当前地理位置回调*/
    public interface OnResultCallBack {
        void onSucceed(XLocation location);

        void onFailed(int errorCode, String errorMsg);
    }

}
