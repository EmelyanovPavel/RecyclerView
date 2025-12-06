package com.example.recyclerview
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MVVM (
    private val viewModel: MainViewModel,
    private val data: MutableList<Pair<Data, Boolean>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_VENUS -> VenusViewHolder(inflater.inflate(R.layout.item_venus, parent, false))
            TYPE_NEPTUNE -> NeptuneViewHolder(inflater.inflate(R.layout.item_neptune, parent, false))
            else -> HeaderViewHolder(inflater.inflate(R.layout.item_header, parent, false))
        }
    }

    fun updateData(newData: List<Pair<Data, Boolean>>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()  //Обновляем adapter
        Log.d("Adapter", "Updated ${data.size} elements")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_VENUS -> (holder as VenusViewHolder).bind(data[position])
            TYPE_NEPTUNE -> (holder as NeptuneViewHolder).bind(data[position])
            else -> (holder as HeaderViewHolder).bind(data[position])
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_HEADER
            data[position].first.someDescription.isNullOrBlank() -> TYPE_NEPTUNE
            else -> TYPE_VENUS
        }
    }

    inner class VenusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dataItem: Pair<Data, Boolean>) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<TextView>(R.id.descriptionTextView)?.text = dataItem.first.someDescription
                itemView.findViewById<ImageView>(R.id.wikiImageView)?.setOnClickListener {
                    viewModel.onItemClick(dataItem.first)
                }
            }
        }
    }

    inner class NeptuneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {
        fun bind(dataItem: Pair<Data, Boolean>) {
            itemView.findViewById<ImageView>(R.id.neptuneImageView)?.setOnClickListener {
                viewModel.onItemClick(dataItem.first)
            }
            itemView.findViewById<ImageView>(R.id.moveItemDown)?.setOnClickListener { moveDown() }
            itemView.findViewById<ImageView>(R.id.moveItemUp)?.setOnClickListener { moveUp() }
            itemView.findViewById<TextView>(R.id.neptuneTextView)?.setOnClickListener { toggleText() }
        }

        private fun toggleText() {
            viewModel.toggleItemText(layoutPosition)
        }

        private fun moveUp() {
            layoutPosition.takeIf { it > 1 }?.also { pos ->
                viewModel.onItemMoved(pos, pos - 1)
            }
        }

        private fun moveDown() {
            layoutPosition.takeIf { it < data.size - 1 }?.also { pos ->
                viewModel.onItemMoved(pos, pos + 1)
            }
        }

        override fun onItemSelected() { itemView.setBackgroundColor(Color.LTGRAY) }
        override fun onItemClear() { itemView.setBackgroundColor(0) }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dataItem: Pair<Data, Boolean>) {
            itemView.setOnClickListener { viewModel.onItemClick(dataItem.first) }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        viewModel.onItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        viewModel.onItemDismissed(position)
    }

    companion object {
        const val TYPE_VENUS = 0
        const val TYPE_NEPTUNE = 1
        const val TYPE_HEADER = 2
    }
}