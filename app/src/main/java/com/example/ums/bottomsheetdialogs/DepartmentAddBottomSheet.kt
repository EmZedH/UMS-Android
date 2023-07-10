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
import com.example.ums.model.Department
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class DepartmentAddBottomSheet: FullScreenBottomSheetDialog() {

    companion object{
        fun newInstance(collegeID: Int?): DepartmentAddBottomSheet?{
            val bottomSheet = DepartmentAddBottomSheet()
            bottomSheet.arguments = Bundle().apply {
                putInt("college_activity_college_id", collegeID ?: return null)
            }
            return bottomSheet
        }
    }

    private lateinit var departmentName : TextInputLayout
    private var collegeID: Int? = null
    private lateinit var departmentDAO: DepartmentDAO

    private lateinit var departmentNameText: String

    private var departmentNameError: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collegeID = arguments?.getInt("college_activity_college_id")
        departmentNameText = savedInstanceState?.getString("department_add_name_text") ?: ""

        departmentNameError = savedInstanceState?.getString("department_add_name_error")
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        departmentDAO = DepartmentDAO(databaseHelper)
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

        departmentName =
            view.findViewById(R.id.user_password_layout)

        if(departmentNameText.isNotEmpty()){
            departmentName.editText?.setText(departmentNameText)
        }

        val addCollegeButton =
            view.findViewById<MaterialButton>(R.id.update_college_button)

        setCollegeIDTextView(view)

        departmentName.editText?.addTextChangedListener(textListener(departmentName) {
            departmentNameError = null
        })

        addCollegeButton.setOnClickListener {
            var flag = true

            departmentNameText = departmentName.editText?.text.toString()

            if (departmentNameText.isEmpty()) {
                flag = false
                departmentNameError = getString(R.string.don_t_leave_name_field_blank_string)
                departmentName.error = departmentNameError
            }
            if (flag) {
                val newID = departmentDAO.getNewID(collegeID!!)

                if(collegeID!=null){
                    departmentDAO.insert(
                        Department(
                            newID,
                            departmentNameText,
                            collegeID!!
                        )
                    )
                }

                setCollegeIDTextView(view)

                setFragmentResult("departmentAddFragmentPosition", bundleOf("id" to newID))
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
        departmentName.error = null
    }

    private fun setCollegeIDTextView(view : View){
        val collegeID = collegeID ?: return
        view.findViewById<TextView>(R.id.course_id_text_view)?.text = getString(R.string.department_id,
            collegeID, departmentDAO.getNewID(collegeID))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("department_add_name_text",departmentName.editText?.text.toString())
        outState.putString("department_add_name_error", departmentNameError)

        departmentName.error = departmentNameError
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
}