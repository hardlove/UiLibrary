package com.hongwen.location.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException

/**
 * Created by chenlu at 2023/7/21 10:47
 */
object FileUtils {

    /**
     * 复制数据库 文件到外部存储目录
     */
    @SuppressLint("SdCardPath")
    fun copyDatabaseToExternalStorage(context: Context) {
        val sourcePath = "/data/user/0/${context.packageName}/databases"

        val destPath = context.getExternalFilesDir(null)!!.absolutePath

        Log.d("Carlos", "复制数据库${sourcePath} 复制到 $destPath")
        try {
            copyDbFiles(sourcePath, destPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun copyDbFiles(sourceDir: String, destDir: String) {
        val sourceFile = File(sourceDir)
        val destFile = File(destDir)

        if (!destFile.exists()) {
            destFile.mkdirs()
        }

        if (!sourceFile.isDirectory || !destFile.isDirectory) {
            return
        }

        val files = sourceFile.listFiles()

        files?.let {
            for (file in files) {
                if (file.name.endsWith(".db")) {
                    val destPath = File(destFile, file.name)
                    file.copyTo(destPath, true)
                }

            }
        }
    }
}