package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.ums.R

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
        dialogBuilder.setTitle(getString(R.string.confirmation_string)).setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_course_string))
        dialogBuilder.setPositiveButton(getString(R.string.delete_string)) { dialog, _ ->
            setFragmentResult("CourseDeleteDialog", bundleOf("courseID" to courseID))
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton(getString(R.string.cancel_string)) { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }

}