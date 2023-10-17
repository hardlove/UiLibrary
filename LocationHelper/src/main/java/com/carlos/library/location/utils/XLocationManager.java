package com.carlos.library.location.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.carlos.library.location.InitProvider;
import com.carlos.library.location.XLocation;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Iterator;
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
public class XLocationManager {
    private final Context context;
    private LocationManager locationManager;

    private XLocationManager() {
        context = InitProvider.getAppContext();
        init();
    }

    public static XLocationManager getInstance() {
        return new XLocationManager();
    }

    HandlerThread mHandlerThread = new HandlerThread(this.getClass().getSimpleName());
    private String mDefaultProvider;
    private boolean mCanUseCoarse = true;//是否可以使用低精度

    public XLocationManager setCanUseCoarse(boolean canUseCoarse) {
        this.mCanUseCoarse = canUseCoarse;
        return this;
    }

    public void setDefaultProvider(String provider) {
        this.mDefaultProvider = provider;
    }

    /*初始化服务*/
    private void init() {
        try {
            locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "获取定位服务失败！" + e.getLocalizedMessage());
        }
        mHandlerThread.start();

    }

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final LocationListener locationListener = new LocationListener() {
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
            if (alwaysQueryCallBacks == null || alwaysQueryCallBacks.size() == 0) {
                //停止定位
                stopLocation();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged~~~~~~provider:" + provider + " status:" + status);
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
            notifyFailed(1, "位置信息开关未开启");
            return;
        }


        String provider = getProvider();

        if (provider == null) {
            notifyFailed(2, "你的设备当前不支持定位，请检查网络或GPS定位是否已开启");
            return;
        }


        if (LocationManager.GPS_PROVIDER.equals(provider) || LocationManager.FUSED_PROVIDER.equals(provider)) {

            if (!isNetworkAvailable(context)) {
                notifyFailed(3, "请检查网络");
                return;
            }
            /**
             * gps定位：java.lang.SecurityException: "gps" location provider requires ACCESS_FINE_LOCATION permission. 需要两个权限
             * network定位：只需要ACCESS_COARSE_LOCATION即可
             */
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //需要权限才能调用
                boolean flagCoarse = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                notifyFailed(4, !flagCoarse ? "未开启定位权限" : "未开启精准定位权限");
                return;
            }
        } else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
            //NETWORK_PROVIDER 使用的是网络基站和 Wi-Fi 网络的信息来进行定位，而不是使用 GPS。因此，只需要获取粗略位置权限（ACCESS_COARSE_LOCATION）来使用 NETWORK_PROVIDER 进行定位。
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //需要权限才能调用
                notifyFailed(5, "未开启定位权限");
                return;
            }
        }
        //在异步线程mHandlerThread中回调
        locationManager.removeUpdates(locationListener);
        locationManager.requestLocationUpdates(provider, 1000 * 10, 0, locationListener, mHandlerThread.getLooper());
        Log.d(TAG, "请求定位 。。。。provider：" + provider);
    }

    private String getProvider() {
        if (!TextUtils.isEmpty(mDefaultProvider)) {
            return mDefaultProvider;
        }


        int accuracy;
        if (mCanUseCoarse) {//可以使用低精度定位,则低精度优先
            accuracy = Criteria.ACCURACY_COARSE;
        } else {//高精度定位
            accuracy = Criteria.ACCURACY_FINE;
        }

        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(accuracy);
        // 设置是否要求速度
        criteria.setSpeedRequired(Criteria.ACCURACY_FINE == accuracy);
        //如果设置要求速度，那么就可以用:criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH)来设置速度的精度；
        if (criteria.isSpeedRequired()) {
            //Criteria.ACCURACY_HIGH:精度高，误差在100米内
            //ACCURACY_MEDIUM精度中等，误差在100-500米之间
            //ACCURACY_LOW精度低，误差大于500米
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        }

        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(true);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        //Criteria.POWER_LOW:耗电低，Criteria.POWER_MEDIUM:中度耗电
        //Criteria.POWER_HIGH：耗电高，但是精确度也高
        criteria.setPowerRequirement(mCanUseCoarse ? Criteria.POWER_LOW : Criteria.POWER_MEDIUM);

        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider == null || !locationManager.isProviderEnabled(bestProvider)) {
            if (mCanUseCoarse) {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    bestProvider = LocationManager.NETWORK_PROVIDER;
                } else {
                    if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                        bestProvider = LocationManager.PASSIVE_PROVIDER;
                    } else {
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            bestProvider = LocationManager.GPS_PROVIDER;
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                if (locationManager.isProviderEnabled(LocationManager.FUSED_PROVIDER)) {
                                    bestProvider = LocationManager.FUSED_PROVIDER;
                                }
                            }
                        }
                    }
                }
            } else {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    bestProvider = LocationManager.GPS_PROVIDER;
                }
            }

        }
        boolean flagFine = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean flagCoarse = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (mCanUseCoarse && isNetworkAvailable(context) && !flagFine && flagCoarse) {
            bestProvider = LocationManager.NETWORK_PROVIDER;
        }
        return bestProvider;
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo netInfo = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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

        //1. 通知查询当前位置成功
        if (lifecycleWraps != null && lifecycleWraps.size() > 0) {
            for (LifecycleWrap wrap : lifecycleWraps) {
                wrap.onResultCallBack.onSucceed(xLocation);
            }
            //清空所有LifecycleWrap，避免多次回调
            lifecycleWraps.clear();
        }

        //2.通知持久监听器位置变更
        if (alwaysQueryCallBacks != null && alwaysQueryCallBacks.size() > 0) {
            for (OnResultCallBack callBack : alwaysQueryCallBacks) {
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
        if (lifecycleWraps != null && lifecycleWraps.size() > 0) {
            for (LifecycleWrap wrap : lifecycleWraps) {
                wrap.onResultCallBack.onFailed(code, msg);
            }
            //清空所有LifecycleWrap，避免多次回调
            lifecycleWraps.clear();
        }
        //持久监听
        if (alwaysQueryCallBacks != null && alwaysQueryCallBacks.size() > 0) {
            for (OnResultCallBack queryCallBack : alwaysQueryCallBacks) {
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


    private static final String TAG = "XLocationManager";

    List<OnResultCallBack> singleQueryCallBacks = new ArrayList<>();
    List<LifecycleWrap> lifecycleWraps = new ArrayList<>();
    List<OnResultCallBack> alwaysQueryCallBacks = new ArrayList<>();


    /**
     * 请求定位当前最新的地理位置(只定位一次)
     * 未绑定生命周期，与public void removeOnResultCallBack(OnResultCallBack onLocationCallBack)配对使用
     */
    public void queryCurrentLocation(OnResultCallBack onLocationCallBack) {
        singleQueryCallBacks.add(onLocationCallBack);
        startLocation();
    }

    /**
     * 与public void queryCurrentLocation(OnResultCallBack onLocationCallBack)配对使用
     */
    public void removeOnResultCallBack(OnResultCallBack onLocationCallBack) {
        if (singleQueryCallBacks.contains(onLocationCallBack)) {
            singleQueryCallBacks.remove(onLocationCallBack);
        }
    }

    /*请求定位当前最新的地理位置(只定位一次)，内部自动处理生命周期，调用者无需关心*/
    public void queryCurrentLocation(@NonNull LifecycleOwner lifecycleOwner, @NonNull OnResultCallBack onLocationCallBack) {
        addLifeCycle(lifecycleOwner, onLocationCallBack);
        startLocation();
    }

    private void addLifeCycle(@NonNull LifecycleOwner lifecycleOwner, @NonNull OnResultCallBack onResultCallBack) {
        LifecycleWrap wrap = new LifecycleWrap(lifecycleWraps, lifecycleOwner, onResultCallBack);
        lifecycleWraps.add(wrap);
    }


    /*注册地理位置变化监听器*/
    public void registerLocationChangerListener(OnResultCallBack onResultCallBack) {
        alwaysQueryCallBacks.add(onResultCallBack);
        startLocation();
    }

    /*移除地理位置变化监听器*/
    public void unRegisterListener(OnResultCallBack onResultCallBack) {
        if (alwaysQueryCallBacks != null && onResultCallBack != null) {
            alwaysQueryCallBacks.remove(onResultCallBack);
        }
    }


    /*请求查询当前地理位置回调*/
    public interface OnResultCallBack {
        void onSucceed(XLocation location);

        void onFailed(int errorCode, String errorMsg);
    }

    public static class LifecycleWrap implements LifecycleObserver {
        private final LifecycleOwner lifecycleOwner;
        private final OnResultCallBack onResultCallBack;
        private final List<LifecycleWrap> lifecycleWraps;

        public LifecycleWrap(List<LifecycleWrap> lifecycleWraps, LifecycleOwner lifecycleOwner, OnResultCallBack onResultCallBack) {
            this.lifecycleWraps = lifecycleWraps;
            this.lifecycleOwner = lifecycleOwner;
            this.onResultCallBack = onResultCallBack;
            //监听生命周期
            lifecycleOwner.getLifecycle().addObserver(this);
        }

        @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
        public void onDestroy(LifecycleOwner owner) {
            owner.getLifecycle().removeObserver(this);

            Iterator<LifecycleWrap> iterator = lifecycleWraps.iterator();
            while (iterator.hasNext()) {
                LifecycleWrap next = iterator.next();
                if (next.lifecycleOwner == owner) {
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
