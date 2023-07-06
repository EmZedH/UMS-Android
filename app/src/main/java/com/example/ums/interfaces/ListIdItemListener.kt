package com.example.ums.interfaces

interface ListIdItemListener {
    fun onDelete(id: List<Int>)
    fun onUpdate(id: List<Int>)
    fun onClick(id: List<Int>)
    fun onLongClick(id: List<Int>)
}