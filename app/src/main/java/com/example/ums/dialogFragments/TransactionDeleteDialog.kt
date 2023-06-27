package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class TransactionDeleteDialog : DialogFragment() {

    private var transactionID: Int? = null

    companion object{
        fun getInstance(transactionID: Int): TransactionDeleteDialog{
            val studentDeleteDialog = TransactionDeleteDialog()
            studentDeleteDialog.arguments = Bundle().apply {
                putInt("delete_dialog_transaction_id", transactionID)
            }
            return studentDeleteDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        transactionID = arguments?.getInt("delete_dialog_transaction_id")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("Are you sure you want to delete this transaction?")
        dialogBuilder.setPositiveButton("Delete") { dialog, _ ->
            setFragmentResult("TransactionDeleteDialog", bundleOf("id" to transactionID))
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}