package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class StudentDeleteDialog : DialogFragment() {

    private var studentId: Int? = null

    companion object{
        fun getInstance(studentId: Int): StudentDeleteDialog{
            val studentDeleteDialog = StudentDeleteDialog()
            studentDeleteDialog.arguments = Bundle().apply {
                putInt("delete_dialog_student_id", studentId)
            }
            return studentDeleteDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        studentId = arguments?.getInt("delete_dialog_student_id")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("Are you sure you want to delete this Student?")
        dialogBuilder.setPositiveButton("Delete") { dialog, _ ->
            setFragmentResult("StudentDeleteDialog", bundleOf("id" to studentId))
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}