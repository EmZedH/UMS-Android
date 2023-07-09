package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.ums.R

class StudentOpenCourseAddConfirmationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.confirmation_string)).setMessage(getString(R.string.student_already_has_two_necessary_open_course_electives_do_you_still_want_to_add_another_string))
        dialogBuilder.setPositiveButton(getString(R.string.add_string)) { dialog, _ ->
            setFragmentResult("StudentOpenCourseAddConfirmationDialog", bundleOf())
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton(getString(R.string.cancel_string)) { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}