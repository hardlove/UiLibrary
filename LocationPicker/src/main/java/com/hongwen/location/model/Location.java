package com.hongwen.location.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by chenlu at 2023/7/14 17:22
 */

@Entity(tableName = "china_city")
public class Location implements IModel {
    @PrimaryKey(autoGenerate = true)
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


    @NonNull
    @Override
    public String getPingYin() {
        return pinyin;
    }


    @NonNull
    @Override
    public String getSection() {
        return IModel.super.getSection();
    }
}
