package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.listItemViewHolder.ListItemViewHolder
import com.example.ums.R
import com.example.ums.interfaces.ItemListener
import com.example.ums.model.CollegeAdmin
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO

class CollegeAdminListItemViewAdapter (private val collegeID: Int, private val collegeAdminDAO: CollegeAdminDAO, private val itemListener: ItemListener): RecyclerView.Adapter<ListItemViewHolder>() {

    private var originalList : MutableList<CollegeAdmin> = collegeAdminDAO.getList(collegeID).sortedBy { it.user.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val collegeAdmin = originalList[position]
        holder.itemIDTextView.text = "ID: C/$collegeID-U/${collegeAdmin.user.id}"
        holder.itemNameTextView.text = collegeAdmin.user.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("college_admin_profile_college_id", collegeID)
                putInt("college_admin_profile_college_admin_id", collegeAdmin.user.id)
            }
            itemListener.onClick(bundle)
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(collegeAdmin, holder)
        }
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
                collegeAdminDAO.getList(collegeID).sortedBy { it.user.id }
            else
                collegeAdminDAO.getList(collegeID).filter { collegeAdmin -> collegeAdmin.user.name.contains(query, ignoreCase = true) }.sortedBy { it.user.id }

        filterQuery = if(query.isNullOrEmpty()){
            null
        } else{
            query
        }

        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun addItem(id: Int){
        val query = filterQuery
        val collegeAdmin = collegeAdminDAO.get(id)
        if(collegeAdmin!=null){
            if(query!=null && collegeAdmin.user.name.lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(collegeAdmin)
                    sortBy {collegeAdmin -> collegeAdmin.user.id }
                    notifyItemInserted(indexOf(collegeAdmin))
                }
            }
            else if(query==null){
                originalList.add(collegeAdmin)
                notifyItemInserted(originalList.size)
            }
        }
    }

    fun updateItemInAdapter(id: Int){
        val query = filterQuery
        val collegeAdmin = collegeAdminDAO.get(id) ?: return
        for (listCollegeAdmin in originalList){
            if(listCollegeAdmin.user.id == collegeAdmin.user.id){
                if(query!=null && query!=""){
                    val flag = collegeAdmin.user.name.lowercase().contains(query.lowercase())
                    if(flag){
                        originalList.apply {
                            set(originalList.indexOf(listCollegeAdmin), collegeAdmin)
                            sortBy { it.user.id }
                            notifyItemChanged(originalList.indexOf(collegeAdmin))
                        }
                        return
                    }
                    else{
                        originalList.apply {
                            notifyItemRemoved(originalList.indexOf(listCollegeAdmin))
                            remove(listCollegeAdmin)
                            sortBy { it.user.id }
                        }
                        return
                    }
                }
                else{
                    originalList.apply {
                        set(originalList.indexOf(listCollegeAdmin), collegeAdmin)
                        sortBy { it.user.id }
                        notifyItemChanged(originalList.indexOf(collegeAdmin))
                    }
                    return
                }
            }
        }
    }

    fun deleteItem(id: Int){
        val collegeAdmin = collegeAdminDAO.get(id)
        val updatedPosition = originalList.indexOf(collegeAdmin)
        collegeAdminDAO.delete(id)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }
}