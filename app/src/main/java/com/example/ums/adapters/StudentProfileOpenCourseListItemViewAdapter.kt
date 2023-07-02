package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.listener.DeleteClickListener
import com.example.ums.model.Course
import com.example.ums.model.databaseAccessObject.CourseDAO

class StudentProfileOpenCourseListItemViewAdapter(
    private val studentID: Int,
    private val courseDAO: CourseDAO,
    private val listener: DeleteClickListener
): RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var originalList : MutableList<Course> = courseDAO.getOpenCourses(studentID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selectable_list_item_layout, parent, false)
        return ClickableListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ClickableListItemViewHolder, position: Int) {
        val course = originalList[position]
        holder.firstTextView.text = "ID: C/${course.collegeID}-D/${course.departmentID}-CO/${course.id}"
        holder.secondTextView.text = course.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("studentID", studentID)
                putInt("courseID", course.id)
                putInt("departmentID", course.departmentID)
            }
            listener.onClick(bundle)
        }
    }

    fun filter(query: String?){
        val filteredList =
            if(query.isNullOrEmpty())
                courseDAO.getOpenCourses(studentID).sortedBy { it.id }
            else
                courseDAO.getOpenCourses(studentID).filter { transaction -> transaction.name.contains(query, ignoreCase = true) }.sortedBy { it.id }

        filterQuery = if(query.isNullOrEmpty()) null else query

        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun updateList(){
        originalList = courseDAO.getOpenCourses(studentID).sortedBy { it.id }.toMutableList()
        notifyDataSetChanged()
    }
}