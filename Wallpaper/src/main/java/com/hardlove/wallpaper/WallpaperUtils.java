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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.RomUtils;
import com.hardlove.wallpaper.service.VideoWallpaperService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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


    public static void setWallpaper(Context context, Uri uri) {
        try {
            if (RomUtils.isHuawei()) {
                setHuawei(context, uri);
            } else if (RomUtils.isXiaomi()) {
                setXiaomi(context, uri);
            } else if (RomUtils.isVivo()) {
                setVivo(context, uri);
            } else if (RomUtils.isOppo()) {
                setOppo(context, uri);
            } else {
                setOther(context, uri);
            }
        } catch (Exception e) {
            try {
                e.printStackTrace();
                setWallpaperDefault2(context, uri);
            } catch (Exception ex) {
                try {
                    ex.printStackTrace();
                    setWallpaperDefault3(context, uri);
                } catch (IOException exc) {
                    exc.printStackTrace();
                    Toast.makeText(context, "你是暂不支持", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 华为
     */
    private static void setHuawei(Context context, Uri uri) {
        try {
            ComponentName componentName = new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.Wallpaper");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            setWallpaperDefault(context, uri);
        }

    }

    /**
     * 小米
     */
    private static void setXiaomi(Context context, Uri uri) {
        try {
            ComponentName componentName = new ComponentName("com.android.thememanager",
                    "com.android.thememanager.activity.WallpaperDetailActivity");
            Intent intent = new Intent("miui.intent.action.START_WALLPAPER_DETAIL");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            setWallpaperDefault(context, uri);
        }

    }

    /**
     * Vivo
     */
    private static void setVivo(Context context, Uri uri) {
        try {
            ComponentName componentName = new ComponentName("com.vivo.gallery", "com.android.gallery3d.app.Wallpaper");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            setWallpaperDefault(context, uri);
        }

    }

    /**
     * OPPO
     */
    private static void setOppo(Context context, Uri uri) {
        try {
            ComponentName componentName = new ComponentName("com.coloros.gallery3d", "com.oppo.gallery3d.app.Wallpaper");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            setWallpaperDefault(context, uri);
        }

    }


    public static void setOther(Context context, Uri uri) {
        setWallpaperDefault(context, uri);
    }

    public static void setWallpaperDefault(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("mimeType", "image/*");
        intent.setDataAndType(uri, "image/*");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            throw new RuntimeException("setWallpaperDefault Intent.ACTION_ATTACH_DATA  设置壁纸失败...");
        }

    }

    private static void setWallpaperDefault2(Context context, Uri uri) {
        Intent cropAndSetWallpaperIntent = WallpaperManager.getInstance(context.getApplicationContext()).getCropAndSetWallpaperIntent(uri);
        if (cropAndSetWallpaperIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(cropAndSetWallpaperIntent);
        } else {
            throw new RuntimeException("setOthers 设置壁纸失败...");
        }
    }

    /**
     * 设置为桌面静态壁纸
     */
    private static void setWallpaperDefault3(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        WallpaperManager.getInstance(context.getApplicationContext()).setStream(inputStream);
//            WallpaperManager.getInstance(context.getApplicationContext()).setBitmap(ImageUtils.getBitmap(UriUtils.uri2File(uri)));
    }

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
     * 将图片插入到系统图库，修复部分设备无法加载图片的问题
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        Uri uri = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
//            Uri baseUri = Uri.parse("content://media/external/images/media");
            Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return uri;
    }
}
