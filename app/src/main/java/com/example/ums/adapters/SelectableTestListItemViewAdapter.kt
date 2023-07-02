package com.example.ums.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R
import com.example.ums.listItemViewHolder.ClickableListItemViewHolder
import com.example.ums.model.Test
import com.example.ums.model.databaseAccessObject.TestDAO

class SelectableTestListItemViewAdapter(
    private val studentID: Int,
    private val courseID: Int,
    private val departmentID: Int,
    private val testDAO: TestDAO
) : RecyclerView.Adapter<ClickableListItemViewHolder>() {

    private var originalList : MutableList<Test> = testDAO.getList(studentID, courseID, departmentID).sortedBy { it.id }.toMutableList()
    private var filterQuery: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickableListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.selectable_list_item_layout, parent, false)
        return ClickableListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: ClickableListItemViewHolder, position: Int) {
        val test = originalList[position]
        holder.firstTextView.setText(R.string.id_string)
        holder.firstTextView.append(" T/${test.id}")
        holder.secondTextView.text = test.mark.toString()
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

    fun updateList(){
        originalList = testDAO.getList(studentID, courseID, departmentID).sortedBy { it.id }.toMutableList()
        notifyDataSetChanged()
    }
}