package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class RecordDeleteDialog: DialogFragment() {

    private var position: Int? = null
    private var fragmentKey: String? = null

    companion object{
        fun getInstance(position: Int, fragmentKey: String): RecordDeleteDialog{
            val courseDeleteDialog = RecordDeleteDialog()
            courseDeleteDialog.arguments = Bundle().apply {
                putInt("delete_dialog_position_id", position)
                putString("delete_dialog_fragment_key", fragmentKey)
            }
            return courseDeleteDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        position = arguments?.getInt("delete_dialog_position_id")
        fragmentKey = arguments?.getString("delete_dialog_fragment_key")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("Are you sure you want to delete this course record? All data will be lost")
        dialogBuilder.setPositiveButton("Delete") { dialog, _ ->
            setFragmentResult(
                "RecordDeleteDialog$fragmentKey",
                bundleOf(
                    "position" to position,
                )
            )
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }

}