package com.carlos.library.location.utils;

public class ObjectUtils {
    public static boolean isNotEmpty(final CharSequence obj) {
        return !isEmpty(obj);
    }

    public static boolean isEmpty(final CharSequence obj) {
        return obj == null || obj.toString().length() == 0;
    }

}
