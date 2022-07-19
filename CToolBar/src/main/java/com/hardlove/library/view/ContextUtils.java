package com.hardlove.library.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

public class ContextUtils {
    public static Activity getActivity(Context ctx) {
        while(ctx instanceof ContextWrapper){
            if(ctx instanceof Activity){
                return (Activity) ctx;
            }
            ctx = ((ContextWrapper) ctx).getBaseContext();
        }
        return null;
    }
}
