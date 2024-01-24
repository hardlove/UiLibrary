package com.hongwen.hongutils.image

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import java.io.*
import java.net.URL

object BitmapUtils {
    /**
     * @param context
     * @param bitmap
     * @param storePath 保存图片的目录
     * @return
     */
    fun saveImageToGallery(context: Context, bitmap: Bitmap, storePath: String): Boolean {
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = System.currentTimeMillis().toString() + ".png"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            //通过io流的方式来压缩保存图片
            val isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath,
                fileName,
                null
            )
            //保存图片后发送广播通知更新数据库
            val uri = Uri.fromFile(file)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            return isSuccess
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 将bitmap 按照指定的尺寸缩放
     *
     * @param bitmap
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun decodeSampledBitmap(bitmap: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        // 计算缩放比例
        val scaleWidth = reqWidth / width
        val scaleHeight = reqHeight / height
        // 缩放比例，如小于1就设置为1
        var bili = Math.max(scaleWidth, scaleHeight)
        bili = Math.max(bili, 1)
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(1.0f / bili, 1.0f / bili) //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    /**
     * 将已经在界面上显示的View转成为Bitmap
     *
     * @param v
     * @return
     */
    fun convertView2Bitmap(v: View): Bitmap {
        val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        //        RectF rectf=new RectF(0,0,480,800);
//        canvas.drawArc(rectf, 0, -90, false, paint);
        v.draw(canvas)
        return bitmap
    }

    /**
     * 加载静态xml布局资生成Bitmap
     *
     * @param context
     * @param layoutRes xml 布局资源ID
     * @param onInitCallBack 数据初始化回调
     */
    fun convertViewToBitmap(
        context: Context,
        @LayoutRes layoutRes: Int,
        onInitCallBack: (View) -> Unit,
    ): Bitmap {
        val root = LayoutInflater.from(context).inflate(layoutRes, null)
        onInitCallBack(root)
        //测量
        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        root.measure(width, height)
        val measuredWidth = root.measuredWidth
        val measuredHeight = root.measuredHeight

        //再次测量（避免子View无法正常显示）
        root.measure(
            View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY)
        )

        //布局
        root.layout(0, 0, measuredWidth, measuredHeight)

        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        root.draw(canvas)
        return bitmap
    }

    /**
     * 加载静态View布局资生成Bitmap
     *
     * @param context
     * @param view    布局资源
     */
    fun convertViewToBitmap(context: Context, root: View): Bitmap {
        //测量
        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        root.measure(width, height)
        val measuredWidth = root.measuredWidth
        val measuredHeight = root.measuredHeight

//        //再次测量（避免子View无法正常显示）
//        root.measure(
//            View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY),
//            View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY)
//        )

        //布局
        root.layout(0, 0, measuredWidth, measuredHeight)

        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        root.draw(canvas)
        return bitmap
    }

    /**
     * 获取网络或本地缩略图
     * @param imageUrl 图片的url地址
     * @param defResID 默认本地占位资源ID
     * @return
     */
    fun loadImageFromURL(imageUrl: String, w: Int, h: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try { // 可以在这里通过文件名来判断，是否本地有此图片
            bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (bitmap != null) {
            val matrix = Matrix()
            val width = bitmap.width
            val height = bitmap.height
            val scaleWidth = w.toFloat() / width
            val scaleHeight = h.toFloat() / height
            matrix.postScale(scaleWidth, scaleHeight)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        } else {
            Log.d("test", "not null drawable")
        }
        return bitmap
    }

    fun loadImageFromURL(imageUrl: String, size: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try { // 可以在这里通过文件名来判断，是否本地有此图片
            bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (bitmap != null) {
            val matrix = Matrix()
            val width = bitmap.width
            val height = bitmap.height
            val scaleWidth = size.toFloat() / width
            val scaleHeight = size.toFloat() / height
            val scale = Math.max(scaleWidth, scaleHeight)
            matrix.postScale(scale, scale)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        } else {
            Log.d("test", "not null drawable")
        }
        return bitmap
    }

    fun loadImageFromFile(file: File, size: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try { // 可以在这里通过文件名来判断，是否本地有此图片
            bitmap = BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (bitmap != null) {
            val matrix = Matrix()
            val width = bitmap.width
            val height = bitmap.height
            val scaleWidth = size.toFloat() / width
            val scaleHeight = size.toFloat() / height
            val scale = Math.max(scaleWidth, scaleHeight)
            matrix.postScale(scale, scale)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        } else {
            Log.d("test", "not null drawable")
        }
        return bitmap
    }

    /**
     * bitmap 2 drawable
     *
     * @param bitmap
     * @return
     */
    @JvmStatic
    fun convertBitmap2Drawable(bitmap: Bitmap): Drawable {
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }

    /**
     * drawable 2 bitmap
     *
     * @param drawable
     * @return
     */
    @JvmStatic
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 解析图片的尺寸信息
     * options.outWidth ：图片的宽度
     * options.outHeight：图片的高度
     *
     * @param pathName
     * @return
     */
    fun getBitmapOptions(pathName: String): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        return options
    }

    /**
     * 保存Bitmap至本地
     *
     * @param bitmap
     * @param file
     */
    @JvmStatic
    fun saveBitmapFile(bitmap: Bitmap, file: File?) {
        var bos: BufferedOutputStream? = null
        try {
            bos = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)
            bos.flush()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            close(bos)
        }
    }

    @JvmStatic
    fun close(c: Closeable?) {
        // java.lang.IncompatibleClassChangeError: interface not implemented
        if (c is Closeable) {
            try {
                c.close()
            } catch (e: java.lang.Exception) {
                // silence
            }
        }
    }
}