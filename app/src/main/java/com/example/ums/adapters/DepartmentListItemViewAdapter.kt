package com.example.ums.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.ListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ItemListener
import com.example.ums.model.Department
import com.example.ums.model.databaseAccessObject.DepartmentDAO

class DepartmentListItemViewAdapter(private val collegeID: Int, private val departmentDAO: DepartmentDAO, private val itemListener: ItemListener): RecyclerView.Adapter<ListItemViewHolder>() {

    private var originalList : MutableList<Department> = departmentDAO.getList(collegeID).toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val college = originalList[position]
        holder.itemIDTextView.text = "ID: C/$collegeID-D/${college.id}"
        holder.itemNameTextView.text = college.name

//        holder.itemView.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putInt("collegeID",college.id)
//            itemListener.onClick(bundle)
//        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(college, holder)
        }
    }

    fun updateItemInAdapter(position: Int) {
        originalList[position] = departmentDAO.get(position+1, collegeID)!!
        notifyItemChanged(position)
    }

    private fun showOptionsPopupMenu(department : Department, holder: ListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    itemListener.onUpdate(department.id)
                    true
                }
                R.id.delete_college -> {
                    itemListener.onDelete(department.id)
//                    showConfirmationDialog(context, college)
                    true
                }

                else -> {
                    false
                }
            }}
        popupMenu.show()
    }

    fun filter(query: String){
        val filteredList =
            if(query.isEmpty())
                departmentDAO.getList(collegeID)
            else
                departmentDAO.getList(collegeID).filter { item -> item.name.contains(query, ignoreCase = true) }

        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }
    fun addItem(position: Int){
        originalList.add(position, departmentDAO.get(position+1, collegeID)!!)
    }

    fun deleteItem(id: Int){
        val department = departmentDAO.get(id, collegeID)
        val updatedPosition = originalList.indexOf(department)
        departmentDAO.delete(id)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }
}