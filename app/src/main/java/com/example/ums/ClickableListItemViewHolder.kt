package com.example.ums

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClickableListItemViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    val firstTextView : TextView = itemView.findViewById(R.id.first_text_view)
    val secondTextView : TextView = itemView.findViewById(R.id.second_text_view)
}