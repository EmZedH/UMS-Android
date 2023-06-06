package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.ums.LoginActivity

class LogOutDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Log Out").setMessage("Are you sure you want to Log Out?")
        dialogBuilder.setPositiveButton("Confirm") { _, _ ->
            val editor = activity?.getSharedPreferences("UMSPreferences", Context.MODE_PRIVATE)?.edit()
            editor?.putBoolean("isLoggedOut", true)
            editor?.apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}