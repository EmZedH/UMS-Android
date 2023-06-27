package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.ListItemViewHolder
import com.example.ums.R
import com.example.ums.listener.ItemListener
import com.example.ums.model.Test
import com.example.ums.model.databaseAccessObject.TestDAO

class TestListItemViewAdapter(private val studentID: Int, private val courseID: Int, private val departmentID: Int, private val testDAO: TestDAO, private val itemListener: ItemListener) : RecyclerView.Adapter<ListItemViewHolder>() {

    private var originalList : MutableList<Test> = testDAO.getList(studentID, courseID, departmentID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val test = originalList[position]
        holder.itemIDTextView.setText(R.string.id_string)
        holder.itemIDTextView.append(" T/${test.id}")
        holder.itemNameTextView.text = test.mark.toString()

        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(test, holder)
        }
    }

    private fun showOptionsPopupMenu(test : Test, holder: ListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    itemListener.onUpdate(test.id)
                    true
                }
                R.id.delete_college -> {
                    itemListener.onDelete(test.id)
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
                testDAO.getList(studentID, courseID, departmentID).sortedBy { test -> test.id }
            else
                testDAO.getList(studentID, courseID, departmentID).filter { test -> test.mark.toString().contains(query, true) }.sortedBy { test -> test.id }

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
        val test = testDAO.get(id, studentID, courseID, departmentID) ?: return
        for (listTest in originalList){
            if(listTest.id == test.id){
                if(query!=null && query!=""){
                    val flag = test.mark.toString().lowercase().contains(query.lowercase())
                    if(flag){
                        originalList.apply {
                            set(originalList.indexOf(listTest), test)
                            sortBy { it.id }
                            notifyItemChanged(originalList.indexOf(test))
                        }
                        return
                    }
                    else{
                        originalList.apply {
                            notifyItemRemoved(originalList.indexOf(listTest))
                            remove(listTest)
                            sortBy { it.id }
                        }
                        return
                    }
                }
                else{
                    originalList.apply {
                        set(originalList.indexOf(listTest), test)
                        sortBy { it.id }
                        notifyItemChanged(originalList.indexOf(test))
                    }
                    return
                }
            }
        }
    }

    fun addItem(id: Int){
        val query= filterQuery
        val test = testDAO.get(id, studentID, courseID, departmentID)
        if(test!=null){
            if(query!=null && test.mark.toString().lowercase().contains(query.lowercase())){
                originalList.apply {
                    add(test)
                    sortBy { it.id }
                    notifyItemInserted(indexOf(test))
                }
            }
            else if(query==null){
                originalList.add(id-1, test)
                notifyItemInserted(id-1)
            }
        }
    }

    fun deleteItem(id: Int){
        val test = testDAO.get(id, studentID, courseID, departmentID)
        val updatedPosition = originalList.indexOf(test)
        testDAO.delete(id, studentID, courseID, departmentID)
        originalList.removeAt(updatedPosition)
        notifyItemRemoved(updatedPosition)
    }

    fun updateList(){
        originalList = testDAO.getList(studentID, courseID, departmentID).sortedBy { it.id }.toMutableList()
        notifyDataSetChanged()
    }
}