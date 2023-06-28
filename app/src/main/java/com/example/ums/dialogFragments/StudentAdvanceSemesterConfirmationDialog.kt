package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class StudentAdvanceSemesterConfirmationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("All courses with overall grade below 60% will be marked as backlog. They will still appear within students current semester list. Do you still wish to advance student semester")
        dialogBuilder.setPositiveButton("Confirm") { dialog, _ ->
            setFragmentResult("StudentAdvanceSemesterConfirmationDialog", bundleOf())
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}