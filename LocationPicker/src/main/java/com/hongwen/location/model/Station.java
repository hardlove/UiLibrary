package com.hongwen.location.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by chenlu at 2023/7/21 9:38
 */
@Entity(tableName = "train_station")
public class Station implements IModel{
    @PrimaryKey
    private int id;
    private String name;
    private String pingYin;
    private String address;
    private String ownership;
    private String type;
    private String category;
    private String province;
    private String city;
    private double gcjLng;
    private double gcjLat;
    private double wgs84Lng;
    private double wgs84Lat;
    private double bdLng;
    private double bdLat;
    private double isVip;

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


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getGcjLng() {
        return gcjLng;
    }

    public void setGcjLng(double gcjLng) {
        this.gcjLng = gcjLng;
    }

    public double getGcjLat() {
        return gcjLat;
    }

    public void setGcjLat(double gcjLat) {
        this.gcjLat = gcjLat;
    }

    public double getWgs84Lng() {
        return wgs84Lng;
    }

    public void setWgs84Lng(double wgs84Lng) {
        this.wgs84Lng = wgs84Lng;
    }

    public double getWgs84Lat() {
        return wgs84Lat;
    }

    public void setWgs84Lat(double wgs84Lat) {
        this.wgs84Lat = wgs84Lat;
    }

    public double getBdLng() {
        return bdLng;
    }

    public void setBdLng(double bdLng) {
        this.bdLng = bdLng;
    }

    public double getBdLat() {
        return bdLat;
    }

    public void setBdLat(double bdLat) {
        this.bdLat = bdLat;
    }

    public double getIsVip() {
        return isVip;
    }

    public void setIsVip(double isVip) {
        this.isVip = isVip;
    }

    @Ignore
    public Station(String name, String city, String province) {
        this.name = name;
        this.city = city;
        this.province = province;
    }

    public Station() {
    }

    @NonNull
    @Override
    public String getPingYin() {
        return pingYin;
    }

    public void setPingYin(String pingYin) {
        this.pingYin = pingYin;
    }

    @Nullable
    @Override
    public String getSection() {
        return IModel.super.getSection();
    }
}
