package com.example.ums

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    val optionsButton : ImageButton = itemView.findViewById(R.id.options_button)
    init {
        itemView.setOnClickListener {
        }
    }
    val itemIDTextView : TextView = itemView.findViewById(R.id.element_id)
    val itemNameTextView : TextView = itemView.findViewById(R.id.element_name)

}