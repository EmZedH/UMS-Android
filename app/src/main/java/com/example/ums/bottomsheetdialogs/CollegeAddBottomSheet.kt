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
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class CollegeAddBottomSheet : FullScreenBottomSheetDialog() {

    private lateinit var collegeName : TextInputLayout
    private lateinit var collegeAddress : TextInputLayout
    private lateinit var collegeTelephone : TextInputLayout
    private lateinit var collegeDAO: CollegeDAO

    private lateinit var collegeNameText: String
    private lateinit var collegeAddressText: String
    private lateinit var collegeTelephoneText: String

    private lateinit var addCollegeButton: MaterialButton

    private var collegeNameError: String? = null
    private var collegeAddressError: String? = null
    private var collegeTelephoneError: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeNameText = savedInstanceState?.getString("college_add_name_text") ?: ""
        collegeAddressText = savedInstanceState?.getString("college_add_address_text") ?: ""
        collegeTelephoneText = savedInstanceState?.getString("college_add_telephone_text") ?: ""

        collegeNameError = savedInstanceState?.getString("college_add_name_error")
        collegeAddressError = savedInstanceState?.getString("college_add_address_error")
        collegeTelephoneError = savedInstanceState?.getString("college_add_telephone_error")

        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        collegeDAO = CollegeDAO(databaseHelper)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_college, container, false)

        val bottomSheetCloseButton =
            view.findViewById<ImageButton>(R.id.close_button)

        bottomSheetCloseButton?.setOnClickListener {
            dismiss()
        }

        collegeName =
            view.findViewById(R.id.user_password_layout)
        collegeAddress =
            view.findViewById(R.id.college_address_layout)
        collegeTelephone =
            view.findViewById(R.id.college_telephone_layout)

        if(collegeNameText.isNotEmpty()){
            collegeName.editText?.setText(collegeNameText)
        }
        if(collegeAddressText.isNotEmpty()){
            collegeAddress.editText?.setText(collegeAddressText)
        }
        if(collegeTelephoneText.isNotEmpty()){
            collegeTelephone.editText?.setText(collegeTelephoneText)
        }

        addCollegeButton =
            view.findViewById(R.id.update_college_button)

        setCollegeIDTextView(view)

        collegeName.editText?.addTextChangedListener(textListener(collegeName) {
            collegeNameError = null
        })
        collegeAddress.editText?.addTextChangedListener(textListener(collegeAddress) {
            collegeAddressError = null
        })
        collegeTelephone.editText?.addTextChangedListener(textListener(collegeTelephone) {
            collegeTelephoneError = null
        })

        addCollegeButton.setOnClickListener {
            var flag = true

            collegeNameText = collegeName.editText?.text.toString()
            collegeAddressText = collegeAddress.editText?.text.toString()
            collegeTelephoneText = collegeTelephone.editText?.text.toString()

            if (collegeNameText.isEmpty()) {
                flag = false
                collegeName.error = "Don't leave name field blank"
                collegeNameError = "Don't leave name field blank"
            }
            if (collegeAddressText.isEmpty()) {
                flag = false
                collegeAddress.error = "Don't leave address field blank"
                collegeAddressError = "Don't leave address field blank"
            }
            if (collegeTelephoneText.isEmpty()) {
                flag = false
                collegeTelephone.error = "Don't leave telephone field blank"
                collegeTelephoneError = "Don't leave telephone field blank"
            }
            else if(!Utility.isValidContactNumber(collegeTelephoneText)){
                flag = false
                collegeTelephone.error = "Enter 10 digit contact number"
                collegeTelephoneError = "Enter 10 digit contact number"
            }
            if (flag) {

                val newID = collegeDAO.getNewID()
                collegeDAO.insert(
                    College(
                        newID,
                        collegeNameText,
                        collegeAddressText,
                        collegeTelephoneText
                    )
                )

                setCollegeIDTextView(view)

                setFragmentResult("collegeAddBottomSheet", bundleOf("id" to newID))
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
        collegeName.error = null
        collegeAddress.error = null
        collegeTelephone.error = null
    }

    private fun setCollegeIDTextView(view : View){

        view.findViewById<TextView>(R.id.course_id_text_view)!!.setText(R.string.college_id)
        view.findViewById<TextView>(R.id.course_id_text_view)!!.append(collegeDAO.getNewID().toString())

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("college_add_name_error", collegeNameError)
        outState.putString("college_add_address_error", collegeAddressError)
        outState.putString("college_add_telephone_error", collegeTelephoneError)

        outState.putString("college_add_name_text", collegeName.editText?.text.toString())
        outState.putString("college_add_address_text", collegeAddress.editText?.text.toString())
        outState.putString("college_add_telephone_text", collegeTelephone.editText?.text.toString())

        collegeName.error = collegeNameError
        collegeAddress.error = collegeAddressError
        collegeTelephone.error = collegeTelephoneError
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

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}