package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.interfaces.ItemListener
import com.example.ums.listItemViewHolder.ListItemViewHolder
import com.example.ums.model.Course
import com.example.ums.model.databaseAccessObject.CourseDAO

class CourseListItemViewAdapter(private val departmentID: Int, private val collegeID: Int, private val courseDAO: CourseDAO, private val itemListener: ItemListener): RecyclerView.Adapter<ListItemViewHolder>()  {
    private var originalList : MutableList<Course> = courseDAO.getList(departmentID, collegeID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val course = originalList[position]
        holder.itemIDTextView.text = "ID: C/$collegeID-D/${course.departmentID}-CO/${course.id}"
        holder.itemNameTextView.text = course.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("courseID", course.id)
            bundle.putInt("departmentID", departmentID)
            bundle.putInt("collegeID", collegeID)
            itemListener.onClick(bundle)
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(course, holder)
        }
    }

    private fun showOptionsPopupMenu(course : Course, holder: ListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    itemListener.onUpdate(course.id)
                    true
                }
                R.id.delete_college -> {
                    itemListener.onDelete(course.id)
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
                courseDAO.getList(departmentID, collegeID).sortedBy { course ->  course.id }
            else
                courseDAO.getList(departmentID, collegeID).filter { course -> course.name.contains(query, true) }.sortedBy { course ->  course.id }

        filterQuery = if(query.isNullOrEmpty()) null else query

        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun updateItemInAdapter(id: Int) {
        val query = filterQuery
        val course = courseDAO.get(id, departmentID, collegeID) ?: return
        for (listCourse in originalList){
            if(listCourse.id == id){
                if(query!=null && query!=""){
                    val flag = course.name.lowercase().contains(query.lowercase())
                    if(flag){
                        originalList.apply {
                            set(originalList.indexOf(listCourse), course)
                            sortBy { it.id }
                            notifyItemChanged(originalList.indexOf(course))
                        }
                        return
                    }
                    else{
                        originalList.apply {
                            notifyItemRemoved(originalList.indexOf(listCourse))
                            remove(listCourse)
                            sortBy { it.id }
                        }
                        return
                    }
                }
                else{
                    originalList.apply {
                        set(originalList.indexOf(listCourse), course)
                        sortBy { it.id }
                        notifyItemChanged(originalList.indexOf(course))
                    }
                    return
                }
            }
        }
    }

    fun addItem(id: Int){
        val query = filterQuery
        val course = courseDAO.get(id, departmentID, collegeID)
        if(course!=null){
            if(query!=null && course.name.lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(course)
                    sortBy {course -> course.id }
                    notifyItemInserted(indexOf(course))
                }
            }
            else if(query==null){
                originalList.add(id-1, course)
                notifyItemInserted(id-1)
            }
        }
    }

    fun deleteItem(id: Int){
        val course = courseDAO.get(id, departmentID, collegeID)
        val updatedPosition = originalList.indexOf(course)
        courseDAO.delete(id, departmentID, collegeID)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }

    fun updateList(){
        originalList = courseDAO.getList(departmentID, collegeID).sortedBy { it.id }.toMutableList()
        notifyDataSetChanged()
    }
}