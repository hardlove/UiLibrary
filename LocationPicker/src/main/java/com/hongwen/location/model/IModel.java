package com.hongwen.location.model;

import android.text.TextUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenlu at 2023/7/23 13:57
 */
public interface IModel {
    public String getName();

    public String getPingYin();

    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     * @return
     */
    public default String getSection() {
        if (TextUtils.isEmpty(getPingYin())) {
            return "#";
        } else {
            String c = getPingYin().substring(0, 1);
            Pattern p = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(c);
            if (m.matches()) {
                return c.toUpperCase(Locale.getDefault());
            } else if (TextUtils.equals(c, "定") || TextUtils.equals(c, "热")) {
                return getPingYin();
            } else {
                return "#";
            }
        }
    }
}
