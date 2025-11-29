package com.example.recyclerview
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.recyclerview.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListAdapter
    private var dataList = mutableListOf<ListItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): RelativeLayout {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = ListAdapter()
        binding.recyclerView.adapter = adapter
        dataList = mutableListOf(
            ListItem.Header("Tasks"),
            ListItem.RegularItem("Example")
        )
        adapter.submitList(dataList)
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val newItem = ListItem.RegularItem("New task")
            dataList.add(newItem)  // Меняем локальный список
            adapter.submitList(dataList)  // Обновляем адаптер
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}