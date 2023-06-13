package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.ListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ItemListener
import com.example.ums.model.CollegeAdmin
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO

class CollegeAdminListItemViewAdapter (private val collegeID: Int, private val collegeAdminDAO: CollegeAdminDAO, private val itemListener: ItemListener): RecyclerView.Adapter<ListItemViewHolder>() {

    private var originalList : MutableList<CollegeAdmin> = collegeAdminDAO.getList(collegeID).toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val collegeAdmin = originalList[position]
        holder.itemIDTextView.text = "ID: C/$collegeID-D/${collegeAdmin.user.id}"
        holder.itemNameTextView.text = collegeAdmin.user.name

        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(collegeAdmin, holder)
        }
    }

//    fun updateItemInAdapter(position: Int) {
//        originalList[position] = collegeAdminDAO.get(position+1, collegeID)!!
//        notifyItemChanged(position)
//    }

    fun updateItemInAdapter(position: Int, id: Int){
        originalList[position] = collegeAdminDAO.get(id)!!
        notifyItemChanged(position)
    }

    private fun showOptionsPopupMenu(collegeAdmin : CollegeAdmin, holder: ListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    itemListener.onUpdate(collegeAdmin.user.id)
                    true
                }
                R.id.delete_college -> {
                    itemListener.onDelete(collegeAdmin.user.id)
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
                collegeAdminDAO.getList(collegeID)
            else
                collegeAdminDAO.getList(collegeID).filter { collegeAdmin -> collegeAdmin.user.name.contains(query, ignoreCase = true) }

        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

//    fun addItem(position: Int){
//        originalList.add(position, collegeAdminDAO.get(position+1, collegeID)!!)
//    }

    fun addItem(id: Int){
        originalList.add(originalList.size, collegeAdminDAO.get(id)!!)
        notifyItemInserted(originalList.size)
    }

//    fun deleteItem(id: Int){
//        val department = collegeAdminDAO.get(id, collegeID)
//        val updatedPosition = originalList.indexOf(department)
//        collegeAdminDAO.delete(id)
//        originalList.removeAt(updatedPosition)
//        notifyItemRemoved(updatedPosition)
//    }

    fun deleteItem(id: Int){
        val department = collegeAdminDAO.get(id)
        val updatedPosition = originalList.indexOf(department)
        collegeAdminDAO.delete(id)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }
}