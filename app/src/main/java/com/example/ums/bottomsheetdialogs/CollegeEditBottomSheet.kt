package com.example.ums.bottomsheetdialogs

import android.os.Bundle
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

class CollegeEditBottomSheet : BottomSheetDialogFragment() {
    private val superAdminSharedViewModel: SuperAdminSharedViewModel by activityViewModels()
    private lateinit var collegeTextfieldViewModel: CollegeTextfieldsViewModel
    private lateinit var college: College
    private lateinit var collegeNameTextLayout: TextInputLayout
    private lateinit var collegeTelephoneTextLayout: TextInputLayout
    private lateinit var collegeAddressTextLayout: TextInputLayout

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
        superAdminSharedViewModel.getID().observe(viewLifecycleOwner){collegeID->
            college = collegeDAO.get(collegeID)!!
            val closeButton = view.findViewById<ImageButton>(R.id.close_button)
            val collegeIDTextView = view.findViewById<TextView>(R.id.college_id_text_view)
            collegeNameTextLayout = view.findViewById(R.id.college_name_layout)
            collegeTelephoneTextLayout = view.findViewById(R.id.college_telephone_layout)
            collegeAddressTextLayout = view.findViewById(R.id.college_address_layout)
            val updateButton = view.findViewById<MaterialButton>(R.id.update_college_button)

            collegeIDTextView.append(collegeID.toString())

            collegeNameTextLayout.editText!!.setText(college.name)
            collegeAddressTextLayout.editText!!.setText(college.address)
            collegeTelephoneTextLayout.editText!!.setText(college.telephone)
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
}