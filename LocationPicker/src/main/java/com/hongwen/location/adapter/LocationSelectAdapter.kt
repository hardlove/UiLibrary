package com.hongwen.location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hongwen.location.databinding.ItemSectionHotBinding
import com.hongwen.location.databinding.ItemSectionLocationBinding
import com.hongwen.location.databinding.ItemSectionNormalBinding
import com.hongwen.location.holder.BindingViewHolder
import com.hongwen.location.model.HotLocation
import com.hongwen.location.model.LocatedLocation
import com.hongwen.location.model.Location

/**
 * Created by chenlu at 2023/7/14 16:40
 *
 * 悬浮分组
 * https://github.com/timehop/sticky-headers-recyclerview
 */
class LocationSelectAdapter(private val allItems: List<Location>, val hotItems: List<Location>) :
    RecyclerView.Adapter<BindingViewHolder>() {
    companion object {
        private const val VIEW_TYPE_LOCATION = 0x01
        private const val VIEW_TYPE_HOT = 0x02
        private const val VIEW_TYPE_CONTENT = 0x03
    }

    override fun getItemViewType(position: Int): Int {
        return when (allItems[position]) {
            is LocatedLocation -> VIEW_TYPE_LOCATION
            is HotLocation -> VIEW_TYPE_HOT
            else -> VIEW_TYPE_CONTENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        when (viewType) {
            VIEW_TYPE_LOCATION -> {
                return BindingViewHolder(
                    ItemSectionLocationBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_HOT -> {
                return BindingViewHolder(
                    ItemSectionHotBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                return BindingViewHolder(
                    ItemSectionNormalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }


    }

    override fun getItemCount() = allItems.size


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val location = allItems[holder.adapterPosition]
        when (location) {
            is LocatedLocation -> {

            }
            is HotLocation -> {

            }
            else -> {

            }
        }

    }

}

