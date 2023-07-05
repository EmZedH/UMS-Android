package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.interfaces.ClickListener
import com.example.ums.model.CourseProfessor
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO

class StudentProfessorsListItemViewAdapter(private val courseID: Int, private val departmentID: Int, private val collegeID: Int, private val courseProfessorDAO: CourseProfessorDAO, private val listener: ClickListener): RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var originalList : MutableList<CourseProfessor> = courseProfessorDAO.getList(courseID, departmentID, collegeID).sortedBy { it.professor.user.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selectable_list_item_layout, parent, false)
        return ClickableListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClickableListItemViewHolder, position: Int) {
        val courseProfessor = originalList[position]
        holder.firstTextView.text = "ID: C/$collegeID-D/${courseProfessor.professor.departmentID}-U/${courseProfessor.professor.user.id}"
        holder.secondTextView.text = courseProfessor.professor.user.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("professor_id", courseProfessor.professor.user.id)
                putInt("course_id", courseProfessor.course.id)
                putInt("department_id", courseProfessor.course.departmentID)
                putInt("college_id", courseProfessor.course.collegeID)
            }
            listener.onClick(bundle)
        }
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    fun filter(query: String?){
        val filteredList =
            if(query.isNullOrEmpty())
                courseProfessorDAO.getList(courseID, departmentID, collegeID).sortedBy { courseProfessor ->  courseProfessor.professor.user.id }
            else
                courseProfessorDAO.getList(courseID, departmentID, collegeID).filter { courseProfessor -> courseProfessor.professor.user.name.contains(query, true) }.sortedBy { courseProfessor ->  courseProfessor.professor.user.id }

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
        val courseProfessor = courseProfessorDAO.get(id, courseID, departmentID, collegeID)
        if(courseProfessor!=null){
            if(query!=null && courseProfessor.professor.user.name.lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(courseProfessor)
                    sortBy {courseProfessor -> courseProfessor.professor.user.id }
                    notifyItemInserted(indexOf(courseProfessor))
                }
            }
            else if(query==null){
                originalList.add(originalList.size, courseProfessor)
                notifyItemInserted(originalList.size)
            }
        }
    }

    fun deleteItem(professorID: Int){
        val courseProfessor = courseProfessorDAO.get(professorID, courseID, departmentID, collegeID)
        val updatedPosition = originalList.indexOf(courseProfessor)
        courseProfessorDAO.delete(professorID, courseID, departmentID, collegeID)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }

    fun updateList(){
        originalList = courseProfessorDAO.getList(courseID, departmentID, collegeID).sortedBy { it.professor.user.id }.toMutableList()
        notifyDataSetChanged()
    }
}