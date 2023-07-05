package com.example.ums.bottomsheetdialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.Utility
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class CollegeUpdateBottomSheet: FullScreenBottomSheetDialog() {

    companion object{

        fun newInstance(collegeID: Int?): CollegeUpdateBottomSheet?{
            val collegeUpdateBottomSheet = CollegeUpdateBottomSheet()
            collegeUpdateBottomSheet.arguments = Bundle().apply {
                putInt("college_update_college_id", collegeID ?: return null)
            }
            return collegeUpdateBottomSheet
        }
    }

    private var collegeID: Int? = null

    private lateinit var collegeNameTextLayout: TextInputLayout
    private lateinit var collegeTelephoneTextLayout: TextInputLayout
    private lateinit var collegeAddressTextLayout: TextInputLayout

    private lateinit var updateButton: MaterialButton

    private lateinit var collegeNameText: String
    private lateinit var collegeAddressText: String
    private lateinit var collegeTelephoneText: String
    private var isRotate: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        collegeID = bundle?.getInt("college_update_college_id")

        isRotate = savedInstanceState?.getBoolean("college_update_is_rotate") ?: return
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val collegeDAO = CollegeDAO(DatabaseHelper(requireActivity()))
        val view = inflater.inflate(R.layout.fragment_update_college, container, false)
        val college = collegeDAO.get(collegeID)
        if(college!=null){
            val closeButton = view.findViewById<ImageButton>(R.id.close_button)
            val collegeIDTextView = view.findViewById<TextView>(R.id.course_id_text_view)
            collegeNameTextLayout = view.findViewById(R.id.user_password_layout)
            collegeTelephoneTextLayout = view.findViewById(R.id.college_telephone_layout)
            collegeAddressTextLayout = view.findViewById(R.id.college_address_layout)
            updateButton = view.findViewById(R.id.update_college_button)

            if(savedInstanceState!=null){
                collegeNameText = savedInstanceState.getString("college_add_name_text") ?: college.name
                collegeAddressText = savedInstanceState.getString("college_add_address_text") ?: college.address
                collegeTelephoneText = savedInstanceState.getString("college_add_telephone_text") ?: college.telephone
            }

            collegeIDTextView.append(collegeID.toString())

            updateButton.isEnabled = false
            if(isRotate){
                collegeNameTextLayout.editText?.setText(college.name)
                collegeAddressTextLayout.editText?.setText(college.address)
                collegeTelephoneTextLayout.editText?.setText(college.telephone)
            }
            else{
                collegeNameTextLayout.editText?.setText(collegeNameText)
                collegeAddressTextLayout.editText?.setText(collegeAddressText)
                collegeTelephoneTextLayout.editText?.setText(collegeTelephoneText)
            }

            if(collegeNameTextLayout.editText?.text.toString() != college.name ||
                collegeAddressTextLayout.editText?.text.toString() != college.address ||
                collegeTelephoneTextLayout.editText?.text.toString() != college.telephone){
                updateButton.isEnabled = true
            }

            collegeNameTextLayout.editText?.addTextChangedListener(textListener(college.name, collegeNameTextLayout))
            collegeAddressTextLayout.editText?.addTextChangedListener(textListener(college.address, collegeAddressTextLayout))
            collegeTelephoneTextLayout.editText?.addTextChangedListener(textListener(college.telephone, collegeTelephoneTextLayout))

            closeButton.setOnClickListener {
                dismiss()
            }
            updateButton.setOnClickListener {
                var flag = true

                collegeNameText = collegeNameTextLayout.editText?.text.toString()
                collegeAddressText = collegeAddressTextLayout.editText?.text.toString()
                collegeTelephoneText = collegeTelephoneTextLayout.editText?.text.toString()

                if (collegeNameText.isEmpty()) {
                    flag = false
                    collegeNameTextLayout.error = "Don't leave name field blank"
                }
                if (collegeAddressText.isEmpty()) {
                    flag = false
                    collegeAddressTextLayout.error = "Don't leave address field blank"
                }
                if (collegeTelephoneText.isEmpty()) {
                    flag = false
                    collegeTelephoneTextLayout.error = "Don't leave telephone field blank"
                }
                else if(!Utility.isValidContactNumber(collegeTelephoneText)){
                    flag = false
                    collegeTelephoneTextLayout.error = "Enter 10 digit contact number"
                }
                if (flag) {
                    val newCollege = College(
                        college.id,
                        collegeNameText,
                        collegeAddressText,
                        collegeTelephoneText
                    )
                    collegeDAO.update(newCollege)
                    setFragmentResult("CollegeUpdateBottomSheet", bundleOf("collegeID" to collegeID))
                    Toast.makeText(requireContext(), "Details Updated!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
        return view
    }

    private fun textListener(collegeDetail: String, layout: TextInputLayout): TextWatcher{
        return object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                layout.error = null
                updateButton.isEnabled = p0?.toString() != collegeDetail
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("college_add_name_text",collegeNameTextLayout.editText?.text.toString())
        outState.putString("college_add_address_text", collegeAddressTextLayout.editText?.text.toString())
        outState.putString("college_add_telephone_text",collegeTelephoneTextLayout.editText?.text.toString())
        outState.putBoolean("college_update_is_rotate", false)
    }
}