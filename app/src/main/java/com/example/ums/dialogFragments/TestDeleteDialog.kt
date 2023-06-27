package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

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
        dialogBuilder.setTitle("Confirmation").setMessage("Are you sure you want to delete this test record?")
        dialogBuilder.setPositiveButton("Delete") { dialog, _ ->
            setFragmentResult("TestDeleteDialog", bundleOf("id" to testID))
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}