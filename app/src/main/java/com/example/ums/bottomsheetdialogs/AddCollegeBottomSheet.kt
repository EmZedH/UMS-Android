package com.example.ums.bottomsheetdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.ums.listener.AddCollegeListener
import com.example.ums.R
import com.example.ums.Utility
import com.example.ums.model.College
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class AddCollegeBottomSheet(private val collegeDAO: CollegeDAO, private val addCollegeListener: AddCollegeListener) : BottomSheetDialogFragment() {

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

                collegeNameText = ""
                collegeAddressText = ""
                collegeTelephoneText = ""

                collegeName.editText?.setText("")
                collegeAddress.editText?.setText("")
                collegeTelephone.editText?.setText("")
                addCollegeListener.addItemToAdapter(newID-1)
                dismiss()
            }

        }
        return view
    }

    override fun dismiss() {
        super.dismiss()
        finalize()

    }

    override fun onStop() {
        super.onStop()
        finalize()
    }

    private fun finalize(){
        collegeName.error = null
        collegeAddress.error = null
        collegeTelephone.error = null

        collegeNameText = collegeName.editText?.text.toString()
        collegeAddressText = collegeAddress.editText?.text.toString()
        collegeTelephoneText = collegeTelephone.editText?.text.toString()
    }

    private fun setCollegeIDTextView(view : View){
        view.findViewById<TextView>(R.id.college_id_text_view)!!.setText(R.string.college_id_string)
        view.findViewById<TextView>(R.id.college_id_text_view)!!.append(collegeDAO.getNewID().toString())
    }

}