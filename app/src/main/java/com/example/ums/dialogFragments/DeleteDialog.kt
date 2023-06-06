package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ums.viewmodels.DeleteDialogViewModel
import com.example.ums.viewmodels.SuperAdminSharedViewModel

class DeleteDialog: DialogFragment() {
    private val superAdminSharedViewModel: SuperAdminSharedViewModel by activityViewModels()
    private lateinit var deleteDialogViewModel: DeleteDialogViewModel
    private var collegeID: Int? = null

    fun setCollegeID(collegeID: Int?){
        this.collegeID = collegeID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deleteDialogViewModel = ViewModelProvider(this)[DeleteDialogViewModel::class.java]
        if(collegeID!=null){
            deleteDialogViewModel.setID(collegeID!!)
        }
        else{
            collegeID = deleteDialogViewModel.getID().value
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("Are you sure you want to delete this college?")
        dialogBuilder.setPositiveButton("Delete") { dialog, _ ->
//            superAdminSharedViewModel.getAdapter().observe(viewLifecycleOwner){adapter->
//                adapter.deleteItem(collegeID!!)
//            }
            val adapter = superAdminSharedViewModel.getAdapter().value
            Log.i("SuperAdminMainPageClass","adapter: $adapter")
            adapter?.deleteItem(collegeID!!)
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}