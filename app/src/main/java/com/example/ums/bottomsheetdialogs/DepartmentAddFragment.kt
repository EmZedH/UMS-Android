package com.example.ums.bottomsheetdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.model.Department
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class DepartmentAddFragment: BottomSheetDialogFragment() {


    private lateinit var collegeName : TextInputLayout
    private var collegeID: Int? = null
    private lateinit var departmentDAO: DepartmentDAO

//    private val superAdminMainPageViewModel: SuperAdminSharedViewModel by activityViewModels ()

    private lateinit var collegeNameText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collegeID = arguments?.getInt("college_activity_college_id")
        collegeNameText = savedInstanceState?.getString("department_add_name_text") ?: ""

        departmentDAO = DepartmentDAO(DatabaseHelper(requireActivity()))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_department, container, false)
        val bottomSheetCloseButton =
            view.findViewById<ImageButton>(R.id.close_button)

        bottomSheetCloseButton!!.setOnClickListener {
            dismiss()
        }

        collegeName =
            view.findViewById(R.id.college_name_layout)
        if(collegeNameText.isNotEmpty()){
            collegeName.editText?.setText(collegeNameText)
        }

        val addCollegeButton =
            view.findViewById<MaterialButton>(R.id.update_college_button)

        setCollegeIDTextView(view)

        addCollegeButton.setOnClickListener {
            var flag = true

            collegeNameText = collegeName.editText?.text.toString()

            if (collegeNameText.isEmpty()) {
                flag = false
                collegeName.error = "Don't leave name field blank"
            }
            if (flag) {
                val newID = departmentDAO.getNewID(collegeID!!)

                if(collegeID!=null){
                    departmentDAO.insert(
                        Department(
                            newID,
                            collegeNameText,
                            collegeID!!
                        )
                    )
                }

                setCollegeIDTextView(view)
//                superAdminMainPageViewModel.getAddListener().observe(viewLifecycleOwner){ listener->
//                    listener.onAdd(newID-1)
//                }
                setFragmentResult("departmentAddFragmentPosition", bundleOf("position" to newID-1))
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
        collegeName.error = null
    }

    private fun setCollegeIDTextView(view : View){
        view.findViewById<TextView>(R.id.college_id_text_view)!!.setText(R.string.id_string)
        view.findViewById<TextView>(R.id.college_id_text_view)!!.append(" C/$collegeID-D/${departmentDAO.getNewID(collegeID!!)}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("department_add_name_text",collegeName.editText?.text.toString())
    }
}