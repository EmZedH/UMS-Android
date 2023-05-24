package com.example.ums

import android.content.ClipData
import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO

class CollegeListItemViewAdapter(private val collegeList : MutableList<College>, private val collegeDAO: CollegeDAO) : RecyclerView.Adapter<ListItemViewHolder>(){

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

        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(college, holder, position)
        }
    }

    private fun showOptionsPopupMenu(college : College, holder: ListItemViewHolder, position: Int){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    // Handle edit option
                    true
                }
                R.id.delete_college -> {
                    // Handle delete option
                    collegeDAO.delete(college.collegeID)
                    collegeList.removeAt(position)
                    notifyItemRemoved(position)
                    true
                }
                // Add more menu item cases as needed

                else -> {
                    false
                }
            }}
        popupMenu.show()
    }
}