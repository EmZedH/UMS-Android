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
import com.example.ums.model.Department
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class DepartmentUpdateBottomSheet: FullScreenBottomSheetDialog() {

    private var collegeID: Int? = null
    private var departmentID: Int? = null
    private lateinit var department: Department
    private lateinit var departmentNameTextLayout: TextInputLayout
    private lateinit var updateButton: MaterialButton
    private lateinit var departmentNameText: String
    private var isRotate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeID = arguments?.getInt("department_update_college_id")
        departmentID = arguments?.getInt("department_update_department_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val departmentDAO = DepartmentDAO(DatabaseHelper(requireActivity()))
        val view = inflater.inflate(R.layout.fragment_update_department, container, false)
        val collegeID = collegeID
        val departmentID = departmentID
        if(collegeID!=null && departmentID!=null){
            department = departmentDAO.get(departmentID, collegeID)!!
            val closeButton = view.findViewById<ImageButton>(R.id.close_button)
            val collegeIDTextView = view.findViewById<TextView>(R.id.course_id_text_view)
            departmentNameTextLayout = view.findViewById(R.id.user_password_layout)
            updateButton = view.findViewById(R.id.update_college_button)

            departmentNameText = savedInstanceState?.getString("department_update_name_text") ?: department.name
            collegeIDTextView.text = getString(R.string.id_string)
            collegeIDTextView.append(" C/$collegeID-D/$departmentID")

            updateButton.isEnabled = false

            if(isRotate){
                departmentNameTextLayout.editText!!.setText(department.name)
            }
            else{
                departmentNameTextLayout.editText!!.setText(departmentNameText)
            }

            if(departmentNameTextLayout.editText!!.text.toString() != department.name){
                updateButton.isEnabled = true
            }
            departmentNameTextLayout.editText!!.addTextChangedListener(textListener(department.name, departmentNameTextLayout))

            closeButton.setOnClickListener {
                dismiss()
            }
            updateButton.setOnClickListener {
                var flag = true

                departmentNameText = departmentNameTextLayout.editText?.text.toString()

                if (departmentNameText.isEmpty()) {
                    flag = false
                    departmentNameTextLayout.error = "Don't leave name field blank"
                }
                if (flag) {
                    val newDepartment = Department(
                        departmentID,
                        departmentNameText,
                        collegeID
                    )
                    departmentDAO.update(newDepartment)
                    setFragmentResult("DepartmentUpdateBottomSheet", bundleOf("departmentID" to departmentID))
                    Toast.makeText(requireContext(), "Details Updated!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
        return view
    }

    private fun textListener(collegeDetail: String, layout: TextInputLayout): TextWatcher {
        return object: TextWatcher {
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
        outState.putString("department_update_name_text",departmentNameTextLayout.editText?.text.toString())
    }
}