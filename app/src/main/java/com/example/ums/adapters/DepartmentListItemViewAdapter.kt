package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.listItemViewHolder.ListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ItemListener
import com.example.ums.model.Department
import com.example.ums.model.databaseAccessObject.DepartmentDAO

class DepartmentListItemViewAdapter(private val collegeID: Int, private val departmentDAO: DepartmentDAO, private val itemListener: ItemListener): RecyclerView.Adapter<ListItemViewHolder>() {

    private var originalList : MutableList<Department> = departmentDAO.getList(collegeID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val department = originalList[position]
        holder.itemIDTextView.text = "ID: C/$collegeID-D/${department.id}"
        holder.itemNameTextView.text = department.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("departmentID",department.id)
            bundle.putInt("collegeID", collegeID)
            itemListener.onClick(bundle)
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(department, holder)
        }
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
                departmentDAO.getList(collegeID).sortedBy { department ->  department.id }
            else
                departmentDAO.getList(collegeID).filter { department -> department.name.contains(query, true) }.sortedBy { department ->  department.id }

        filterQuery = if(query.isNullOrEmpty()){
                null
            } else{
                query
            }
        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun updateItemInAdapter(id: Int) {
        val query = filterQuery
        val department = departmentDAO.get(id, collegeID) ?: return
        for (listDepartment in originalList){
            if(listDepartment.id == id){
                if(query!=null && query!=""){
                    val flag = department.name.lowercase().contains(query.lowercase())
                    if(flag){
                        originalList.apply {
                            set(originalList.indexOf(listDepartment), department)
                            sortBy { it.id }
                            notifyItemChanged(originalList.indexOf(department))
                        }
                        return
                    }
                    else{
                        originalList.apply {
                            notifyItemRemoved(originalList.indexOf(listDepartment))
                            remove(listDepartment)
                            sortBy { it.id }
                        }
                        return
                    }
                }
                else{
                    originalList.apply {
                        set(originalList.indexOf(listDepartment), department)
                        sortBy { it.id }
                        notifyItemChanged(originalList.indexOf(department))
                    }
                    return
                }
            }
        }
    }

    fun addItem(id: Int){
        val query = filterQuery
        val department = departmentDAO.get(id, collegeID)
        if(department!=null){
            if(query!=null && department.name.lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(department)
                    sortBy {department -> department.id }
                    notifyItemInserted(indexOf(department))
                }
            }
            else if(query==null){
                originalList.add(id-1, department)
                notifyItemInserted(id-1)
            }
        }
    }

    fun deleteItem(id: Int){
        val department = departmentDAO.get(id, collegeID)
        val updatedPosition = originalList.indexOf(department)
        departmentDAO.delete(id, collegeID)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }

    fun updateList(){
        originalList = departmentDAO.getList(collegeID).sortedBy { it.id }.toMutableList()
        notifyDataSetChanged()
    }
}