package com.example.ums.bottomsheetdialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.Utility
import com.example.ums.model.CollegeAdmin
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class CollegeAdminUpdateBottomSheet: FullScreenBottomSheetDialogFragment() {


    private lateinit var userName : TextInputLayout
    private lateinit var contactNumber: TextInputLayout
    private lateinit var userAddress: TextInputLayout

    private var collegeAdminId: Int? = null
    private lateinit var collegeAdminDAO: CollegeAdminDAO

    private lateinit var userNameText: String
    private lateinit var contactNumberText: String
    private lateinit var userAddressText: String

    private lateinit var collegeAdmin: CollegeAdmin

    private var userNameError: String? = null
    private var contactNumberError: String? = null
    private var userAddressError: String? = null

    private lateinit var updateButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collegeAdminId = arguments?.getInt("college_activity_college_admin_id")

        userNameError = savedInstanceState?.getString("college_admin_update_user_name_error")
        contactNumberError = savedInstanceState?.getString("college_admin_update_contact_number_error")
        userAddressError = savedInstanceState?.getString("college_admin_update_user_address_error")

        collegeAdminDAO = CollegeAdminDAO(DatabaseHelper(requireActivity()))
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_college_admin, container, false)

        updateButton = view.findViewById(R.id.add_user_button)
        val bottomSheetCloseButton = view.findViewById<ImageButton>(R.id.close_button)

        if(collegeAdminId!=null){
            collegeAdmin = collegeAdminDAO.get(collegeAdminId!!)!!
            userNameText = savedInstanceState?.getString("college_admin_update_name_text") ?: collegeAdmin.user.name
            userAddressText = savedInstanceState?.getString("college_admin_update_user_address_text") ?: collegeAdmin.user.address
            contactNumberText = savedInstanceState?.getString("college_admin_update_contact_number_text") ?: collegeAdmin.user.contactNumber
        }

        userName = view.findViewById(R.id.user_name_layout)
        contactNumber = view.findViewById(R.id.user_contact_layout)
        userAddress = view.findViewById(R.id.user_address_layout)

        bottomSheetCloseButton?.setOnClickListener {
            dismiss()
        }

        if(userNameText.isNotEmpty()){
            userName.editText?.setText(userNameText)
        }
        if(contactNumberText.isNotEmpty()){
            contactNumber.editText?.setText(contactNumberText)
        }
        if(userAddressText.isNotEmpty()){
            userAddress.editText?.setText(userAddressText)
        }

        setCollegeIDTextView(view)
        updateButton.isEnabled = false

        if(userName.editText!!.text.toString() != collegeAdmin.user.name){
            updateButton.isEnabled = true
        }
        if(contactNumber.editText!!.text.toString() != collegeAdmin.user.contactNumber){
            updateButton.isEnabled = true
        }
        if(userAddress.editText!!.text.toString() != collegeAdmin.user.address){
            updateButton.isEnabled = true
        }

        userName.editText?.addTextChangedListener(textListener(userName, collegeAdmin.user.name))
        contactNumber.editText?.addTextChangedListener(textListener(contactNumber, collegeAdmin.user.contactNumber))
        userAddress.editText?.addTextChangedListener(textListener(userAddress, collegeAdmin.user.address))

        updateButton.setOnClickListener {

            var flag = true

            userNameText = userName.editText?.text.toString()
            contactNumberText = contactNumber.editText?.text.toString()
            userAddressText = userAddress.editText?.text.toString()

            if (userNameText.isEmpty()) {
                flag = false
                userNameError = "Don't leave name field blank"
                userName.error = userNameError
            }
            if (contactNumberText.isEmpty()) {
                flag = false
                contactNumberError = "Don't leave contact number field blank"
                contactNumber.error = contactNumberError
            }
            else if(!Utility.isValidContactNumber(contactNumber.editText?.text.toString())){
                flag = false
                contactNumberError = "Enter 10 digit contact number"
                contactNumber.error = contactNumberError
            }
            if (userAddressText.isEmpty()) {
                flag = false
                userAddressError = "Don't leave address field blank"
                userAddress.error = userAddressError
            }

            if (flag) {

                if(collegeAdminId!=null){
                    collegeAdminDAO.update(
                        collegeAdminDAO.get(collegeAdminId!!)?.apply {
                            user.name = userNameText
                            user.address = userAddressText
                            user.contactNumber = contactNumberText
                        })
                }

                setCollegeIDTextView(view)
                setFragmentResult("collegeAdminUpdateFragmentPosition", bundleOf("id" to collegeAdminId))
                dismiss()
            }
        }
        return view
    }

    override fun onStop() {
        super.onStop()
        clearErrors()
    }


    private fun clearErrors(){
        userName.error = null
        contactNumber.error = null
        userAddress.error = null
    }

    private fun setCollegeIDTextView(view : View){
        view.findViewById<TextView>(R.id.user_id_text_view)?.setText(R.string.user_id_string)
        view.findViewById<TextView>(R.id.user_id_text_view)?.append(" C/$collegeAdminId-U/${collegeAdminDAO.getNewID()}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("college_admin_update_name_text",userName.editText?.text.toString())
        outState.putString("college_admin_update_contact_number_text", contactNumber.editText?.text.toString())
        outState.putString("college_admin_update_user_address_text", userAddress.editText?.text.toString())

        outState.putString("college_admin_update_user_name_error", userNameError)
        outState.putString("college_admin_update_contact_number_error", contactNumberError)
        outState.putString("college_admin_update_user_address_error", userAddressError)

        userName.error = userNameError
        contactNumber.error = contactNumberError
        userAddress.error = userAddressError

    }

    private fun textListener(layout: TextInputLayout, errorOperation: (() -> Unit), userDetail: String): TextWatcher {
        return object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                layout.error = null
                updateButton.isEnabled = p0?.toString() != userDetail
                errorOperation()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
    }

    private fun textListener(layout: TextInputLayout, userDetail: String): TextWatcher {
        return object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                layout.error = null
                updateButton.isEnabled = p0?.toString() != userDetail
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
    }
}