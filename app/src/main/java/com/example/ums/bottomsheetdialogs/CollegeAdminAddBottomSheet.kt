package com.example.ums.bottomsheetdialogs

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.UserRole
import com.example.ums.Utility
import com.example.ums.model.CollegeAdmin
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class CollegeAdminAddBottomSheet : BottomSheetDialogFragment() {

    private lateinit var userName : TextInputLayout
    private lateinit var contactNumber: TextInputLayout
    private lateinit var dateOfBirth: TextInputLayout
    private lateinit var genderRadio: RadioGroup
    private lateinit var userAddress: TextInputLayout
    private lateinit var emailAddress: TextInputLayout
    private lateinit var userPassword: TextInputLayout

    private var collegeID: Int? = null
    private lateinit var collegeAdminDAO: CollegeAdminDAO

    private lateinit var userNameText: String
    private lateinit var contactNumberText: String
    private lateinit var dateOfBirthText: String
    private var genderOptionId: Int? = null
    private lateinit var userAddressText: String
    private lateinit var emailAddressText: String
    private lateinit var userPasswordText: String
    private var isGenderErrorOn: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collegeID = arguments?.getInt("college_activity_college_id")
        userNameText = savedInstanceState?.getString("college_admin_add_name_text") ?: ""
        contactNumberText = savedInstanceState?.getString("college_admin_add_contact_number_text") ?: ""
        dateOfBirthText = savedInstanceState?.getString("college_admin_add_dob_text") ?: ""
        userAddressText = savedInstanceState?.getString("college_admin_add_user_address_text") ?: ""
        emailAddressText = savedInstanceState?.getString("college_admin_add_email_address_text") ?: ""
        userPasswordText = savedInstanceState?.getString("college_admin_add_password_text") ?: ""
        genderOptionId = savedInstanceState?.getInt("college_admin_add_gender_option")
        isGenderErrorOn = savedInstanceState?.getBoolean("college_admin_add_is_gender_error_on")
        collegeAdminDAO = CollegeAdminDAO(DatabaseHelper(requireActivity()))

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_college_admin, container, false)

        val addCollegeAdminButton = view.findViewById<MaterialButton>(R.id.add_user_button)
        val bottomSheetCloseButton = view.findViewById<ImageButton>(R.id.close_button)


        userName = view.findViewById(R.id.user_name_layout)
        contactNumber = view.findViewById(R.id.user_contact_layout)
        dateOfBirth = view.findViewById(R.id.user_dob_layout)
        userAddress = view.findViewById(R.id.user_address_layout)
        emailAddress = view.findViewById(R.id.user_email_layout)
        userPassword = view.findViewById(R.id.user_password_layout)
        genderRadio = view.findViewById(R.id.gender_radio_group)

        genderRadio.setOnCheckedChangeListener { _, checkedId ->

            if(genderOptionId==null){
                genderOptionId = checkedId
            }

        }
        dateOfBirth.setStartIconOnClickListener {
            showDatePicker()
        }

        bottomSheetCloseButton?.setOnClickListener {
            dismiss()
        }

        if(userNameText.isNotEmpty()){
            userName.editText?.setText(userNameText)
        }
        if(contactNumberText.isNotEmpty()){
            contactNumber.editText?.setText(contactNumberText)
        }
        if(dateOfBirthText.isNotEmpty()){
            dateOfBirth.editText?.setText(dateOfBirthText)
        }
        if(userAddressText.isNotEmpty()){
            userAddress.editText?.setText(userAddressText)
        }
        if(emailAddressText.isNotEmpty()){
            emailAddress.editText?.setText(emailAddressText)
        }
        if(userPasswordText.isNotEmpty()){
            userPassword.editText?.setText(userPasswordText)
        }
        if(genderOptionId!=null){
            genderRadio.check(genderOptionId!!)
        }
        if(isGenderErrorOn==true){
            view.findViewById<TextView>(R.id.gender_text_view).setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
        }

        setCollegeIDTextView(view)

        addCollegeAdminButton.setOnClickListener {

            var flag = true

            userNameText = userName.editText?.text.toString()
            contactNumberText = contactNumber.editText?.text.toString()
            dateOfBirthText = dateOfBirth.editText?.text.toString()
            userAddressText = userAddress.editText?.text.toString()
            emailAddressText = emailAddress.editText?.text.toString()
            userPasswordText = userPassword.editText?.text.toString()

            if (userNameText.isEmpty()) {
                flag = false
                userName.error = "Don't leave name field blank"
            }
            if (contactNumberText.isEmpty()) {
                flag = false
                contactNumber.error = "Don't leave contact number field blank"
            }
            else if(!Utility.isValidContactNumber(contactNumber.editText?.text.toString())){
                flag = false
                contactNumber.error = "Enter 10 digit contact number"
            }
            if (dateOfBirthText.isEmpty()) {
                flag = false
                dateOfBirth.error = "Don't leave date of birth field blank"
            }
            if (userAddressText.isEmpty()) {
                flag = false
                userAddress.error = "Don't leave address field blank"
            }
            if (emailAddressText.isEmpty()) {
                flag = false
                emailAddress.error = "Don't leave email address field blank"
            }
            else if(Utility.isEmailAddressFree(emailAddressText, requireActivity())){
                flag = false
                emailAddress.error = "Email Address already exists"
            }
            if(userPasswordText.isEmpty()){
                flag = false
                userPassword.error = "Don't leave password field blank"
            }
            var gender: String? = null
            if(genderOptionId==null){
                flag = false
                view.findViewById<TextView>(R.id.gender_text_view).setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
                isGenderErrorOn = true
            }
            else{
                when (genderRadio.findViewById<RadioButton>(genderOptionId!!).text) {
                    "Male" -> {
                        gender = "M"
                    }
                    "Female" -> {
                        gender = "F"
                    }
                    "Other" -> {
                        gender = "O"
                    }
                }
            }

            if (flag) {
                val newID = collegeAdminDAO.getNewID()

                if(collegeID!=null){
                    collegeAdminDAO.insert(
                        CollegeAdmin(
                            User(
                                newID,
                                userNameText,
                                contactNumberText,
                                dateOfBirthText,
                                gender!!,
                                userAddressText,
                                emailAddressText,
                                UserRole.COLLEGE_ADMIN.role,
                                userPasswordText
                            ),
                            collegeID!!
                        )
                    )
                }

                setCollegeIDTextView(view)
                setFragmentResult("collegeAdminAddFragmentPosition", bundleOf("id" to newID))
                dismiss()
            }

        }
        return view
    }

    override fun dismiss() {
        super.dismiss()
        clearErrors()
    }

    private fun clearErrors(){
        userName.error = null
        contactNumber.error = null
        dateOfBirth.error = null
        userAddress.error = null
        emailAddress.error = null
        userPassword.error = null
    }

    private fun setCollegeIDTextView(view : View){
        view.findViewById<TextView>(R.id.college_id_text_view)!!.setText(R.string.user_id_string)
        view.findViewById<TextView>(R.id.college_id_text_view)!!.append(" C/$collegeID-D/${collegeAdminDAO.getNewID()}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("college_admin_add_name_text",userName.editText?.text.toString())
        outState.putString("college_admin_add_contact_number_text", contactNumber.editText?.text.toString())
        outState.putString("college_admin_add_dob_text", dateOfBirth.editText?.text.toString())
        outState.putString("college_admin_add_user_address_text", userAddress.editText?.text.toString())
        outState.putString("college_admin_add_email_address_text", emailAddress.editText?.text.toString())
        outState.putString("college_admin_add_password_text", userPassword.editText?.text.toString())

        if(genderOptionId!=null){
            outState.putInt("college_admin_add_gender_option", genderOptionId!!)
        }
        if(isGenderErrorOn!=null){
            outState.putBoolean("college_admin_add_is_gender_error_on", isGenderErrorOn!!)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "${selectedYear}-${selectedDay}-${selectedMonth + 1}"
            dateOfBirth.editText?.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}