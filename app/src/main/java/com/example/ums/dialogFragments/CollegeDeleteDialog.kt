package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class CollegeDeleteDialog: DialogFragment() {
    private var collegeID: Int? = null

    fun setCollegeID(collegeID: Int?){
        this.collegeID = collegeID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState!=null){
            collegeID = savedInstanceState.getInt("delete_dialog_college_id")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("Are you sure you want to delete this college?")
        dialogBuilder.setPositiveButton("Delete") { dialog, _ ->
            setFragmentResult("collegeDeleteDialog", bundleOf("collegeID" to collegeID))
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(collegeID!=null){
            outState.putInt("delete_dialog_college_id",collegeID!!)
        }
    }
}