package com.example.ums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isNotEmpty
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class AddCollege(private val collegeDAO: CollegeDAO, private val superAdminMainPage: SuperAdminMainPage) : BottomSheetDialogFragment() {

    private lateinit var collegeName : TextInputLayout
    private lateinit var collegeAddress : TextInputLayout
    private lateinit var collegeTelephone : TextInputLayout

    private var collegeNameText = ""
    private var collegeAddressText = ""
    private var collegeTelephoneText = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_add_college, container, false)
        val bottomSheetCloseButton =
            view.findViewById<ImageButton>(R.id.add_item_close_button)

        bottomSheetCloseButton!!.setOnClickListener {
            dismiss()
        }

        collegeName =
            view.findViewById(R.id.college_name_text_field)
        collegeAddress =
            view.findViewById(R.id.college_address_textfield)
        collegeTelephone =
            view.findViewById(R.id.college_telephone_textfield)

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
            view.findViewById<MaterialButton>(R.id.add_college_button)

        view.findViewById<TextView>(R.id.college_id)!!.text =
            "CID : C/${collegeDAO.getNewID()}"

        addCollegeButton.setOnClickListener {

            collegeNameText = collegeName.editText?.text.toString()
            collegeAddressText = collegeAddress.editText?.text.toString()
            collegeTelephoneText = collegeTelephone.editText?.text.toString()

            if (collegeNameText.isEmpty()) {
                collegeName.error = "Don't leave name field blank"
            }
            if (collegeAddressText.isEmpty()) {
                collegeAddress.error = "Don't leave address field blank"
            }

            if (collegeTelephoneText.isEmpty()) {
                collegeTelephone.error = "Don't leave telephone field blank"
            }

            if (collegeNameText.isNotEmpty() and
                collegeAddressText.isNotEmpty() and
                collegeTelephoneText.isNotEmpty()
            ) {

                collegeDAO.insert(
                    College(
                        collegeDAO.getNewID(),
                        collegeNameText,
                        collegeAddressText,
                        collegeTelephoneText
                    )
                )

                view.findViewById<TextView>(R.id.college_id)!!.text =
                    "CID : C/${collegeDAO.getNewID()}"

                collegeNameText = ""
                collegeAddressText = ""
                collegeTelephoneText = ""

                collegeName.editText?.setText("")
                collegeAddress.editText?.setText("")
                collegeTelephone.editText?.setText("")

                dismiss()
            }

        }
        return view
    }

    override fun dismiss() {
        collegeName.error = null
        collegeAddress.error = null
        collegeTelephone.error = null

        collegeNameText = collegeName.editText?.text.toString()
        collegeAddressText = collegeAddress.editText?.text.toString()
        collegeTelephoneText = collegeTelephone.editText?.text.toString()

        requireActivity().supportFragmentManager.beginTransaction().detach(superAdminMainPage).commit()
        requireActivity().supportFragmentManager.beginTransaction().attach(superAdminMainPage).commit()
        super.dismiss()
    }

}