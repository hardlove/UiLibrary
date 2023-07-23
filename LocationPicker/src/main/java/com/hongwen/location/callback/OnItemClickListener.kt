package com.hongwen.location.callback

import com.hongwen.location.model.IModel

interface OnGridItemClickListener<in T : IModel> {
        /**
         * 点击Item时回调
         */
        fun onItemClick(item: T)
    }