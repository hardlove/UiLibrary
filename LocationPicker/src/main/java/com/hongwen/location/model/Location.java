package com.hongwen.location.model;

import android.text.TextUtils;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenlu at 2023/7/14 17:22
 */

@Entity(tableName ="china_city" )
public class Location {
    @PrimaryKey(autoGenerate = false)
    private int id;
    private String name;
    private String pinyin;
    private String province;
    private double longitude;
    private String code;
    private double latitude;
    private int flag;
    private int type;
    private String typeName;

    @Ignore
    public Location(String name, String province, String pinyin, String code) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
    }

    @Ignore

    public Location(String name, String province, String pinyin, String code, int flag) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
        this.flag = flag;
    }
    @Ignore

    public Location(String name, String province, String pinyin, String code, int type, String typeName) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
        this.type = type;
        this.typeName = typeName;
    }

    public Location(String name, String province, String pinyin, String code, double longitude, double latitude, int flag, int type, String typeName) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
        this.longitude = longitude;
        this.latitude = latitude;
        this.flag = flag;
        this.type = type;
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     * @return
     */
    public String getSection(){
        if (TextUtils.isEmpty(pinyin)) {
            return "#";
        } else {
            String c = pinyin.substring(0, 1);
            Pattern p = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(c);
            if (m.matches()) {
                return c.toUpperCase();
            }
            //在添加定位和热门数据时设置的section就是‘定’、’热‘开头
            else if (TextUtils.equals(c, "定") || TextUtils.equals(c, "热"))
                return pinyin;
            else
                return "#";
        }
    }
}
