package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class CourseDeleteDialog: DialogFragment() {

    private var courseID: Int? = null

    companion object{
        fun newInstance(courseID: Int): CourseDeleteDialog{
            val courseDeleteDialog = CourseDeleteDialog()
            courseDeleteDialog.arguments = Bundle().apply {
                putInt("delete_dialog_course_id", courseID)
            }
            return courseDeleteDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        courseID = arguments?.getInt("delete_dialog_course_id")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("Are you sure you want to delete this Course?")
        dialogBuilder.setPositiveButton("Delete") { dialog, _ ->
            setFragmentResult("CourseDeleteDialog", bundleOf("courseID" to courseID))
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }

}