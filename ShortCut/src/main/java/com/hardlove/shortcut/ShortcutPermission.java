package com.hardlove.shortcut;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ShortcutPermission {
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_DENIED = -1;
    public static final int PERMISSION_ASK = 1;
    public static final int PERMISSION_UNKNOWN = 2;

    @IntDef(value = {
            PERMISSION_GRANTED,
            PERMISSION_DENIED,
            PERMISSION_ASK,
            PERMISSION_UNKNOWN
    })
    @Retention(RetentionPolicy.SOURCE)
    public static @interface PermissionResult {
    }
}
