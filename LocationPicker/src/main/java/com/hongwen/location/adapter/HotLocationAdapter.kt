package com.hongwen.location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hongwen.location.callback.OnGridItemClickListener
import com.hongwen.location.databinding.ItemInnerTableBinding
import com.hongwen.location.holder.BindingViewHolder
import com.hongwen.location.model.IModel

/**
 * Created by chenlu at 2023/7/16 8:34
 */
class HotLocationAdapter(private val items: List<IModel>) :
    RecyclerView.Adapter<BindingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        return BindingViewHolder(
            ItemInnerTableBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val item = items[holder.adapterPosition]
        holder.binding as ItemInnerTableBinding
        holder.binding.tvName.text = item.getName()
        holder.binding.tvName.setOnClickListener {
            onGridItemClickListener?.onItemClick(item)
        }
    }

    private var onGridItemClickListener: OnGridItemClickListener<IModel>? = null
    fun setOnGridItemClickListener(onGridItemClickListener: OnGridItemClickListener<IModel>?) {
        this.onGridItemClickListener = onGridItemClickListener
    }


}