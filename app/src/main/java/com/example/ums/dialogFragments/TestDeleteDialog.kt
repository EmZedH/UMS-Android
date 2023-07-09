package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.ums.R

class TestDeleteDialog : DialogFragment() {

    private var testID: Int? = null

    companion object{
        fun getInstance(testID: Int): TestDeleteDialog{
            val studentDeleteDialog = TestDeleteDialog()
            studentDeleteDialog.arguments = Bundle().apply {
                putInt("test_delete_test_id", testID)
            }
            return studentDeleteDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        testID = arguments?.getInt("test_delete_test_id")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.confirmation_string)).setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_test_record_string))
        dialogBuilder.setPositiveButton(getString(R.string.delete_string)) { dialog, _ ->
            setFragmentResult("TestDeleteDialog", bundleOf("id" to testID))
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton(getString(R.string.cancel_string)) { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}