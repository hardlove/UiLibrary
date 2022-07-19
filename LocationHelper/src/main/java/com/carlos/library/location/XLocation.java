package com.carlos.library.location;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.carlos.library.location.utils.ObjectUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

/**
 * 注意：该坐标系使用的是GPS坐标系
 */
public class XLocation {
    /**
     * 纬度
     */
    private double mLatitude;
    /**
     * 经度
     */
    private double mLongitude;
    /**
     * 国家名称
     */
    private String mCountryName;
    /**
     * 国家编码
     */
    private String mCountryCode;
    /**
     * 省份
     */
    private String mProvince;
    /**
     * 省份编码
     */
    private String mProvinceCode;
    /**
     * 城市名称
     */
    private String mCityName;
    /**
     * 城市编码
     */
    private String mCityCode;
    /**
     * 区县名称
     */
    private String mDistrictName;
    /**
     * 区县编码
     */
    private String mDistrictCode;
    /**
     * 街道社区名称
     */
    private String mStreetName;
    /**
     * 门牌号
     */
    private String mNumber;
    /**
     * 兴趣点名称
     */
    private String mPoiName;
    /**
     * 兴趣面名称
     */
    private String mAoiName;
    /**
     * 详细地址
     */
    private String mAddress;
    /**
     * 解析错误码
     */
    private int mErrorCode;
    /**
     * 错误信息
     */
    private String mErrorMsg;

    public XLocation(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        this.mErrorCode = -1;
        this.mErrorMsg = "未进行地理位置解析";
    }

    public XLocation(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
        this.mErrorCode = -1;
        this.mErrorMsg = "未进行地理位置解析";
    }


    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getCountryName() {
        return mCountryName;
    }

    public void setCountryName(String mCountryName) {
        this.mCountryName = mCountryName;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String mCountryCode) {
        this.mCountryCode = mCountryCode;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String mProvince) {
        this.mProvince = mProvince;
    }

    public String getProvinceCode() {
        return mProvinceCode;
    }

    public void setProvinceCode(String mProvinceCode) {
        this.mProvinceCode = mProvinceCode;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String mCityName) {
        this.mCityName = mCityName;
    }

    public String getCityCode() {
        return mCityCode;
    }

    public void setCityCode(String mCityCode) {
        this.mCityCode = mCityCode;
    }

    public String getDistrictName() {
        return mDistrictName;
    }

    public void setDistrictName(String mDistrictName) {
        this.mDistrictName = mDistrictName;
    }

    public String getDistrictCode() {
        return mDistrictCode;
    }

    public void setDistrictCode(String mDistrictCode) {
        this.mDistrictCode = mDistrictCode;
    }

    public String getStreetName() {
        return mStreetName;
    }

    public void setStreetName(String mStreetName) {
        this.mStreetName = mStreetName;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getPoiName() {
        return mPoiName;
    }

    public void setPoiName(String mPoiName) {
        this.mPoiName = mPoiName;
    }

    public String getAoiName() {
        return mAoiName;
    }

    public void setAoiName(String mAoiName) {
        this.mAoiName = mAoiName;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public void setErrorMsg(String mErrorMsg) {
        this.mErrorMsg = mErrorMsg;
    }
    //==================================================================================

    /**
     * 逆地理编码
     * 如需获取地理位置解析信息，需要调用该方法对各字段赋值
     * 注意：该该任务同步返回，耗时，需要在子线程中执行，避免阻塞主线程
     */
    public void doGeocoder() {
        Geocoder geocoder = new Geocoder(InitProvider.getAppContext(), Locale.getDefault());
        try {
            //反向地理编码,该操作是耗时任务，同步返回，因此需要开启异步线程执行
            List<Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
            Address address = null;
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0);
            }

            if (address != null) {

                StringBuilder sb = new StringBuilder();
                if (ObjectUtils.isNotEmpty(address.getCountryName())) {
                    //中国
                    sb.append(address.getCountryName())
                            .append("-");
                }
                if (ObjectUtils.isNotEmpty(address.getAdminArea())) {
                    //四川
                    sb.append(address.getAdminArea())
                            .append("-");
                }
                if (ObjectUtils.isNotEmpty(address.getLocality())) {
                    //成都市
                    sb.append(address.getLocality())
                            .append("-");
                }
                if (ObjectUtils.isNotEmpty(address.getSubLocality())) {
                    //武侯区
                    sb.append(address.getSubLocality())
                            .append("-");
                }
                if (ObjectUtils.isNotEmpty(address.getSubAdminArea())) {
                    //桂溪街道
                    sb.append(address.getSubAdminArea())
                            .append("-");
                }
                if (ObjectUtils.isNotEmpty(address.getThoroughfare())) {
                    //吉庆三路
                    sb.append(address.getThoroughfare())
                            .append("-");
                }
                if (ObjectUtils.isNotEmpty(address.getSubThoroughfare())) {
                    //329号
                    sb.append(address.getSubThoroughfare())
                            .append("-");
                }
                if (ObjectUtils.isNotEmpty(address.getFeatureName())) {
                    //蜀都中心(北区)雄川金融中心
                    sb.append(address.getFeatureName())
                            .append("-");
                }
                if (ObjectUtils.isNotEmpty(sb.toString()) && sb.toString().contains("-")) {
                    sb.deleteCharAt(sb.lastIndexOf("-"));
                }

                //国家
                this.setCityName(address.getCountryName());
                //省份
                this.setProvince(address.getAdminArea());
                //城市
                this.setCityName(address.getLocality());
                //区县
                this.setDistrictName(ObjectUtils.isNotEmpty(address.getSubLocality()) ? address.getSubLocality() : address.getLocality());
                //街道名称
                this.setStreetName(address.getSubAdminArea());
                //门牌号
                this.setNumber(address.getSubThoroughfare());
                //兴趣点名称
                this.setPoiName(address.getFeatureName());
                //兴趣面名称
                this.setAoiName(address.getFeatureName());
                //详细地址
                this.setAddress(sb.toString());
                this.mErrorCode = 0;
                this.mErrorMsg = "地理位置解析成功";
            } else {
                this.mErrorCode = 1;
                this.mErrorMsg = MessageFormat.format("地理位置解析失败：无可用的地址信息，坐标 mLatitude：{0},+mLongitude:{1}", mLatitude, mLongitude);
                Log.d("Carlos", "地理位置解析失败！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.mErrorCode = 2;
            this.mErrorMsg = MessageFormat.format("地理位置解析失败：坐标 mLatitude：{0},+mLongitude:{1}", mLatitude, mLongitude) + " msg:" + e.getLocalizedMessage();
        }

    }

    public boolean isSucceed() {
        return this.mErrorCode == 0;
    }

}
