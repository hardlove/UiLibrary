package com.hongwen.location.model;

import androidx.annotation.NonNull;

/**
 * Created by chenlu at 2023/7/14 17:22
 */
public class HotLocation implements IModel {
    private final String name;
    public HotLocation(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String getPingYin() {
        return "热门城市";
    }

    @NonNull
    @Override
    public String getSection() {
        return IModel.super.getSection();
    }
}
