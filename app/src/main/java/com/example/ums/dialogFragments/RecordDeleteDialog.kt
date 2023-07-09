package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.ums.R

class RecordDeleteDialog: DialogFragment() {

    private var courseID: Int? = null
    private var departmentID: Int? = null
    private var fragmentKey: String? = null

    companion object{
        fun getInstance(position: Int, fragmentKey: String): RecordDeleteDialog{
            val courseDeleteDialog = RecordDeleteDialog()
            courseDeleteDialog.arguments = Bundle().apply {
                putInt("record_delete_dialog_course_id", position)
                putString("record_delete_dialog_fragment_key", fragmentKey)
            }
            return courseDeleteDialog
        }

        fun getInstance(courseID: Int, departmentID: Int, fragmentKey: String): RecordDeleteDialog{
            val courseDeleteDialog = RecordDeleteDialog()
            courseDeleteDialog.arguments = Bundle().apply {
                putInt("record_delete_dialog_course_id", courseID)
                putInt("record_delete_dialog_department_id", departmentID)
                putString("record_delete_dialog_fragment_key", fragmentKey)
            }
            return courseDeleteDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        courseID = arguments?.getInt("record_delete_dialog_course_id")
        departmentID = arguments?.getInt("record_delete_dialog_department_id")
        fragmentKey = arguments?.getString("record_delete_dialog_fragment_key")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.confirmation_string)).setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_course_record_all_data_will_be_lost_string))
        dialogBuilder.setPositiveButton(getString(R.string.delete_string)) { dialog, _ ->
            setFragmentResult(
                "RecordDeleteDialog$fragmentKey",
                bundleOf(
                    "course_id" to courseID,
                    "department_id" to departmentID
                )
            )
            courseID = null
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton(getString(R.string.cancel_string)) { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }

}