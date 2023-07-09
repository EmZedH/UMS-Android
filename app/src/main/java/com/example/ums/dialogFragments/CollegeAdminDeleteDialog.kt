package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.ums.R

class CollegeAdminDeleteDialog: DialogFragment() {

    private var collegeAdminID: Int? = null

    companion object{
        fun getInstance(collegeAdminID: Int): CollegeAdminDeleteDialog{
            val collegeAdminDeleteDialog = CollegeAdminDeleteDialog()
            collegeAdminDeleteDialog.arguments = Bundle().apply {
                putInt("delete_dialog_college_admin_id", collegeAdminID)
            }
            return collegeAdminDeleteDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        collegeAdminID = arguments?.getInt("delete_dialog_college_admin_id")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.confirmation_string)).setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_college_admin_string))
        dialogBuilder.setPositiveButton(getString(R.string.delete_string)) { dialog, _ ->
            setFragmentResult("collegeAdminDeleteDialog", bundleOf("id" to collegeAdminID))
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton(getString(R.string.cancel_string)) { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}