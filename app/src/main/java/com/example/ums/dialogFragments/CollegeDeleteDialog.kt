package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.ums.viewmodels.SuperAdminSharedViewModel

class CollegeDeleteDialog: DialogFragment() {
    private val superAdminSharedViewModel: SuperAdminSharedViewModel by activityViewModels()
    private var collegeID: Int? = null

    fun setCollegeID(collegeID: Int?){
        this.collegeID = collegeID
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(savedInstanceState!=null){
            collegeID = savedInstanceState.getInt("delete_dialog_college_id")
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("Are you sure you want to delete this college?")
        dialogBuilder.setPositiveButton("Delete") { dialog, _ ->
            val adapter = superAdminSharedViewModel.getAdapter().value
            adapter?.deleteItem(collegeID!!)
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