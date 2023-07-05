package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.R
import com.example.ums.interfaces.ClickListener
import com.example.ums.model.Course
import com.example.ums.model.CourseProfessor
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.ProfessorDAO

class NewCoursesForProfessorListItemViewAdapter(private val professorID: Int, private val professorDAO: ProfessorDAO, private val courseProfessorDAO: CourseProfessorDAO, private val courseDAO: CourseDAO, private val clickListener: ClickListener): RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var originalList : MutableList<Course> = courseDAO.getNewCourses(professorID).sortedBy { it.id }.toMutableList()
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

            courseProfessorDAO.insert(CourseProfessor(professorDAO.get(professorID) ?: return@setOnClickListener, course))
            val updatedPosition = originalList.indexOf(course)
            originalList.removeAt(updatedPosition)
            notifyItemRemoved(updatedPosition)
            clickListener.onClick(null)
        }
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    fun filter(query: String?){
        val filteredList =
            if(query.isNullOrEmpty())
                courseDAO.getNewCourses(professorID).sortedBy { course ->  course.id }
            else
                courseDAO.getNewCourses(professorID).filter { course -> course.name.contains(query, true) }.sortedBy { course ->  course.id }

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
        val professor = professorDAO.get(professorID) ?: return
        val course = courseDAO.get(id, professor.departmentID, professor.collegeID)
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

//    fun deleteItem(professorID: Int){
//        val department = professorDAO.get(professorID, courseID, departmentID, collegeID)
//        val updatedPosition = originalList.indexOf(department)
//        professorDAO.delete(professorID, courseID, departmentID, collegeID)
//        originalList.removeAt(updatedPosition)
//        notifyItemRemoved(updatedPosition)
//    }
}