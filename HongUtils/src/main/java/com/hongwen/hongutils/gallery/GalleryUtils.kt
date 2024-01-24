package com.hongwen.hongutils.gallery

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore

/**
 * ==================================================
 * Author：CL
 * 日期:2024/1/4
 * 说明：系统图库工具类
 *
 * ==================================================
 **/
object GalleryUtils {

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String) {
        // 获取图库的Uri
        val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentResolver = context.contentResolver
        // 创建保存图片的元数据
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }

        // 向图库插入新图片的元数据，并获取图片的Uri
        val imageUri = contentResolver.insert(contentUri, contentValues)
        // 如果成功获取到图片的Uri，则将Bitmap压缩成PNG格式并保存到该Uri指定的路径
        imageUri?.let { uri ->
            //打开一个输出流，该流将写入图片数据
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
    }
}