package com.example.ums

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.ums.model.databaseAccessObject.UserDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ChangePasswordBottomSheet(private val userID : Int, private val userDAO: UserDAO) : BottomSheetDialogFragment() {

    private lateinit var currentPassword : TextInputLayout
    private lateinit var newPassword : TextInputLayout
    private lateinit var confirmNewPassword : TextInputLayout

    private lateinit var view : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        retainInstance = true
        view = inflater.inflate(R.layout.fragment_change_password, container, false)

        val closeButton = view.findViewById<ImageButton>(R.id.close_button)
        val emailTextView = view.findViewById<TextView>(R.id.college_id_text_view)
        currentPassword = view.findViewById(R.id.college_name_layout)
        newPassword = view.findViewById(R.id.college_address_layout)
        confirmNewPassword = view.findViewById(R.id.college_telephone_layout)
        val updateButton = view.findViewById<MaterialButton>(R.id.update_button)

        val user = userDAO.get(userID)

        emailTextView.text = user?.emailID

        updateButton.setOnClickListener {

            val currentPasswordText = currentPassword.editText?.text.toString()
            val newPasswordText = newPassword.editText?.text.toString()
            val confirmPasswordText = confirmNewPassword.editText.toString()

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
                    currentPassword.error = "Current password is wrong"
                }
                else{
                    if(newPasswordText != confirmPasswordText){
                        confirmNewPassword.error = "Re-entered password does not match"
                    }
                    else{
                        user.password = newPasswordText
                        userDAO.update(userID, user)
                    }
                }
            }
        }

        closeButton.setOnClickListener{
            dismiss()
        }

        return view
    }

//    override fun onResume() {
//        super.onResume()
//
//        currentPassword = view.findViewById(R.id.college_name_layout)
//        newPassword = view.findViewById(R.id.college_address_layout)
//        confirmNewPassword = view.findViewById(R.id.college_telephone_layout)
//    }

//    override fu˳
    override fun onStop() {
        super.onStop()
        currentPassword.error = null
        newPassword.error = null
        confirmNewPassword.error = null
    }
}