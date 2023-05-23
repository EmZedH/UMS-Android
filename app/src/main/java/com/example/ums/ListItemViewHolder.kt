package com.example.ums

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.setOnClickListener {
        }
}
    val itemIDTextView : TextView = itemView.findViewById(R.id.element_id)
    val itemNameTextView : TextView = itemView.findViewById(R.id.element_name)
}