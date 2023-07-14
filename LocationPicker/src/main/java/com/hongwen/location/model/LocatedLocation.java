package com.hongwen.location.model;

/**
 * Created by chenlu at 2023/7/14 17:22
 */
public class LocatedLocation extends Location {
    public LocatedLocation(String name, String province, String code) {
        super(name, province, "定位城市", code);
    }
}
