package com.example.ums.bottomsheetdialogs

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.model.databaseAccessObject.UserDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ChangePasswordBottomSheet: FullScreenBottomSheetDialog() {

    companion object{
        fun newInstance(userID: Int?): ChangePasswordBottomSheet?{
            val changePasswordBottomSheet = ChangePasswordBottomSheet()
            changePasswordBottomSheet.arguments = Bundle().apply {
                putInt("user_id", userID ?: return null)
            }
            return changePasswordBottomSheet
        }
    }

    private lateinit var currentPassword : TextInputLayout
    private lateinit var newPassword : TextInputLayout
    private lateinit var confirmPassword : TextInputLayout

    private var currentPasswordText: String? = null
    private var newPasswordText: String? = null
    private var confirmPasswordText: String? = null

    private var currentPasswordError: String? = null
    private var newPasswordError: String? = null
    private var confirmPasswordError: String? = null

    private var userID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = arguments?.getInt("user_id")
        if(savedInstanceState!=null){
            currentPasswordText = savedInstanceState.getString("change_password_bottom_sheet_current_password")
            newPasswordText = savedInstanceState.getString("change_password_bottom_sheet_new_password")
            confirmPasswordText = savedInstanceState.getString("change_password_bottom_sheet_confirm_password")

            currentPasswordError = savedInstanceState.getString("change_password_bottom_sheet_current_password_error")
            newPasswordError = savedInstanceState.getString("change_password_bottom_sheet_new_password_error")
            confirmPasswordError = savedInstanceState.getString("change_password_bottom_sheet_confirm_password_error")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val userDAO = UserDAO(databaseHelper)
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)
        val closeButton = view.findViewById<ImageButton>(R.id.close_button)
        val emailTextView = view.findViewById<TextView>(R.id.course_id_text_view)
        val updateButton = view.findViewById<MaterialButton>(R.id.update_button)
        val userID = userID
        if(userID!=null){
            val user = userDAO.get(userID)

            currentPassword = view.findViewById(R.id.user_password_layout)
            newPassword = view.findViewById(R.id.college_address_layout)
            confirmPassword = view.findViewById(R.id.college_telephone_layout)

            if(currentPasswordText!=null){
                currentPassword.editText?.setText(currentPasswordText)
            }
            if(newPasswordText!=null){
                newPassword.editText?.setText(newPasswordText)
            }
            if(confirmPasswordText!=null){
                confirmPassword.editText?.setText(confirmPasswordText)
            }


            emailTextView.text = user?.emailID

            currentPassword.editText?.addTextChangedListener(textListener(currentPassword) {
                currentPasswordError = null
            })
            newPassword.editText?.addTextChangedListener(textListener(newPassword) {
                newPasswordError = null
            })
            confirmPassword.editText?.addTextChangedListener(textListener(confirmPassword) {
                confirmPasswordError = null
            })

            updateButton.setOnClickListener {

                val currentPasswordText = currentPassword.editText?.text.toString()
                val newPasswordText = newPassword.editText?.text.toString()
                val confirmPasswordText = confirmPassword.editText?.text.toString()

                if(currentPasswordText.isEmpty()){
                    currentPassword.error = getString(R.string.please_enter_the_current_password_string)
                    currentPasswordError = getString(R.string.please_enter_the_current_password_string)
                }
                if(newPasswordText.isEmpty()){
                    newPassword.error = getString(R.string.please_enter_the_new_password_string)
                    newPasswordError = getString(R.string.please_enter_the_new_password_string)
                }
                if(confirmPasswordText.isEmpty()){
                    confirmPassword.error = getString(R.string.please_re_enter_new_password_string)
                    confirmPasswordError = getString(R.string.please_re_enter_new_password_string)
                }
                if(currentPasswordText.isNotEmpty() and
                    newPasswordText.isNotEmpty() and
                    confirmPasswordText.isNotEmpty()){
                    if(currentPasswordText != user!!.password){
                        currentPassword.error = getString(R.string.current_password_is_wrong_string)
                    }
                    else{
                        if(newPasswordText != confirmPasswordText){
                            confirmPassword.error = getString(R.string.re_entered_password_does_not_match_string)
                        }
                        else if(newPasswordText == currentPasswordText){
                            confirmPassword.error = getString(R.string.please_don_t_enter_your_current_password_string)
                        }
                        else{
                            user.password = newPasswordText
                            userDAO.update(userID, user)
                            val sharedPreferences = context?.getSharedPreferences("UMSPreferences", Context.MODE_PRIVATE)
                            val editor = sharedPreferences?.edit()
                            editor?.putString("password",user.password)
                            editor?.apply()
                            Toast.makeText(requireContext(), getString(R.string.password_updated_string),Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
                }
            }

            closeButton.setOnClickListener{
                dismiss()
            }

        }

        return view
    }
    override fun onStop() {
        super.onStop()
        currentPassword.error = null
        newPassword.error = null
        confirmPassword.error = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("change_password_bottom_sheet_current_password", currentPassword.editText?.text.toString())
        outState.putString("change_password_bottom_sheet_new_password", newPassword.editText?.text.toString())
        outState.putString("change_password_bottom_sheet_confirm_password", confirmPassword.editText?.text.toString())

        outState.putString("change_password_bottom_sheet_current_password_error", currentPasswordError)
        outState.putString("change_password_bottom_sheet_new_password_error", newPasswordError)
        outState.putString("change_password_bottom_sheet_confirm_password_error", confirmPasswordError)

        currentPassword.error = currentPasswordError
        newPassword.error = newPasswordError
        confirmPassword.error = confirmPasswordError
    }

    private fun textListener(layout: TextInputLayout, errorOperation: (() -> Unit)): TextWatcher {
        return object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                layout.error = null
                errorOperation()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
    }
}