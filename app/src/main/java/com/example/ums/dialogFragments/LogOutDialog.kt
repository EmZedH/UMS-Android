package com.example.ums.dialogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.ums.LoginActivity
import com.example.ums.R

class LogOutDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.log_out_string)).setMessage(getString(R.string.are_you_sure_you_want_to_log_out_string))
        dialogBuilder.setPositiveButton(getString(R.string.confirm_string)) { _, _ ->
            val editor = activity?.getSharedPreferences("UMSPreferences", Context.MODE_PRIVATE)?.edit()
            editor?.putBoolean("isLoggedOut", true)
            editor?.apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        dialogBuilder.setNegativeButton(getString(R.string.cancel_string)) { dialog, _ ->
            dialog.dismiss()
        }
        return dialogBuilder.create()
    }
}