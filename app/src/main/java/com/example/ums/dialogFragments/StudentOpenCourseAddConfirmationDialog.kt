package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class StudentOpenCourseAddConfirmationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("Student already has two necessary Open Course Electives. Do you still want to add another?")
        dialogBuilder.setPositiveButton("Add") { dialog, _ ->
            setFragmentResult("StudentOpenCourseAddConfirmationDialog", bundleOf())
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}