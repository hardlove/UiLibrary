package com.hongwen.hongutils.timer

import android.os.Handler
import android.os.Looper
import java.text.DecimalFormat
import java.util.*

/**
 * 自定义正计时器|倒计时器
 */
class CustomTimer() {
    /*倒计时总时长*/
    private var totalTime: Long = 0

    /*已计时时长*/
    private var customerTime: Long = 0

    /*是否暂停*/
    private var isPause = false

    /*是否已经启动*/
    private var isStart = false


    constructor(totalTime: Long) : this() {
        this.totalTime = totalTime
    }

    fun getTotalTime() = totalTime
    fun getCustomerTime() = customerTime
    fun isPause() = isPause
    fun isStart() = isStart

    private var timer: Timer? = null

    /**
     * 设置倒计时总时长
     */
    fun setTotalTime(millis: Long) {
        this.totalTime = millis
    }

    /**
     * 用于恢复计时
     */
    fun setCustomerTime(millis: Long) {
        this.customerTime = customerTime
    }

    /**
     * 开启倒计时
     */
    fun start() {
        if (!isStart) {
            timer?.cancel()
            timer = Timer()
            timer?.schedule(object : TimerTask() {
                override fun run() {
                    if (!isPause) {
                        customerTime += 1000L

                        if (customerTime <= totalTime) {
                            notifyTimeChange()
                        } else {
                            stop()
                        }
                    }
                }

            }, 0, 1000L)
            isStart = true
        }
    }


    /**
     * 恢复计时
     */
    fun resume() {
        if (isPause) {
            isPause = false
        }

    }

    /**
     * 暂停计时
     */
    fun pause() {
        if (!isPause) {
            isPause = true
        }
    }

    /**
     * 重置计时
     */
    fun reset() {
        isStart = false
        isPause = false
        customerTime = 0
        timer?.cancel()
        timer = null

    }

    /**
     * 停止计时
     */
    fun stop() {
        isStart = false
        isPause = false
        timer?.cancel()
        timer = null
    }

    interface OnTimeChangeListener {
        /**
         * 正计时时长
         */
        fun onPositiveTimeChange(time: String) {}

        /**
         * 倒计时剩余时长
         */
        fun onCountDownTimeChange(time: String) {}

        /**
         * 计时完成
         */
        fun onComplete() {}

        /**
         * 计时开始
         */
        fun onStart() {}
    }

    private var listener: OnTimeChangeListener? = null

    fun setOnTimeChangeListener(listener: OnTimeChangeListener) {
        this.listener = listener
    }

    private val handler = Handler(Looper.getMainLooper())
    private fun notifyTimeChange() {
        handler.post {
            listener?.onCountDownTimeChange(millsToString(totalTime - customerTime))
            listener?.onPositiveTimeChange(millsToString(customerTime))
            if (totalTime == customerTime) {
                listener?.onComplete()
            }
        }

    }


    fun millsToString(l: Long): String {
        val sb = StringBuilder()
        val l1 = l / 3600000
        val l2 = l % 3600000 / 60000
        val l3 = l % 60000 / 1000
        sb.append(DecimalFormat("00").format(l1)).append(":").append(DecimalFormat("00").format(l2))
            .append(":").append(DecimalFormat("00").format(l3))
        return sb.toString()

    }


}