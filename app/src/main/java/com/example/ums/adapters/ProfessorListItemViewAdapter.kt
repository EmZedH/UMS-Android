package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.listItemViewHolder.ListItemViewHolder
import com.example.ums.R
import com.example.ums.interfaces.ItemListener
import com.example.ums.model.Professor
import com.example.ums.model.databaseAccessObject.ProfessorDAO

class ProfessorListItemViewAdapter (private val departmentID: Int, private val collegeID: Int, private val professorDAO: ProfessorDAO, private val itemListener: ItemListener): RecyclerView.Adapter<ListItemViewHolder>() {

    private var originalList : MutableList<Professor> = professorDAO.getList(departmentID, collegeID).sortedBy { it.user.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val professor = originalList[position]
        holder.itemIDTextView.text = "ID: C/$collegeID-D/$departmentID-U/${professor.user.id}"
        holder.itemNameTextView.text = professor.user.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("professor_profile_department_id", departmentID)
                putInt("professor_profile_college_id", collegeID)
                putInt("professor_profile_professor_id", professor.user.id)
            }
            itemListener.onClick(bundle)
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(professor, holder)
        }
    }

    private fun showOptionsPopupMenu(professor : Professor, holder: ListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    itemListener.onUpdate(professor.user.id)
                    true
                }
                R.id.delete_college -> {
                    itemListener.onDelete(professor.user.id)
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
                professorDAO.getList(departmentID, collegeID).sortedBy { it.user.id }
            else
                professorDAO.getList(departmentID, collegeID).filter { professor -> professor.user.name.contains(query, ignoreCase = true) }.sortedBy { it.user.id }

        filterQuery = if(query.isNullOrEmpty()) null else query

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
                originalList.add(professor)
                notifyItemInserted(originalList.size)
            }
        }
    }

    fun updateItemInAdapter(id: Int){
        val query = filterQuery
        val professor = professorDAO.get(id) ?: return
        for (listProfessor in originalList){
            if(listProfessor.user.id == professor.user.id){
                if(query!=null && query!=""){
                    val flag = professor.user.name.lowercase().contains(query.lowercase())
                    if(flag){
                        originalList.apply {
                            set(originalList.indexOf(listProfessor), professor)
                            sortBy { it.user.id }
                            notifyItemChanged(originalList.indexOf(professor))
                        }
                        return
                    }
                    else{
                        originalList.apply {
                            notifyItemRemoved(originalList.indexOf(listProfessor))
                            remove(listProfessor)
                            sortBy { it.user.id }
                        }
                        return
                    }
                }
                else{
                    originalList.apply {
                        set(originalList.indexOf(listProfessor), professor)
                        sortBy { it.user.id }
                        notifyItemChanged(originalList.indexOf(professor))
                    }
                    return
                }
            }
        }
    }

    fun deleteItem(id: Int){
        val professor = professorDAO.get(id)
        val updatedPosition = originalList.indexOf(professor)
        professorDAO.delete(id)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }

    fun updateList(){
        originalList = professorDAO.getList(departmentID, collegeID).sortedBy { it.user.id }.toMutableList()
        notifyDataSetChanged()
    }
}