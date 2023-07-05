package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.interfaces.ClickListener
import com.example.ums.model.Student
import com.example.ums.model.databaseAccessObject.ProfessorDAO
import com.example.ums.model.databaseAccessObject.StudentDAO

class CourseStudentListItemViewAdapter(private val professorID: Int, private val courseID: Int, private val studentDAO: StudentDAO, private val professorDAO: ProfessorDAO, private val listener: ClickListener): RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var originalList : MutableList<Student> = studentDAO.getNewCurrentStudentsList(professorID, courseID).sortedBy { it.user.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selectable_list_item_layout, parent, false)
        return ClickableListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClickableListItemViewHolder, position: Int) {
        val student = originalList[position]
        holder.firstTextView.text = "ID: C/${student.collegeID}-D/${student.departmentID}-U/${student.user.id}"
        holder.secondTextView.text = student.user.name
        holder.itemView.setOnClickListener {
            val professor = professorDAO.get(professorID)
            val bundle = Bundle().apply {
                putInt("studentID", student.user.id)
                putInt("courseID", courseID)
                putInt("departmentID", professor?.departmentID ?: return@setOnClickListener)
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
                studentDAO.getNewCurrentStudentsList(professorID, courseID).sortedBy { student ->  student.user.id }
            else
                studentDAO.getNewCurrentStudentsList(professorID, courseID).filter { student -> student.user.name.contains(query, true) }.sortedBy { student ->  student.user.id }

        filterQuery = if(query.isNullOrEmpty()){
            null
        } else{
            query
        }
        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun onRefresh(){
        originalList = studentDAO.getNewCurrentStudentsList(professorID, courseID).sortedBy { it.user.id }.toMutableList()
        notifyDataSetChanged()
    }
}