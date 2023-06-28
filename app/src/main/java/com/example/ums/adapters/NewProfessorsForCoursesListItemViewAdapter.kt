package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ClickListener
import com.example.ums.model.CourseProfessor
import com.example.ums.model.Professor
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.ProfessorDAO

class NewProfessorsForCoursesListItemViewAdapter(private val courseID: Int, private val departmentID: Int, private val collegeID: Int, private val professorDAO: ProfessorDAO, private val courseProfessorDAO: CourseProfessorDAO, private val courseDAO: CourseDAO, private val clickListener: ClickListener): RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var originalList : MutableList<Professor> = professorDAO.getNewProfessors(courseID, departmentID, collegeID).sortedBy { it.user.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selectable_list_item_layout, parent, false)
        return ClickableListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClickableListItemViewHolder, position: Int) {
        val professor = originalList[position]
        holder.firstTextView.text = "ID: C/$collegeID-D/${professor.departmentID}-U/${professor.user.id}"
        holder.secondTextView.text = professor.user.name
        holder.itemView.setOnClickListener {

            courseProfessorDAO.insert(CourseProfessor(professor, courseDAO.get(courseID, departmentID, collegeID) ?: return@setOnClickListener))
            val updatedPosition = originalList.indexOf(professor)
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
                professorDAO.getNewProfessors(courseID, departmentID, collegeID).sortedBy { professor ->  professor.user.id }
            else
                professorDAO.getNewProfessors(courseID, departmentID, collegeID).filter { professor -> professor.user.name.contains(query, true) }.sortedBy { professor ->  professor.user.id }

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
        val professor = professorDAO.get(id)
        if(professor!=null){
            if(query!=null && professor.user.name.lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(professor)
                    sortBy {professor -> professor.user.id }
                    notifyItemInserted(indexOf(professor))
                }
            }
            else if(query==null){
                originalList.add(originalList.size, professor)
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