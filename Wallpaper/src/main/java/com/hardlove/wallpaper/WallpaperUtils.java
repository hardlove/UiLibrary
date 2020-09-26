package com.hardlove.wallpaper;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.UriUtils;
import com.hardlove.wallpaper.service.VideoWallpaperService;

import java.io.File;
import java.io.IOException;

/**
 * 壁纸工具类
 */
public class WallpaperUtils {
    /**
     * {@link Intent#ACTION_WALLPAPER_CHANGED}
     * 注册该广播可以监听壁纸变化
     */

    /**
     * 设置桌面视频动态壁纸
     *
     * @param context
     * @param videoPath
     */
    public static void setVideoWallpaper(Context context, String videoPath) {
        VideoWallpaperService.setToWallPaper(context, videoPath);
    }

    /**
     * 设置静音
     *
     * @param context
     */
    public static void setVoiceSilence(@NonNull Context context) {
        VideoWallpaperService.setVoiceSilence(context);
    }

    /**
     * 设置有声音
     *
     * @param context
     */
    public static void setVoiceNormal(@NonNull Context context) {
        VideoWallpaperService.setVoiceNormal(context);
    }

    //=============================================================================================

    /**
     * 设置为桌面静态壁纸
     *
     * @param context
     * @param bitmap
     */
    public static void setWallpaper(Context context, Bitmap bitmap) {
        try {
            WallpaperManager.getInstance(context).setBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用系统图库，设置指定的图片为壁纸
     *可选择设置桌面壁纸和锁屏壁纸
     * @param context
     * @param uriPath
     */
    public static void setWallpaper(Context context, Uri uriPath) {
        if (RomUtils.isHuawei()) {
            setHuawei(context, UriUtils.uri2File(uriPath).getAbsolutePath(), uriPath);
        } else if (RomUtils.isXiaomi()) {
            setXiaomi(context, UriUtils.uri2File(uriPath).getAbsolutePath(), uriPath);
        } else if (RomUtils.isVivo()) {
            setVivo(context, UriUtils.uri2File(uriPath).getAbsolutePath(), uriPath);
        } else if (RomUtils.isOppo()) {
            setOppo(context, UriUtils.uri2File(uriPath).getAbsolutePath(), uriPath);
        } else {
            setOthers(context, UriUtils.uri2File(uriPath).getAbsolutePath(), UriUtils.uri2File(uriPath));
        }
    }

    /**
     * 华为
     */
    private static void setHuawei(Context context, String path, Uri uriPath) {
        Intent intent;
        try {
            ComponentName componentName =
                    new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.Wallpaper");
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uriPath, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            setWallpaperDefault(context, path);
        }
    }

    /**
     * 小米
     */
    private static void setXiaomi(Context context, String path, Uri uriPath) {
        Intent intent;
        try {
            ComponentName componentName = new ComponentName("com.android.thememanager",
                    "com.android.thememanager.activity.WallpaperDetailActivity");
            intent = new Intent("miui.intent.action.START_WALLPAPER_DETAIL");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uriPath, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            setWallpaperDefault(context, path);
        }
    }

    /**
     * Vivo
     */
    private static void setVivo(Context context, String path, Uri uriPath) {
        Intent intent;
        try {
            ComponentName componentName =
                    new ComponentName("com.vivo.gallery", "com.android.gallery3d.app.Wallpaper");
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uriPath, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            setWallpaperDefault(context, path);
        }
    }

    /**
     * OPPO
     */
    private static void setOppo(Context context, String path, Uri uriPath) {
        Intent intent;
        try {
            ComponentName componentName =
                    new ComponentName("com.coloros.gallery3d", "com.oppo.gallery3d.app.Wallpaper");
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uriPath, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            setWallpaperDefault(context, path);
        }
    }

    /**
     * 其他
     */
    private static void setOthers(Context context, String path, File file) {
        try {
            context.startActivity(WallpaperManager.getInstance(context.getApplicationContext())
                    .getCropAndSetWallpaperIntent(getImageContentUri(context, file)));
        } catch (Exception e) {
            e.printStackTrace();
            setWallpaperDefault(context, path);
        }
    }

    /**
     * 默认方法
     */
    private static void setWallpaperDefault(Context context, String path) {
        try {
            WallpaperManager.getInstance(context.getApplicationContext()).setBitmap(ImageUtils.getBitmap(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param imageFile
     * @return content Uri
     */
    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
