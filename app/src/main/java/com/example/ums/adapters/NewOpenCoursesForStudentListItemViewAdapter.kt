package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.listener.ClickListener
import com.example.ums.model.Course
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.StudentDAO

class NewOpenCoursesForStudentListItemViewAdapter(private val studentID: Int, private val courseDAO: CourseDAO, private val studentDAO: StudentDAO, private val clickListener: ClickListener) : RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var originalList : MutableList<Course> = courseDAO.getNewOpenCourses(studentID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selectable_list_item_layout, parent, false)
        return ClickableListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClickableListItemViewHolder, position: Int) {
        val course = originalList[position]
        holder.firstTextView.text = "ID: C/${course.collegeID}-D/${course.departmentID}-CO/${course.id}"
        holder.secondTextView.text = course.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("course_professor_list_activity_department_id", course.departmentID)
                putInt("course_professor_list_activity_college_id", course.collegeID)
                putInt("course_professor_list_activity_course_id", course.id)
            }
            clickListener.onClick(bundle)
        }
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    fun filter(query: String?){
        val filteredList =
            if(query.isNullOrEmpty())
                courseDAO.getNewOpenCourses(studentID).sortedBy { it.id }.toMutableList()
            else
                courseDAO.getNewOpenCourses(studentID).filter { course -> course.name.contains(query, true) }.sortedBy { course ->  course.id }

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
        val student = studentDAO.get(studentID) ?: return
        val course = courseDAO.get(id, student.departmentID, student.collegeID)
        if(course!=null){
            if(query!=null && course.name.lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(course)
                    sortBy {course -> course.id }
                    notifyItemInserted(indexOf(course))
                }
            }
            else if(query==null){
                originalList.add(originalList.size, course)
                notifyItemInserted(originalList.size)
            }
        }
    }

    fun updateList(){
        originalList = courseDAO.getNewOpenCourses(studentID).sortedBy { it.id }.toMutableList()
        notifyDataSetChanged()
    }
}