package com.example.ums.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.ListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ItemListener
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO

class CollegeListItemViewAdapter(private val collegeDAO: CollegeDAO, private val itemListener: ItemListener) : RecyclerView.Adapter<ListItemViewHolder>() {

    private var originalList : MutableList<College> = collegeDAO.getList().toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val college = originalList[position]
        holder.itemIDTextView.setText(R.string.college_id)
        holder.itemIDTextView.append(college.id.toString())
        holder.itemNameTextView.text = college.name

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("collegeID",college.id)
            itemListener.onClick(bundle)
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(college, holder)
        }
    }

    fun updateItemInAdapter(position: Int) {
        originalList[position] = collegeDAO.get(position+1)!!
        notifyItemChanged(position)
    }

    private fun showOptionsPopupMenu(college : College, holder: ListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    itemListener.onUpdate(college.id)
                    true
                }
                R.id.delete_college -> {
                    itemListener.onDelete(college.id)
//                    showConfirmationDialog(context, college)
                    true
                }

                else -> {
                    false
                }
            }}
        popupMenu.show()
    }

    fun filter(query: String?){
        val filteredList =
            if(query.isNullOrEmpty())
                collegeDAO.getList()
            else
                collegeDAO.getList().filter { item -> item.name.contains(query, ignoreCase = true) }

        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }
    fun addItem(position: Int){
        originalList.add(position, collegeDAO.get(position+1)!!)
    }

    fun deleteItem(id: Int){
        Log.i("CollegeDeleteDialogClass","collegeID: $id")
        val college = collegeDAO.get(id)
        val updatedPosition = originalList.indexOf(college)
        collegeDAO.delete(id)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }
}