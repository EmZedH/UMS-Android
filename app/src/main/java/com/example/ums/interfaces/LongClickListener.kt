package com.example.ums.interfaces

import com.example.ums.model.SelectionItem

interface LongClickListener {
    fun onLongClick(selectedItemId: String, itemList: MutableList<SelectionItem>)
}