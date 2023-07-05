package com.example.ums.superAdminCollegeAdminActivities

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R

class StudentTransactionsListItemViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    val firstTextView : TextView = itemView.findViewById(R.id.first_text_view)
    val amountTextView : TextView = itemView.findViewById(R.id.amount_text_view)
    val secondTextView : TextView = itemView.findViewById(R.id.second_text_view)
}