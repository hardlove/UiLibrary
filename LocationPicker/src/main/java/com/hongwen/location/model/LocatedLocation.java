package com.hongwen.location.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by chenlu at 2023/7/14 17:22
 */
public class LocatedLocation implements IModel, Serializable {
    private final String name;

    public LocatedLocation(String name) {
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
        return "";
    }

    @NonNull
    @Override
    public String getSection() {
        return IModel.super.getSection();
    }
}
