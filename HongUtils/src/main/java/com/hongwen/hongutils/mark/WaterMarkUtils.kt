package com.hongwen.hongutils.mark

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

/**
 * ==================================================
 * Author：CL
 * 日期:2023/10/13
 * 说明：水印照片合成工具类
 * ==================================================
 **/
object WaterMarkUtils {
    /***
     * @param layer 背景层
     * @param mark 水印层
     * @param src 水印层合成区域
     * @param des 水印层合成到背景层的目标区域
     */
    @JvmStatic
    fun generateWaterMarkBitmap(
        layer: Bitmap,
        mark: Bitmap,
        src: Rect = Rect(0, 0, mark.width, mark.height),
        des: Rect,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(layer.width, layer.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        //背景层
        canvas.drawBitmap(layer, 0f, 0f, null)
        //水印层
        canvas.drawBitmap(mark, src, des, null)
        return bitmap

    }
}