package com.example.ums

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.model.College

class CollegeListItemViewAdapter(private val collegeList : List<College>) : RecyclerView.Adapter<ListItemViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return collegeList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val college = collegeList[position]
        holder.itemIDTextView.text = "ID : C/${college.collegeID}"
        holder.itemNameTextView.text = college.collegeName

    }

}