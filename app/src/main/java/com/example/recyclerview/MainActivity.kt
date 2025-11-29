package com.example.recyclerview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private val items = mutableListOf<Item>()
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecyclerActivityAdapter
    private lateinit var adapter2: ItemAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val data = arrayListOf(
            Pair(Data("Venus"), false),
            Pair(Data("Venus"), false),
            Pair(Data("Neptune", ""), false),
            Pair(Data("Venus"), false),
            Pair(Data("Venus"), false),
            Pair(Data("Venus"), false),
            Pair(Data("Neptune", null), false)
        )

        data.add(0, Pair(Data("Header"), false))

        adapter = RecyclerActivityAdapter(
            object : RecyclerActivityAdapter.OnListItemClickListener {
                override fun onItemClick(data: Data) {
                    Toast.makeText(this@MainActivity, data.someText, Toast.LENGTH_LONG).show()
                }
            },
            data
        )

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        binding.recyclerView.adapter = adapter
        ItemTouchHelper(ItemTouchHelperCallback(adapter))
            .attachToRecyclerView(binding.recyclerView)


        binding.fab.setOnClickListener {
            adapter.appendItem()
            binding.recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }

        recyclerView = findViewById(R.id.recyclerView)
        fab = findViewById(R.id.fab)

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter2 = ItemAdapter(
            items,
            { item -> showItemDetails(item) },
            { position -> removeItem(position) }
        )
        recyclerView.adapter = adapter

        // FAB: добавление нового элемента
        fab.setOnClickListener {
            showAddItemDialog()
        }
        // Добавляем начальные данные для демонстрации
        loadSampleData()

    }

    //adding new element
    private fun showAddItemDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("The new note")

        val view = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val etTitle: EditText = view.findViewById(R.id.etTitle)
        val etDescription: EditText = view.findViewById(R.id.etDescription)

        builder.setView(view)
        builder.setPositiveButton("Add") { _, _ ->
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            if (title.isNotEmpty()) {
                val newItem = Item(title = title, description = description)
                items.add(newItem)
                adapter.notifyItemInserted(items.size - 1)
                recyclerView.smoothScrollToPosition(items.size - 1)
            }
        }
        builder.setNegativeButton("Cansel", null)
        builder.show()
    }

    //removing element (long press)
    private fun removeItem(position: Int): Boolean {
        items.removeAt(position)
        adapter.notifyItemRemoved(position)
        Toast.makeText(this, "Element removed", Toast.LENGTH_SHORT).show()
        return true
    }

    // 3. Editing element (click)
    private fun showItemDetails(item: Item) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit note")

        val view = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val etTitle: EditText = view.findViewById(R.id.etTitle)
        val etDescription: EditText = view.findViewById(R.id.etDescription)

        etTitle.setText(item.title)
        etDescription.setText(item.description)

        builder.setView(view)
        builder.setPositiveButton("Save") { _, _ ->
            item.title = etTitle.text.toString()
            item.description = etDescription.text.toString()
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cansel", null)
        builder.show()
    }

    private fun loadSampleData() {
        items.addAll(listOf(
            Item(title = "Task 1", description = "description of the first task"),
            Item(title = "Task 2", description = "description of the second task")
        ))
        adapter.notifyDataSetChanged()
    }
}


class RecyclerActivityAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private var data: MutableList<Pair<Data, Boolean>>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_VENUS -> VenusViewHolder(
                inflater.inflate(R.layout.item_venus, parent, false) as View
            )

            TYPE_NEPTUNE ->
                NeptuneViewHolder(
                    inflater.inflate(R.layout.item_neptune, parent, false) as View
                )

            else -> HeaderViewHolder(
                inflater.inflate(R.layout.item_header, parent, false) as View
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_VENUS -> {
                holder as VenusViewHolder
                holder.bind(data[position])
            }

            TYPE_NEPTUNE -> {
                holder as NeptuneViewHolder
                holder.bind(data[position])
            }

            else -> {
                holder as HeaderViewHolder
                holder.bind(data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_HEADER
            data[position].first.someDescription.isNullOrBlank() -> TYPE_NEPTUNE
            else -> TYPE_VENUS
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun appendItem() {
        data.add(generateItem())
        //notifyDataSetChanged()
        notifyItemInserted(itemCount - 1)
    }

    private fun generateItem() = Pair(Data("Neptune", ""), false)

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            data.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class VenusViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(dataItem: Pair<Data, Boolean>) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<TextView>(R.id.descriptionTextView).text =
                    dataItem.first.someDescription
                itemView.findViewById<ImageView>(R.id.wikiImageView)
                    .setOnClickListener { onListItemClickListener.onItemClick(dataItem.first) }
            }
        }
    }

    inner class NeptuneViewHolder(view: View) : RecyclerView.ViewHolder(view),
        ItemTouchHelperViewHolder {

        fun bind(dataItem: Pair<Data, Boolean>) {
            itemView.findViewById<ImageView>(R.id.neptuneImageView)
                .setOnClickListener { onListItemClickListener.onItemClick(dataItem.first) }
            itemView.findViewById<ImageView>(R.id.moveItemDown).setOnClickListener { moveDown() }
            itemView.findViewById<ImageView>(R.id.moveItemUp).setOnClickListener { moveUp() }
            itemView.findViewById<TextView>(R.id.neptuneTextView)
                .setOnClickListener { toggleText() }
        }

        private fun toggleText() {
            data[layoutPosition] = data[layoutPosition].let {
                it.first to !it.second
            }
            //FIXME отладить появление/исчезновение текста
            if (data[layoutPosition].second) {
                itemView.findViewById<TextView>(R.id.neptuneDescriptionTextView).visibility =
                    View.VISIBLE
            } else {
                itemView.findViewById<TextView>(R.id.neptuneDescriptionTextView).visibility =
                    View.GONE
            }
            notifyItemChanged(layoutPosition)
        }

        private fun moveUp() {
            layoutPosition.takeIf { it > 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition - 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition - 1)
            }
        }

        private fun moveDown() {
            layoutPosition.takeIf { it < data.size - 1 }?.also { currentPosition ->
                data.removeAt(currentPosition).apply {
                    data.add(currentPosition + 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition + 1)
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(dataItem: Pair<Data, Boolean>) {
            itemView.setOnClickListener { onListItemClickListener.onItemClick(dataItem.first) }
        }
    }

    interface OnListItemClickListener {
        fun onItemClick(data: Data)
    }

    companion object {
        private const val TYPE_VENUS = 0
        private const val TYPE_NEPTUNE = 1
        private const val TYPE_HEADER = 2
    }
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)

    fun onItemDismiss(position: Int)
}

interface ItemTouchHelperViewHolder {

    fun onItemSelected()

    fun onItemClear()
}

class ItemTouchHelperCallback(private val adapter: RecyclerActivityAdapter) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(
            dragFlags,
            swipeFlags
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(source.getBindingAdapterPosition(), target.getBindingAdapterPosition())
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        adapter.onItemDismiss(viewHolder.getBindingAdapterPosition())
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemViewHolder =
                viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val itemViewHolder =
            viewHolder as ItemTouchHelperViewHolder
        itemViewHolder.onItemClear()
    }
}