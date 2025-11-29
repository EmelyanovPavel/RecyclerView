package com.example.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.databinding.ItemHeaderBinding
import com.example.recyclerview.databinding.ItemRegularBinding

class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ListItem> = listOf()

    fun submitList(list: List<ListItem>) {
        items = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.Header -> VIEW_TYPE_HEADER
            is ListItem.RegularItem -> VIEW_TYPE_REGULAR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemHeaderBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_REGULAR -> RegularViewHolder(
                ItemRegularBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(items[position] as ListItem.Header)
            is RegularViewHolder -> holder.bind(items[position] as ListItem.RegularItem)
        }
    }

    override fun getItemCount(): Int = items.size

    private companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_REGULAR = 1
    }

    class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.Header) {
            binding.header.text = item.title
        }
    }

    class RegularViewHolder(private val binding: ItemRegularBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.RegularItem) {
            binding.tvTitle.text = item.title
            // Здесь можно настроить иконку
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }
}