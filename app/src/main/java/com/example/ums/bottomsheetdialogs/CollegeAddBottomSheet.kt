package com.example.ums.bottomsheetdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
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

class CollegeAddBottomSheet : BottomSheetDialogFragment() {

    private lateinit var collegeName : TextInputLayout
    private lateinit var collegeAddress : TextInputLayout
    private lateinit var collegeTelephone : TextInputLayout
    private lateinit var collegeDAO: CollegeDAO

    private lateinit var collegeTextfieldsViewModel: CollegeTextfieldsViewModel
    private val superAdminMainPageViewModel: SuperAdminSharedViewModel by activityViewModels ()

    private var collegeNameText = ""
    private var collegeAddressText = ""
    private var collegeTelephoneText = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeTextfieldsViewModel = ViewModelProvider(this)[CollegeTextfieldsViewModel::class.java]
        collegeDAO = CollegeDAO(DatabaseHelper(requireActivity()))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_college, container, false)
        val bottomSheetCloseButton =
            view.findViewById<ImageButton>(R.id.close_button)

        bottomSheetCloseButton!!.setOnClickListener {
            dismiss()
        }

        collegeName =
            view.findViewById(R.id.college_name_layout)
        collegeAddress =
            view.findViewById(R.id.college_address_layout)
        collegeTelephone =
            view.findViewById(R.id.college_telephone_layout)

        collegeNameText =
            if(collegeTextfieldsViewModel.getCollegeName()==null){
                ""
            }else{
                collegeTextfieldsViewModel.getCollegeName()!!
            }
        collegeAddressText =
            if(collegeTextfieldsViewModel.getCollegeAddress()==null){
                ""
            }else{
                collegeTextfieldsViewModel.getCollegeAddress()!!
            }

        collegeTelephoneText =
            if(collegeTextfieldsViewModel.getCollegeTelephone()==null){
                ""
            }else{
                collegeTextfieldsViewModel.getCollegeTelephone()!!
            }
        if(collegeNameText.isNotEmpty()){
            collegeName.editText?.setText(collegeNameText)
        }
        if(collegeAddressText.isNotEmpty()){
            collegeAddress.editText?.setText(collegeAddressText)
        }
        if(collegeTelephoneText.isNotEmpty()){
            collegeTelephone.editText?.setText(collegeTelephoneText)
        }

        val addCollegeButton =
            view.findViewById<MaterialButton>(R.id.update_college_button)

        setCollegeIDTextView(view)

        addCollegeButton.setOnClickListener {
            var flag = true

            collegeNameText = collegeName.editText?.text.toString()
            collegeAddressText = collegeAddress.editText?.text.toString()
            collegeTelephoneText = collegeTelephone.editText?.text.toString()

            if (collegeNameText.isEmpty()) {
                flag = false
                collegeName.error = "Don't leave name field blank"
            }
            if (collegeAddressText.isEmpty()) {
                flag = false
                collegeAddress.error = "Don't leave address field blank"
            }
            if (collegeTelephoneText.isEmpty()) {
                flag = false
                collegeTelephone.error = "Don't leave telephone field blank"
            }
            else if(!Utility.isValidContactNumber(collegeTelephoneText)){
                flag = false
                collegeTelephone.error = "Enter 10 digit contact number"
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
                superAdminMainPageViewModel.getAddListener().observe(viewLifecycleOwner){ listener->
                    listener.onAdd(newID-1)
                }
                dismiss()
            }

        }
        return view
    }

    //only close button
    override fun dismiss() {
        super.dismiss()
        clearErrors()
//        finalizeView()
    }

    //for all close and rotate actions
    override fun onStop() {
        super.onStop()
        finalizeView()
    }


    private fun finalizeView(){
        collegeTextfieldsViewModel.setCollegeName(collegeName.editText?.text.toString())
        collegeTextfieldsViewModel.setCollegeAddress(collegeAddress.editText?.text.toString())
        collegeTextfieldsViewModel.setCollegeTelephone(collegeTelephone.editText?.text.toString())
    }

    private fun clearErrors(){
        collegeName.error = null
        collegeAddress.error = null
        collegeTelephone.error = null
    }

    private fun setCollegeIDTextView(view : View){
        view.findViewById<TextView>(R.id.college_id_text_view)!!.setText(R.string.college_id_string)
        view.findViewById<TextView>(R.id.college_id_text_view)!!.append(collegeDAO.getNewID().toString())
    }
}