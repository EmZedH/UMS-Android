package com.example.ums.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.ListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ItemListener
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO

class CollegeListItemViewAdapter(private val collegeDAO: CollegeDAO, private val itemListener: ItemListener) : RecyclerView.Adapter<ListItemViewHolder>() {

    private var originalList : MutableList<College> = collegeDAO.getList().sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val college = originalList[position]
        holder.itemIDTextView.setText(R.string.college_id)
        holder.itemIDTextView.append(college.id.toString())
        holder.itemNameTextView.text = college.name

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("collegeID",college.id)
            itemListener.onClick(bundle)
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(college, holder)
        }
    }

    private fun showOptionsPopupMenu(college : College, holder: ListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    itemListener.onUpdate(college.id)
                    true
                }
                R.id.delete_college -> {
                    itemListener.onDelete(college.id)
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
                collegeDAO.getList().sortedBy {college -> college.id }
            else
                collegeDAO.getList().filter { college -> college.name.contains(query, true) }.sortedBy {college -> college.id }

        filterQuery = if(query.isNullOrEmpty()){
            null
        } else{
            query
        }
        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }

    fun updateItemInAdapter(id: Int) {
        val query = filterQuery
        val college = collegeDAO.get(id) ?: return
        for (listCollege in originalList){
            if(listCollege.id == college.id){
                if(query!=null && query!=""){
                    val flag = college.name.lowercase().contains(query.lowercase())
                    if(flag){
                        originalList.apply {
                            set(originalList.indexOf(listCollege), college)
                            sortBy { it.id }
                            notifyItemChanged(originalList.indexOf(college))
                        }
                        return
                    }
                    else{
                        originalList.apply {
                            notifyItemRemoved(originalList.indexOf(listCollege))
                            remove(listCollege)
                            sortBy { it.id }
                        }
                        return
                    }
                }
                else{
                    originalList.apply {
                        set(originalList.indexOf(listCollege), college)
                        sortBy { it.id }
                        notifyItemChanged(originalList.indexOf(college))
                    }
                    return
                }
            }
        }
    }

    fun addItem(id: Int){
        val query= filterQuery
        val college = collegeDAO.get(id)
        if(college!=null){
            if(query!=null && college.name.lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(college)
                    sortBy { it.id }
                    notifyItemInserted(indexOf(college))
                }
            }
            else if(query==null){
                originalList.add(id-1, college)
                notifyItemInserted(id-1)
            }
        }
    }

    fun deleteItem(id: Int){
        val college = collegeDAO.get(id)
        val updatedPosition = originalList.indexOf(college)
        collegeDAO.delete(id)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }
}