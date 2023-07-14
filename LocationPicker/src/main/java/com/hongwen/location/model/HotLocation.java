package com.hongwen.location.model;

/**
 * Created by chenlu at 2023/7/14 17:22
 */
public class HotLocation extends Location {
    public HotLocation(String name, String province, String code) {
        super(name, province, "热门城市", code);
    }
}
