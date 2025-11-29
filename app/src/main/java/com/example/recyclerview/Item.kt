package com.example.recyclerview

data class Item(
    val id: Long = System.currentTimeMillis(),
    var title: String,
    var description: String
)
