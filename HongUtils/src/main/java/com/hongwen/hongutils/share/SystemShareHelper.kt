package com.hongwen.hongutils.share

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * ==================================================
 * Author：CL
 * 日期:2023/9/11
 * 说明：分享工具类
 * ==================================================
 **/
object SystemShareHelper {

    enum class Client(val client: String,val  componentName: ComponentName) {
        WeChat("微信",ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")),
        PYQ("微信",ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI")),
        QQ("QQ",ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity")),
    }
    /**
     * 调用系统API分享文件
     * @param authority The authority of a FileProvider defined in a <provider> element in your app's manifest.
     */
    @JvmStatic
    fun shareFile(context: Context, file: File, authority: String, component: ComponentName?=null) {
        val uri = file2Uri(context, file, authority)
        shareFile(context, uri, component)
    }
    @JvmStatic
    fun shareFile(context: Context, uri: Uri, component: ComponentName?=null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            component?.let { setComponent(component) }
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setType(getMimeType(context, uri))
            putExtra(Intent.EXTRA_STREAM, uri)
        }
//        context.startActivity(Intent.createChooser(intent, "分享文件"))
        context.startActivity(intent)
    }

    /**
     * 获取文件的MimeType
     */
    @JvmStatic
    fun getMimeType(context: Context, uri: Uri): String {
        //通过contentResolver获取mimeTYpe
        var mimeType = context.contentResolver.getType(uri)
        if (mimeType.isNullOrBlank()) {
            //通过MediaMetadataRetriever获取mimeType
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
        }
        if (mimeType.isNullOrBlank()) {
            mimeType = "*/*"
        }
        return mimeType
    }

    /**
     * file转Uri
     */
    @JvmStatic
    fun file2Uri(context: Context, file: File, authority: String): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, authority, file)
        } else {
            Uri.fromFile(file)
        }
    }

}