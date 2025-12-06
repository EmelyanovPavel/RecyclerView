package com.example.recyclerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _dataList = MutableLiveData<MutableList<Pair<Data, Boolean>>>()
    val dataList: LiveData<MutableList<Pair<Data, Boolean>>> = _dataList

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        initializeData()
    }

    private fun initializeData() {
        val initialData = mutableListOf(
            Pair(Data("Venus"), false),
            Pair(Data("Venus"), false),
            Pair(Data("Neptune", ""), false),
            Pair(Data("Venus"), false),
            Pair(Data("Venus"), false),
            Pair(Data("Venus"), false),
            Pair(Data("Neptune", null), false)
        )
        initialData.add(0, Pair(Data("Header"), false))
        _dataList.value = initialData
    }

    fun onItemClick(data: Data) {
        _toastMessage.value = data.someText
    }

    fun onFabClick() {
        val currentData = _dataList.value ?: mutableListOf()
        currentData.add(Pair(Data("Neptune", ""), false))
        _dataList.value = currentData
    }

    fun onItemMoved(fromPosition: Int, toPosition: Int) {
        val currentData = _dataList.value ?: return
        val movedItem = currentData.removeAt(fromPosition)
        currentData.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, movedItem)
        _dataList.value = ArrayList(currentData) // ✅ Создаем копию для триггера LiveData
    }

    fun onItemDismissed(position: Int) {
        val currentData = _dataList.value ?: return
        currentData.removeAt(position)
        _dataList.value = ArrayList(currentData)
    }

    fun toggleItemText(position: Int) {
        val currentData = _dataList.value ?: return
        if (position in currentData.indices) {
            val currentItem = currentData[position]
            currentData[position] = Pair(currentItem.first, !currentItem.second)
            _dataList.value = ArrayList(currentData)
        }
    }
}