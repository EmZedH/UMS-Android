package com.example.ums

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.Listeners.EditCollegeListener
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO

class CollegeListItemViewAdapter(private val collegeDAO: CollegeDAO) : RecyclerView.Adapter<CollegeListItemViewAdapter.CollegeListItemViewHolder>(),
    EditCollegeListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollegeListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout, parent, false)
        return CollegeListItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return collegeDAO.getList().size
    }

    override fun onBindViewHolder(holder: CollegeListItemViewHolder, position: Int) {
        val college = collegeDAO.getList()[position]
        holder.itemIDTextView.setText(R.string.college_id_string)
        holder.itemIDTextView.append(college.id.toString())
        holder.itemNameTextView.text = college.name

        holder.itemView.setOnClickListener {
//            holder.itemNameTextView.text = "PRESSED"
        }
        holder.optionsButton.setOnClickListener {
            showOptionsPopupMenu(college, holder, position)
        }
    }

    private fun showOptionsPopupMenu(college : College, holder: CollegeListItemViewHolder, position: Int){
        val context = holder.itemView.context
        val popupMenu = PopupMenu(context, holder.optionsButton)

        popupMenu.inflate(R.menu.edit_delete_menu)

        popupMenu.setOnMenuItemClickListener{menuItem ->
            when (menuItem.itemId) {
                R.id.edit_college -> {
                    val editFragment = EditCollegeBottomSheet(collegeDAO, college.id, this)
                    editFragment.show((context as AppCompatActivity).supportFragmentManager, "bottomSheetDialog")
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
            val updatedPosition = collegeDAO.getList().indexOf(college)
            collegeDAO.delete(college.id)

            notifyItemRemoved(updatedPosition)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    inner class CollegeListItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val optionsButton : ImageButton = itemView.findViewById(R.id.options_button)
        val itemIDTextView : TextView = itemView.findViewById(R.id.element_id)
        val itemNameTextView : TextView = itemView.findViewById(R.id.element_name)
    }

    override fun updateItemInAdapter(position: Int) {
        Log.i("SuperAdminMainPagerefreshFragment","position $position")
        notifyItemChanged(position)
    }
}