package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class DepartmentDeleteDialog: DialogFragment() {

    private var departmentID: Int? = null

    companion object{
        fun getInstance(departmentID: Int): DepartmentDeleteDialog{
            val departmentDeleteDialog = DepartmentDeleteDialog()
            departmentDeleteDialog.arguments = Bundle().apply {
                putInt("delete_dialog_department_id", departmentID)
            }
            return departmentDeleteDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

//        if(savedInstanceState!=null){
//            departmentID = savedInstanceState.getInt("delete_dialog_college_id")
//        }
        departmentID = arguments?.getInt("delete_dialog_department_id")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Confirmation").setMessage("Are you sure you want to delete this department?")
        dialogBuilder.setPositiveButton("Delete") { dialog, _ ->
//            val adapter = superAdminSharedViewModel.getAdapter().value
//            Log.i("SuperAdminMainPageClass","adapter: $adapter")
//            adapter?.deleteItem(collegeID!!)
            setFragmentResult("departmentDeleteDialog", bundleOf("departmentID" to departmentID))
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        if(departmentID!=null){
//            outState.putInt("delete_dialog_college_id",departmentID!!)
//        }
//    }
}