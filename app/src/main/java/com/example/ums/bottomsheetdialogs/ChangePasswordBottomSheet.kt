package com.example.ums.bottomsheetdialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.model.databaseAccessObject.UserDAO
import com.example.ums.viewmodels.ChangePasswordBottomSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ChangePasswordBottomSheet : BottomSheetDialogFragment() {

    private lateinit var currentPassword : TextInputLayout
    private lateinit var newPassword : TextInputLayout
    private lateinit var confirmNewPassword : TextInputLayout
    private lateinit var changePasswordBottomSheetViewModel: ChangePasswordBottomSheetViewModel

    private var userID: Int? = null

    private lateinit var view : View

    fun setUserID(userID: Int){
        this.userID = userID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changePasswordBottomSheetViewModel = ViewModelProvider(this)[ChangePasswordBottomSheetViewModel::class.java]
        if(userID==null){
            userID = changePasswordBottomSheetViewModel.getUserID().value
        }
        else{
            changePasswordBottomSheetViewModel.setUserID(userID!!)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val userDAO = UserDAO(DatabaseHelper(requireActivity()))
        view = inflater.inflate(R.layout.fragment_change_password, container, false)
        val closeButton = view.findViewById<ImageButton>(R.id.close_button)
        val emailTextView = view.findViewById<TextView>(R.id.college_id_text_view)
        currentPassword = view.findViewById(R.id.college_name_layout)
        newPassword = view.findViewById(R.id.college_address_layout)
        confirmNewPassword = view.findViewById(R.id.college_telephone_layout)
        val updateButton = view.findViewById<MaterialButton>(R.id.update_button)

        if(changePasswordBottomSheetViewModel.getNewPassword().value!=null){
            newPassword.editText?.setText(changePasswordBottomSheetViewModel.getNewPassword().value)
        }
        if(changePasswordBottomSheetViewModel.getCurrentPassword().value!=null){
            currentPassword.editText?.setText(changePasswordBottomSheetViewModel.getCurrentPassword().value)
        }
        if(changePasswordBottomSheetViewModel.getConfirmPassword().value!=null){
            confirmNewPassword.editText?.setText(changePasswordBottomSheetViewModel.getConfirmPassword().value)
        }
        val user = userDAO.get(userID!!)

        emailTextView.text = user?.emailID

        updateButton.setOnClickListener {

            val currentPasswordText = currentPassword.editText?.text.toString()
            val newPasswordText = newPassword.editText?.text.toString()
            val confirmPasswordText = confirmNewPassword.editText?.text.toString()

            if(currentPasswordText.isEmpty()){
                currentPassword.error = "Please enter the current password"
            }
            if(newPasswordText.isEmpty()){
                newPassword.error = "Please enter the new password"
            }
            if(confirmPasswordText.isEmpty()){
                confirmNewPassword.error = "Please re-enter new password"
            }
            if(currentPasswordText.isNotEmpty() and
                    newPasswordText.isNotEmpty() and
                    confirmPasswordText.isNotEmpty()){
                currentPassword.error = null
                newPassword.error = null
                confirmNewPassword.error = null
                if(currentPasswordText != user!!.password){
                    Log.i("ChangePasswordBottomSheetClass","userPassword: ${user.password} currentPassword: $currentPasswordText")
                    currentPassword.error = "Current password is wrong"
                }
                else{
                    if(newPasswordText != confirmPasswordText){
                        confirmNewPassword.error = "Re-entered password does not match"
                    }
                    else if(newPasswordText == currentPasswordText){
                        confirmNewPassword.error = "Please don't enter your current password"
                    }
                    else{
                        user.password = newPasswordText
                        userDAO.update(userID!!, user)
                        val sharedPreferences = context?.getSharedPreferences("UMSPreferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        editor?.putString("password",user.password)
                        editor?.apply()
                        Toast.makeText(requireContext(), "Password Updated!",Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
        }

        closeButton.setOnClickListener{
            dismiss()
        }

        return view
    }
    override fun onStop() {
        super.onStop()
        currentPassword.error = null
        newPassword.error = null
        confirmNewPassword.error = null

        changePasswordBottomSheetViewModel.setCurrentPassword(currentPassword.editText?.text.toString())
        changePasswordBottomSheetViewModel.setNewPassword(newPassword.editText?.text.toString())
        changePasswordBottomSheetViewModel.setConfirmPassword(confirmNewPassword.editText?.text.toString())
    }
}