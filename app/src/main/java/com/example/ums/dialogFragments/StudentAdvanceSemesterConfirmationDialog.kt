package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.ums.R

class StudentAdvanceSemesterConfirmationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.confirmation_string)).setMessage(getString(R.string.all_courses_with_overall_grade_below_60_will_be_marked_as_backlog_you_can_later_enroll_them_after_paying_next_semesters_fees_do_you_still_wish_to_advance_student_semester_string))
        dialogBuilder.setPositiveButton(getString(R.string.confirm_string)) { dialog, _ ->
            setFragmentResult("StudentAdvanceSemesterConfirmationDialog", bundleOf())
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton(getString(R.string.cancel_string)) { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}