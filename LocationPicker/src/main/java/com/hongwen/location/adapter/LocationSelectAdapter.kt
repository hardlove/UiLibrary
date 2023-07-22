package com.hongwen.location.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hongwen.location.databinding.ItemSectionHotBinding
import com.hongwen.location.databinding.ItemSectionLocationBinding
import com.hongwen.location.databinding.ItemSectionNormalBinding
import com.hongwen.location.holder.BindingViewHolder
import com.hongwen.location.model.*

/**
 * Created by chenlu at 2023/7/14 16:40
 *
 * 悬浮分组
 * https://github.com/timehop/sticky-headers-recyclerview
 */
class LocationSelectAdapter(
    private var allItems: MutableList<IModel>,
    private val hotItems: List<IModel>
) :
    RecyclerView.Adapter<BindingViewHolder>() {
    companion object {
        private const val VIEW_TYPE_LOCATION = 0x01
        private const val VIEW_TYPE_HOT = 0x02
        private const val VIEW_TYPE_CONTENT = 0x03
    }

    private var locateState: LocateState = LocateState.LOCATING
    private lateinit var mLayoutManager: LinearLayoutManager
    fun setLayoutManager(manager: LinearLayoutManager) {
        this.mLayoutManager = manager
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
        val viewBinding = holder.binding
        when (location) {
            is LocatedLocation -> {
                val binding = viewBinding as ItemSectionLocationBinding
                binding.tvLocation.text = location.getName()

            }
            is HotLocation -> {
                val binding = viewBinding as ItemSectionHotBinding
                binding.innerRecyclerView.setHasFixedSize(true)
                binding.innerRecyclerView.layoutManager =
                    GridLayoutManager(binding.root.context, 3, RecyclerView.VERTICAL, false)
                binding.innerRecyclerView.adapter = HotLocationAdapter(hotItems)
            }
            else -> {
                val binding = viewBinding as ItemSectionNormalBinding
                binding.tvName.text = location.getName()
            }
        }

    }

    fun locationChanged(location: LocatedLocation, locateState: LocateState) {
        this.locateState = locateState
        allItems[0] = location
        notifyItemChanged(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(items: MutableList<IModel>) {
        allItems = items
        notifyDataSetChanged()
    }

    /**
     * 滚动RecyclerView到索引位置
     * @param index
     */
    fun scrollToSection(index: String) {
        if (allItems.isEmpty()) return
        if (TextUtils.isEmpty(index)) return
        val size: Int = allItems.size
        for (i in 0 until size) {
            if (TextUtils.equals(index.substring(0, 1), allItems[i].getSection().substring(0, 1))) {
                mLayoutManager.scrollToPositionWithOffset(i, 0)
                return
            }
        }
    }


}

