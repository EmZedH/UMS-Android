package com.example.ums

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.listener.FragmentRefreshListener
import com.example.ums.listener.CollegeEditListener
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO

class CollegeListItemViewAdapter(private val collegeDAO: CollegeDAO, private val deleteListener: FragmentRefreshListener, private val editCollegeListener: CollegeEditListener) : RecyclerView.Adapter<CollegeListItemViewAdapter.CollegeListItemViewHolder>() {

    private var originalList : MutableList<College> = collegeDAO.getList().toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollegeListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return CollegeListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return originalList.size
    }

    override fun onBindViewHolder(holder: CollegeListItemViewHolder, position: Int) {
        val college = originalList[position]
        holder.itemIDTextView.setText(R.string.college_id_string)
        holder.itemIDTextView.append(college.id.toString())
        holder.itemNameTextView.text = college.name

        holder.itemView.setOnClickListener {
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(college, holder)
        }
    }

    fun updateItemInAdapter(position: Int) {
        originalList[position] = collegeDAO.get(position+1)!!
        notifyItemChanged(position)
    }

    private fun showOptionsPopupMenu(college : College, holder: CollegeListItemViewHolder){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    editCollegeListener.updateItemInAdapter(college.id)
                    true
                }
                R.id.delete_college -> {
                    showConfirmationDialog(context, college)
                    true
                }

                else -> {
                    false
                }
            }}
        popupMenu.show()
    }
    private fun showConfirmationDialog(context: Context, college: College) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to delete this college?")

        builder.setPositiveButton("Delete") { dialog, _ ->
            val updatedPosition = originalList.indexOf(college)
            collegeDAO.delete(college.id)
            originalList.removeAt(updatedPosition)
            notifyItemRemoved(updatedPosition)
            deleteListener.onRefresh()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun filter(query: String){
        val filteredList =
            if(query.isEmpty())
                collegeDAO.getList()
            else
                collegeDAO.getList().filter { item -> item.name.contains(query, ignoreCase = true) }

        originalList.clear()
        originalList.addAll(filteredList)
        notifyDataSetChanged()
    }
    fun addItem(position: Int){
        originalList.add(position, collegeDAO.get(position+1)!!)
    }

    inner class CollegeListItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val optionsButton : ImageButton = itemView.findViewById(R.id.options_button)
        val itemIDTextView : TextView = itemView.findViewById(R.id.element_id)
        val itemNameTextView : TextView = itemView.findViewById(R.id.element_name)
    }
}