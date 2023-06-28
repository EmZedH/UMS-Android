package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.listener.ClickListener
import com.example.ums.model.CourseProfessor
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.ProfessorDAO

class ProfessorMainPageListItemViewAdapter(private val professorID: Int, private val courseProfessorDAO: CourseProfessorDAO, private val professorDAO: ProfessorDAO, private val listener: ClickListener): RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var originalList : MutableList<CourseProfessor> = courseProfessorDAO.getList(professorID).sortedBy { it.professor.user.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selectable_list_item_layout, parent, false)
        return ClickableListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClickableListItemViewHolder, position: Int) {
        val courseProfessor = originalList[position]
        holder.firstTextView.text = "ID: C/${courseProfessor.professor.collegeID}-D/${courseProfessor.course.departmentID}-CO/${courseProfessor.course.id}"
        holder.secondTextView.text = courseProfessor.course.name

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("professor_id", professorID)
                putInt("course_id", courseProfessor.course.id)
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
                courseProfessorDAO.getList(professorID).sortedBy { courseProfessor ->  courseProfessor.course.id }
            else
                courseProfessorDAO.getList(professorID).filter { courseProfessor -> courseProfessor.course.name.contains(query, true) }.sortedBy { courseProfessor ->  courseProfessor.course.id }

        filterQuery = if(query.isNullOrEmpty()){
            null
        } else{
            query
        }
        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun addItem(courseID: Int){
        val professor = professorDAO.get(professorID) ?: return
        val query = filterQuery
        val courseProfessor = courseProfessorDAO.get(professorID, courseID, professor.departmentID, professor.collegeID)
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

    fun deleteItem(courseID: Int){
        val professor = professorDAO.get(professorID) ?: return
        val courseProfessor = courseProfessorDAO.get(professorID, courseID, professor.departmentID, professor.collegeID)
        val updatedPosition = originalList.indexOf(courseProfessor)
        courseProfessorDAO.delete(professorID, courseID, professor.departmentID, professor.collegeID)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }

    fun onRefresh(){
        originalList = courseProfessorDAO.getList(professorID).sortedBy { it.course.id }.toMutableList()
        notifyDataSetChanged()
    }
}