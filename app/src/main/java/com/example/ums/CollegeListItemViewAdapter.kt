package com.example.ums

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO

class CollegeListItemViewAdapter(private val collegeList : MutableList<College>, private val collegeDAO: CollegeDAO, private val fragment: Fragment) : RecyclerView.Adapter<ListItemViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return ListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return collegeList.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val college = collegeList[position]
        holder.itemIDTextView.text = "ID : C/${college.collegeID}"
        holder.itemNameTextView.text = college.collegeName

        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(college, holder, position)
        }
    }

    private fun showOptionsPopupMenu(college : College, holder: ListItemViewHolder, position: Int){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    // Handle edit option
                    true
                }
                R.id.delete_college -> {
                    // Handle delete option
                    showConfirmationDialog(context, college, position)
                    true
                }
                // Add more menu item cases as needed

                else -> {
                    false
                }
            }}
        popupMenu.show()
    }
    private fun showConfirmationDialog(context: Context, college: College, position: Int) {
        val builder = AlertDialog.Builder(context)

        // Set the dialog title and message
        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to delete this college?")

        // Set the positive button (delete)
        builder.setPositiveButton("Delete") { dialog, _ ->
            // Perform the delete operation
            collegeDAO.delete(college.collegeID)
            collegeList.removeAt(position)
            notifyItemRemoved(position)
            dialog.dismiss()

            if(collegeList.isEmpty()){
                val noCollegeTextView = fragment.requireView().findViewById<TextView>(R.id.no_colleges_text_view)
                val tapAddButtonTextView = fragment.requireView().findViewById<TextView>(R.id.add_to_get_started_text_view)

                noCollegeTextView.visibility = View.VISIBLE
                tapAddButtonTextView.visibility = View.VISIBLE
            }
        }

        // Set the negative button (cancel)
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }

}