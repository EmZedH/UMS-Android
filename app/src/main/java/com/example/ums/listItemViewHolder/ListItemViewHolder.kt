package com.example.ums.listItemViewHolder

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R

class ListItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val optionsButton : ImageButton = itemView.findViewById(R.id.options_button)
        val itemIDTextView : TextView = itemView.findViewById(R.id.element_id)
        val itemNameTextView : TextView = itemView.findViewById(R.id.element_name)
}