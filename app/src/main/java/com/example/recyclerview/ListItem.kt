package com.example.recyclerview

sealed class ListItem {
    data class Header(val title: String) : ListItem()
    data class RegularItem(val title: String) : ListItem()
}