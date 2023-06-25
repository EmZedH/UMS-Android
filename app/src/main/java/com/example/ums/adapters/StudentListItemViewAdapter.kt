package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.ListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ItemListener
import com.example.ums.model.Student
import com.example.ums.model.databaseAccessObject.StudentDAO

class StudentListItemViewAdapter (private val departmentID: Int, private val collegeID: Int, private val studentDAO: StudentDAO, private val itemListener: ItemListener): RecyclerView.Adapter<ListItemViewHolder>() {

    private var originalList : MutableList<Student> = studentDAO.getList(departmentID, collegeID).sortedBy { it.user.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val student = originalList[position]
        holder.itemIDTextView.text = "ID: C/$collegeID-D/$departmentID-U/${student.user.id}"
        holder.itemNameTextView.text = student.user.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("student_profile_department_id", departmentID)
                putInt("student_profile_college_id", collegeID)
                putInt("student_profile_professor_id", student.user.id)
            }
            itemListener.onClick(bundle)
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(student, holder)
        }
    }

    private fun showOptionsPopupMenu(student : Student, holder: ListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    itemListener.onUpdate(student.user.id)
                    true
                }
                R.id.delete_college -> {
                    itemListener.onDelete(student.user.id)
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
                studentDAO.getList(departmentID, collegeID).sortedBy { it.user.id }
            else
                studentDAO.getList(departmentID, collegeID).filter { professor -> professor.user.name.contains(query, ignoreCase = true) }.sortedBy { it.user.id }

        filterQuery = if(query.isNullOrEmpty()) null else query

        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun addItem(id: Int){
        val query = filterQuery
        val student = studentDAO.get(id)
        if(student!=null){
            if(query!=null && student.user.name.lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(student)
                    sortBy {professor -> professor.user.id }
                    notifyItemInserted(indexOf(student))
                }
            }
            else if(query==null){
                originalList.add(student)
                notifyItemInserted(originalList.size)
            }
        }
    }

    fun updateItemInAdapter(id: Int){
        val query = filterQuery
        val student = studentDAO.get(id) ?: return
        for (listStudent in originalList){
            if(listStudent.user.id == student.user.id){
                if(query!=null && query!=""){
                    val flag = student.user.name.lowercase().contains(query.lowercase())
                    if(flag){
                        originalList.apply {
                            set(originalList.indexOf(listStudent), student)
                            sortBy { it.user.id }
                            notifyItemChanged(originalList.indexOf(student))
                        }
                        return
                    }
                    else{
                        originalList.apply {
                            notifyItemRemoved(originalList.indexOf(listStudent))
                            remove(listStudent)
                            sortBy { it.user.id }
                        }
                        return
                    }
                }
                else{
                    originalList.apply {
                        set(originalList.indexOf(listStudent), student)
                        sortBy { it.user.id }
                        notifyItemChanged(originalList.indexOf(student))
                    }
                    return
                }
            }
        }
    }

    fun deleteItem(id: Int){
        val student = studentDAO.get(id)
        val updatedPosition = originalList.indexOf(student)
        studentDAO.delete(id)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }
}