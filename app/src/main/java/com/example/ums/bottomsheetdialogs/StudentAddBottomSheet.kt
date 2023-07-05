package com.example.ums.bottomsheetdialogs

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.ums.DatabaseHelper
import com.example.ums.Gender
import com.example.ums.R
import com.example.ums.UserRole
import com.example.ums.Utility
import com.example.ums.model.Student
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class StudentAddBottomSheet: FullScreenBottomSheetDialog() {

    companion object{
        fun newInstance(departmentID: Int?, collegeID: Int?): StudentAddBottomSheet?{
            val bottomSheet = StudentAddBottomSheet()
            bottomSheet.arguments = Bundle().apply {
                putInt("department_activity_college_id", collegeID ?: return null)
                putInt("department_activity_department_id", departmentID ?: return null)
            }
            return bottomSheet
        }
    }

    private lateinit var userName : TextInputLayout
    private lateinit var contactNumber: TextInputLayout
    private lateinit var dateOfBirth: TextInputLayout
    private lateinit var genderRadio: RadioGroup
    private lateinit var userAddress: TextInputLayout
    private lateinit var emailAddress: TextInputLayout
    private lateinit var userPassword: TextInputLayout
    private lateinit var genderTextView: TextView
    private var semesterSpinner: Spinner? = null
    private var isDegreeErrorOn = false
    private var degreeOptionId: Int = -1
    private var selectedSemester: Int? = null
    private lateinit var semesterTextView: TextView
    private var isSemesterErrorOn = false
    private lateinit var degreeTextView: TextView

    private lateinit var degreeRadio: RadioGroup

    private var collegeID: Int? = null
    private var departmentID: Int? = null
    private lateinit var studentDAO: StudentDAO

    private lateinit var userNameText: String
    private lateinit var contactNumberText: String
    private lateinit var dateOfBirthText: String
    private var genderOptionId: Int = -1
    private lateinit var userAddressText: String
    private lateinit var emailAddressText: String
    private lateinit var userPasswordText: String

    private var userNameError: String? = null
    private var contactNumberError: String? = null
    private var dateOfBirthError: String? = null
    private var userAddressError: String? = null
    private var emailAddressError: String? = null
    private var degree: String? = null
    private var userPasswordError: String? = null

    private var savedDate: Int? = null
    private var savedMonth: Int? = null
    private var savedYear: Int? = null

    private var isGenderErrorOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collegeID = arguments?.getInt("department_activity_college_id")
        departmentID = arguments?.getInt("department_activity_department_id")

        userNameText = savedInstanceState?.getString("student_add_name_text") ?: ""
        contactNumberText = savedInstanceState?.getString("student_add_contact_number_text") ?: ""
        dateOfBirthText = savedInstanceState?.getString("student_add_dob_text") ?: ""
        userAddressText = savedInstanceState?.getString("student_add_user_address_text") ?: ""
        emailAddressText = savedInstanceState?.getString("student_add_email_address_text") ?: ""
        userPasswordText = savedInstanceState?.getString("student_add_password_text") ?: ""

        savedYear = savedInstanceState?.getInt("student_add_birth_year")
        savedMonth = savedInstanceState?.getInt("student_add_birth_month")
        savedDate = savedInstanceState?.getInt("student_add_birth_date")

        userNameError = savedInstanceState?.getString("student_add_user_name_error")
        contactNumberError = savedInstanceState?.getString("student_add_contact_number_error")
        dateOfBirthError = savedInstanceState?.getString("student_add_date_of_birth_error")
        userAddressError = savedInstanceState?.getString("student_add_user_address_error")
        emailAddressError = savedInstanceState?.getString("student_add_email_address_error")
        userPasswordError = savedInstanceState?.getString("student_add_user_password_error")

        degreeOptionId = savedInstanceState?.getInt("course_add_degree_option_id") ?: -1
        isDegreeErrorOn = savedInstanceState?.getBoolean("course_add_is_degree_error_on") ?: false

        isSemesterErrorOn = savedInstanceState?.getBoolean("course_add_is_semester_error_on") ?: false

        genderOptionId = savedInstanceState?.getInt("student_add_gender_option_id") ?: -1

        isGenderErrorOn = savedInstanceState?.getBoolean("student_add_is_gender_error_on") ?: false

        studentDAO = StudentDAO(DatabaseHelper(requireActivity()))

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_add, container, false)

        val addStudentButton = view.findViewById<MaterialButton>(R.id.add_button)
        val bottomSheetCloseButton = view.findViewById<ImageButton>(R.id.close_button)

        userName = view.findViewById(R.id.course_name_layout)
        contactNumber = view.findViewById(R.id.user_contact_layout)
        dateOfBirth = view.findViewById(R.id.user_dob_layout)
        userAddress = view.findViewById(R.id.user_address_layout)
        emailAddress = view.findViewById(R.id.user_email_layout)
        userPassword = view.findViewById(R.id.user_password_layout)
        genderRadio = view.findViewById(R.id.elective_radio_group)
        genderTextView = view.findViewById(R.id.elective_text_view)
        degreeTextView = view.findViewById(R.id.degree_text_view)
        semesterTextView = view.findViewById(R.id.semester_text_view)
        degreeRadio = view.findViewById(R.id.degree_radio_group)
        semesterSpinner = view.findViewById(R.id.semester_spinner_id)
        semesterSpinner?.isEnabled = false

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
        if(isDegreeErrorOn){
            degreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
            isDegreeErrorOn = true
        }
        if(isSemesterErrorOn){
            semesterTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
            isSemesterErrorOn = true
        }

        if(isGenderErrorOn){
            genderTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
            isGenderErrorOn = true
        }

        degreeRadio.setOnCheckedChangeListener { _, _ ->
            semesterSpinner?.isEnabled = true
            if(degreeRadio.checkedRadioButtonId!=-1){
                isDegreeErrorOn = false
                when(view.findViewById<RadioButton>(degreeRadio.checkedRadioButtonId).text.toString()){
                    "B. Tech" -> degree = "B. Tech"
                    "M. Tech" -> degree = "M. Tech"
                }
            }
            degreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_onSurface))

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                if(degree == "B. Tech")
                    arrayOf("Semester 1","Semester 2","Semester 3","Semester 4","Semester 5","Semester 6","Semester 7","Semester 8")
                else
                    arrayOf("Semester 1","Semester 2","Semester 3","Semester 4")
            )
            semesterSpinner?.adapter = adapter
        }

        setCollegeIDTextView(view)

        dateEntryTextInputLayout(dateOfBirth)

        userName.editText?.addTextChangedListener(textListener(userName) {
            userNameError = null
        })
        contactNumber.editText?.addTextChangedListener(textListener(contactNumber) {
            contactNumberError = null
        })
        dateOfBirth.editText?.addTextChangedListener(textListener(dateOfBirth) {
            dateOfBirthError = null
        })
        userAddress.editText?.addTextChangedListener(textListener(userAddress) {
            userAddressError = null
        })
        emailAddress.editText?.addTextChangedListener(textListener(emailAddress) {
            emailAddressError = null
        })
        userPassword.editText?.addTextChangedListener(textListener(userPassword) {
            userPasswordError = null
        })
        var gender: String? = null
        genderRadio.setOnCheckedChangeListener { _, _ ->
            if(genderRadio.checkedRadioButtonId!=-1){
                isGenderErrorOn = false
                when(view.findViewById<RadioButton>(genderRadio.checkedRadioButtonId).text.toString()){
                    "Male" -> gender = Gender.MALE.type
                    "Female" -> gender = Gender.FEMALE.type
                    "Other" -> gender = Gender.OTHER.type
                }
            }
            genderTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_onSurface))
        }

        addStudentButton.setOnClickListener {

            var flag = true

            degreeOptionId = degreeRadio.checkedRadioButtonId
            selectedSemester = semesterSpinner?.selectedItemPosition
            userNameText = userName.editText?.text.toString()
            contactNumberText = contactNumber.editText?.text.toString()
            dateOfBirthText = dateOfBirth.editText?.text.toString()
            userAddressText = userAddress.editText?.text.toString()
            emailAddressText = emailAddress.editText?.text.toString()
            userPasswordText = userPassword.editText?.text.toString()
            genderOptionId = genderRadio.checkedRadioButtonId

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
            if (dateOfBirthText.isEmpty()) {
                flag = false
                dateOfBirthError = "Don't leave date of birth field blank"
                dateOfBirth.error = dateOfBirthError
            }
            if (userAddressText.isEmpty()) {
                flag = false
                userAddressError = "Don't leave address field blank"
                userAddress.error = userAddressError
            }
            if (emailAddressText.isEmpty()) {
                flag = false
                emailAddressError = "Don't leave email address field blank"
                emailAddress.error = emailAddressError
            }
            else if(!Utility.isEmailAddressFree(emailAddressText, requireActivity())){
                flag = false
                emailAddressError = "Email Address already exists"
                emailAddress.error = emailAddressError
            }
            else if(!Utility.isEmailDotCom(emailAddressText)){
                flag = false
                emailAddressError = "Please type proper email address"
                emailAddress.error = emailAddressError
            }
            if(userPasswordText.isEmpty()){
                flag = false
                userPasswordError = "Don't leave password field blank"
                userPassword.error = userAddressError
            }
            if(genderOptionId==-1){
                flag = false
                genderTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
                isGenderErrorOn = true
            }
            if(degreeOptionId==-1){
                flag = false
                degreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
                isDegreeErrorOn = true
            }

            if (flag) {
                val newID = studentDAO.getNewID()

                if(collegeID!=null && departmentID!=null && gender!=null && degree != null){
                    studentDAO.insert(
                        Student(
                            User(
                                newID,
                                userNameText,
                                contactNumberText,
                                dateOfBirthText,
                                gender!!,
                                userAddressText,
                                userPasswordText,
                                UserRole.STUDENT.role,
                                emailAddressText
                            ),
                            (selectedSemester!!)+1,
                            degree!!,
                            departmentID!!,
                            collegeID!!
                        )
                    )
                }

                setCollegeIDTextView(view)
                setFragmentResult("StudentAddFragmentPosition", bundleOf("id" to newID))
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
        dateOfBirth.error = null
        userAddress.error = null
        emailAddress.error = null
        userPassword.error = null
        genderOptionId = genderRadio.checkedRadioButtonId
        genderRadio.clearCheck()
        genderTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_onSurface))
    }

    private fun setCollegeIDTextView(view : View){
        view.findViewById<TextView>(R.id.course_id_text_view)?.setText(R.string.user_id_string)
        view.findViewById<TextView>(R.id.course_id_text_view)?.append(" C/$collegeID-D/$departmentID-U/${studentDAO.getNewID()}")
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val calendarYear = calendar.get(Calendar.YEAR)
        val calendarMonth = calendar.get(Calendar.MONTH)
        val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, date->
            val selectedDate = "${year}-${date}-${month + 1}"
            dateOfBirth.editText?.setText(selectedDate)
            if(year!=0 && month!=0 && date!=0){
                savedDate = date
                savedMonth = month
                savedYear = year
            }
        }, savedYear ?: calendarYear, savedMonth ?: calendarMonth, savedDate ?: calendarDay)

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis - 488400000000
        datePickerDialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("student_add_name_text",userName.editText?.text.toString())
        outState.putString("student_add_contact_number_text", contactNumber.editText?.text.toString())
        outState.putString("student_add_dob_text", dateOfBirth.editText?.text.toString())
        outState.putString("student_add_user_address_text", userAddress.editText?.text.toString())
        outState.putString("student_add_email_address_text", emailAddress.editText?.text.toString())
        outState.putString("student_add_password_text", userPassword.editText?.text.toString())

        outState.putString("student_add_user_name_error", userNameError)
        outState.putString("student_add_contact_number_error", contactNumberError)
        outState.putString("student_add_date_of_birth_error", dateOfBirthError)
        outState.putString("student_add_user_address_error", userAddressError)
        outState.putString("student_add_email_address_error", emailAddressError)
        outState.putString("student_add_user_password_error", userPasswordError)
        outState.putBoolean("student_add_is_gender_error_on", isGenderErrorOn)
        outState.putBoolean("student_add_is_semester_error_on", isSemesterErrorOn)
        outState.putBoolean("student_add_is_degree_error_on", isDegreeErrorOn)

        if(degreeOptionId!=0 && degreeOptionId!=-1){
            degreeRadio.check(degreeOptionId)
        }
        outState.putInt("student_add_degree_option_id", degreeOptionId)
        if(genderOptionId!=0 && genderOptionId!=-1){
            genderRadio.check(genderOptionId)
        }
        outState.putInt("student_add_gender_option_id", genderOptionId)

        userName.error = userNameError
        contactNumber.error = contactNumberError
        dateOfBirth.error = dateOfBirthError
        userAddress.error = userAddressError
        emailAddress.error = emailAddressError
        userPassword.error = userAddressError

        if(savedDate!=null){
            outState.putInt("student_add_birth_date", savedDate!!)
        }
        if(savedMonth!=null){
            outState.putInt("student_add_birth_month", savedMonth!!)
        }
        if(savedYear!=null){
            outState.putInt("student_add_birth_year", savedYear!!)
        }
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
    private fun dateEntryTextInputLayout(textInputLayout: TextInputLayout?){
        textInputLayout?.editText?.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                showDatePicker()
            }
        }
        textInputLayout?.editText?.setOnClickListener {
            showDatePicker()
        }
        textInputLayout?.setStartIconOnClickListener {
            showDatePicker()
        }
    }
}