package com.hongwen.location.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hongwen.location.callback.OnGridItemClickListener
import com.hongwen.location.callback.OnPickerListener
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
    private val hotItems: List<IModel>,
    private var locateState: LocateState = LocateState.INIT
) :
    RecyclerView.Adapter<BindingViewHolder>(), OnPickerListener.OnLocationStateChangeListener {
    companion object {
        private const val VIEW_TYPE_LOCATION = 0x01
        private const val VIEW_TYPE_HOT = 0x02
        private const val VIEW_TYPE_CONTENT = 0x03
    }

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
        val iModel = allItems[holder.adapterPosition]
        val viewBinding = holder.binding
        when (iModel) {
            is LocatedLocation -> {
                val binding = viewBinding as ItemSectionLocationBinding
                binding.tvLocation.text = iModel.getName()
                binding.tvLocation.setOnClickListener {
                    when (locateState) {
                        LocateState.INIT -> {
                            onLocateListener?.onLocate(this@LocationSelectAdapter)
                            updateLocationStateChanged(
                                LocatedLocation("正在定位"),
                                LocateState.LOCATING
                            )
                        }
                        LocateState.LOCATING -> {
                            return@setOnClickListener
                        }
                        LocateState.SUCCESS -> {
                            onItemClickListener?.onItemClick(iModel)
                        }
                        LocateState.FAILURE -> {
                            onLocateListener?.onLocate(this@LocationSelectAdapter)
                            updateLocationStateChanged(
                                LocatedLocation("正在定位"),
                                LocateState.LOCATING
                            )
                        }
                    }

                }
                binding.tvRelocate.setOnClickListener {
                    onLocateListener?.onLocate(this@LocationSelectAdapter)
                    updateLocationStateChanged(LocatedLocation("正在定位"), LocateState.LOCATING)
                }

            }
            is HotLocation -> {
                val binding = viewBinding as ItemSectionHotBinding
                binding.innerRecyclerView.setHasFixedSize(true)
                binding.innerRecyclerView.layoutManager =
                    GridLayoutManager(binding.root.context, 3, RecyclerView.VERTICAL, false)
                binding.innerRecyclerView.adapter = HotLocationAdapter(hotItems)
                    .apply {
                        setOnGridItemClickListener(object : OnGridItemClickListener<IModel> {
                            override fun onItemClick(item: IModel) {
                                this@LocationSelectAdapter.onItemClickListener?.onItemClick(item)
                            }

                        })
                    }
            }
            else -> {
                val binding = viewBinding as ItemSectionNormalBinding
                binding.tvName.text = iModel.getName()
                binding.tvName.setOnClickListener {
                    this@LocationSelectAdapter.onItemClickListener?.onItemClick(iModel)
                }
            }
        }

    }

    private fun updateLocationStateChanged(location: LocatedLocation, locateState: LocateState) {
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

    private var onItemClickListener: OnPickerListener.OnItemClickListener<IModel>? = null
    fun setOnItemClickListener(onItemClickListener: OnPickerListener.OnItemClickListener<IModel>?) {
        this.onItemClickListener = onItemClickListener
    }

    private var onLocateListener: OnPickerListener.OnLocateListener? = null
    fun setOnLocateListener(onLocateListener: OnPickerListener.OnLocateListener?) {
        this.onLocateListener = onLocateListener
    }

    /**
     * 定位成功
     */

    override fun onSuccess(locate: LocatedLocation) {
        updateLocationStateChanged(locate, LocateState.SUCCESS)
    }

    /**
     * 定位失败
     */

    override fun onFailed(msg: String?) {
        updateLocationStateChanged(LocatedLocation("定位失败"), LocateState.FAILURE)
    }


}

