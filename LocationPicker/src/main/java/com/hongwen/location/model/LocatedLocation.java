package com.hongwen.location.model;

import androidx.annotation.NonNull;

/**
 * Created by chenlu at 2023/7/14 17:22
 */
public class LocatedLocation implements IModel {
    private final String name;
    private LocateState state;

    public LocatedLocation(String name,LocateState state) {
        this.name = name;
        this.state = state;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String getPingYin() {
        return "";
    }

    @NonNull
    @Override
    public String getSection() {
        return IModel.super.getSection();
    }
}
