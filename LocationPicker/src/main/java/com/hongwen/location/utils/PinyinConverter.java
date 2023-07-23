package com.hongwen.location.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinConverter {
    public static String convertToPinyin(String chineseText) {
        StringBuilder pinyinText = new StringBuilder();
        
        // 遍历每个中文字符
        for (char c : chineseText.toCharArray()) {
            // 将中文字符转换成拼音
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            
            // 判断是否为中文字符
            if (pinyinArray != null && pinyinArray.length > 0) {
                // 取第一个拼音作为该中文字符的拼音
                pinyinText.append(pinyinArray[0]);
            } else {
                // 非中文字符保留原样
                pinyinText.append(c);
            }
        }
        
        return pinyinText.toString().replaceAll("1","").replaceAll("2","").replaceAll("3","").replaceAll("4","");
    }
}