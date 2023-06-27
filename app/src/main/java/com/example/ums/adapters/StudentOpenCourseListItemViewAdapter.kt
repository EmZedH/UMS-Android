package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.DeletableListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.DeleteClickListener
import com.example.ums.model.Course
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO

class StudentOpenCourseListItemViewAdapter (private val studentID: Int, private val courseDAO: CourseDAO, private val recordsDAO: RecordsDAO, private val listener: DeleteClickListener): RecyclerView.Adapter<DeletableListItemViewHolder>() {

    private var originalList : MutableList<Course> = courseDAO.getOpenCourses(studentID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.deletable_list_item_layout, parent, false)
        return DeletableListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: DeletableListItemViewHolder, position: Int) {
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
        holder.deleteButton.setOnClickListener {
            listener.onDelete(position)
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

    fun addItem(id: Int){
        val query = filterQuery
        val record = recordsDAO.get(studentID, id)
        val course = courseDAO.get(id, record?.courseProfessor?.course?.departmentID, record?.courseProfessor?.course?.collegeID)
        if(course!=null){
            if(query!=null && course.semester.toString().lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(course)
                    sortBy {course -> course.id }
                    notifyItemInserted(indexOf(course))
                }
            }
            else if(query==null){
                originalList.add(course)
                notifyItemInserted(originalList.size)
            }
        }
    }

    fun deleteItem(position: Int){
        val course = originalList[position]
        recordsDAO.delete(studentID, course.id, course.departmentID)
        originalList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateList(){
        originalList = courseDAO.getOpenCourses(studentID).sortedBy { it.id }.toMutableList()
        notifyDataSetChanged()
    }
}