package com.hongwen.location.callback

import android.content.DialogInterface
import com.hongwen.location.LocationPicker
import com.hongwen.location.model.IModel
import com.hongwen.location.model.LocatedLocation

/**
 * Created by chenlu at 2023/7/22 17:25
 */
interface OnPickerListener {
    interface OnCancelListener {
        fun onCancel(dialog: DialogInterface?)
    }

    interface OnDismissListener {
        fun onDismiss(dialog: DialogInterface?)
    }

    interface OnShowListener {
        fun onShow(dialog: DialogInterface?)
    }

    /**
     * 数据加载器
     */
    interface IModelLoader<out T : IModel> : java.io.Serializable {
        /**
         * 获取所有Item数据
         */
        fun getAllItems(): List<T>

        /**
         * 获取热门推荐数据
         */
        fun getHotItems(): List<T>

    }

    /**
     * 定位
     */
    interface OnLocateListener {
        /**
         * 请求定位回调
         */
        fun onLocate(callback: OnLocationStateChangeListener)
    }

    interface OnItemClickListener<in T : IModel> {
        /**
         * 点击Item时回调
         */
        fun onItemClick(item: T)
    }

    /**
     * 定位状态变更
     */
    interface OnLocationStateChangeListener {
        fun onSuccess(locate: LocatedLocation)
        fun onFailed(msg: String?)
    }
}

fun LocationPicker.setOnCancelListener(onCancelListener: (DialogInterface?) -> Unit): LocationPicker {
    val listener = object : OnPickerListener.OnCancelListener {
        override fun onCancel(dialog: DialogInterface?) {
            onCancelListener(dialog)
        }
    }
    setOnCancelListener(listener)
    return this
}

fun LocationPicker.setOnDismissListener(onDismissListener: (DialogInterface?) -> Unit): LocationPicker {
    val listener = object : OnPickerListener.OnDismissListener {
        override fun onDismiss(dialog: DialogInterface?) {
            onDismissListener(dialog)
        }
    }
    setOnDismissListener(listener)
    return this
}
fun LocationPicker.setOnItemClickListener(onItemClick: (IModel) -> Unit): LocationPicker {
    setOnItemClickListener(object :OnPickerListener.OnItemClickListener<IModel>{
        override fun onItemClick(item: IModel) {
            onItemClick(item)
        }
    })
    return this
}
