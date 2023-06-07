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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.Utility
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.viewmodels.CollegeTextfieldsViewModel
import com.example.ums.viewmodels.SuperAdminSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class CollegeUpdateBottomSheet : BottomSheetDialogFragment() {
    private val superAdminSharedViewModel: SuperAdminSharedViewModel by activityViewModels()
    private lateinit var collegeTextfieldViewModel: CollegeTextfieldsViewModel
    private lateinit var college: College
    private lateinit var collegeNameTextLayout: TextInputLayout
    private lateinit var collegeTelephoneTextLayout: TextInputLayout
    private lateinit var collegeAddressTextLayout: TextInputLayout
    private lateinit var updateButton: MaterialButton
    private var isRotate: Boolean = false

    fun setRotate(isRotate: Boolean){
        this.isRotate = isRotate
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeTextfieldViewModel = ViewModelProvider(requireActivity())[CollegeTextfieldsViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val collegeDAO = CollegeDAO(DatabaseHelper(requireActivity()))
        val view = inflater.inflate(R.layout.fragment_edit_college, container, false)
        superAdminSharedViewModel.getID().observe(viewLifecycleOwner){ collegeID ->
            college = collegeDAO.get(collegeID)!!
            val closeButton = view.findViewById<ImageButton>(R.id.close_button)
            val collegeIDTextView = view.findViewById<TextView>(R.id.college_id_text_view)
            collegeNameTextLayout = view.findViewById(R.id.college_name_layout)
            collegeTelephoneTextLayout = view.findViewById(R.id.college_telephone_layout)
            collegeAddressTextLayout = view.findViewById(R.id.college_address_layout)
            updateButton = view.findViewById(R.id.update_college_button)

            collegeIDTextView.append(collegeID.toString())

            updateButton.isEnabled = false
            if(isRotate){
                collegeNameTextLayout.editText!!.setText(college.name)
                collegeAddressTextLayout.editText!!.setText(college.address)
                collegeTelephoneTextLayout.editText!!.setText(college.telephone)
            }
            else{
                collegeNameTextLayout.editText!!.setText(collegeTextfieldViewModel.getCollegeName())
                collegeAddressTextLayout.editText!!.setText(collegeTextfieldViewModel.getCollegeAddress())
                collegeTelephoneTextLayout.editText!!.setText(collegeTextfieldViewModel.getCollegeTelephone())
            }

            if(collegeNameTextLayout.editText!!.text.toString() != college.name ||
                    collegeAddressTextLayout.editText!!.text.toString() != college.address ||
                    collegeTelephoneTextLayout.editText!!.text.toString() != college.telephone){
                updateButton.isEnabled = true
            }
            collegeNameTextLayout.editText!!.addTextChangedListener(textListener(college.name))
            collegeAddressTextLayout.editText!!.addTextChangedListener(textListener(college.address))
            collegeTelephoneTextLayout.editText!!.addTextChangedListener(textListener(college.telephone))

            closeButton.setOnClickListener {
                dismiss()
            }
            updateButton.setOnClickListener {
                var flag = true

                val collegeNameText = collegeNameTextLayout.editText?.text.toString()
                val collegeAddressText = collegeAddressTextLayout.editText?.text.toString()
                val collegeTelephoneText = collegeTelephoneTextLayout.editText?.text.toString()

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
                        collegeID,
                        collegeNameText,
                        collegeAddressText,
                        collegeTelephoneText
                    )
                    collegeDAO.update(collegeID, newCollege)
                    superAdminSharedViewModel.getAdapter().observe(viewLifecycleOwner){adapter->
                        adapter.updateItemInAdapter(collegeDAO.getList().indexOf(collegeDAO.get(collegeID)))
                    }
                    Toast.makeText(requireContext(), "Details Updated!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
        return view
    }

    override fun onStop() {
        super.onStop()
        collegeTextfieldViewModel.setCollegeName(collegeNameTextLayout.editText!!.text.toString())
        collegeTextfieldViewModel.setCollegeAddress(collegeAddressTextLayout.editText!!.text.toString())
        collegeTextfieldViewModel.setCollegeTelephone(collegeTelephoneTextLayout.editText!!.text.toString())
    }

    private fun textListener(collegeDetail: String): TextWatcher{
        return object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateButton.isEnabled = p0?.toString() != collegeDetail
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
    }
}