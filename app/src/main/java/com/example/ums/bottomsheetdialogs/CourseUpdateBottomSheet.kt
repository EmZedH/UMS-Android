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
import com.example.ums.model.Course
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class CourseUpdateBottomSheet: FullScreenBottomSheetDialog() {

    companion object{

        fun newInstance(courseID: Int?, departmentID: Int?, collegeID: Int?): CourseUpdateBottomSheet?{
            val bottomSheet = CourseUpdateBottomSheet()
            bottomSheet.arguments = Bundle().apply {
                putInt("course_update_course_id", courseID ?: return null)
                putInt("course_update_college_id", collegeID ?: return null)
                putInt("course_update_department_id", departmentID ?: return null)
            }
            return bottomSheet
        }
    }

    private var collegeID: Int? = null
    private var departmentID: Int? = null
    private var courseID: Int? = null

    private lateinit var course: Course
    private lateinit var courseNameTextLayout: TextInputLayout
    private lateinit var updateButton: MaterialButton
    private lateinit var courseNameText: String
    private var isRotate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseID = arguments?.getInt("course_update_course_id")
        collegeID = arguments?.getInt("course_update_college_id")
        departmentID = arguments?.getInt("course_update_department_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val courseDAO = CourseDAO(DatabaseHelper(requireActivity()))
        val view = inflater.inflate(R.layout.fragment_update_course, container, false)
        val courseID = courseID
        val collegeID = collegeID
        val departmentID = departmentID
        if(courseID!=null && collegeID!=null && departmentID!=null){
            course = courseDAO.get(courseID, departmentID, collegeID)!!
            val closeButton = view.findViewById<ImageButton>(R.id.close_button)
            val courseNameTextView = view.findViewById<TextView>(R.id.course_id_text_view)
            courseNameTextLayout = view.findViewById(R.id.user_password_layout)
            updateButton = view.findViewById(R.id.update_college_button)

            courseNameText = savedInstanceState?.getString("course_update_name_text") ?: course.name
            courseNameTextView.text = getString(R.string.id_string)
            courseNameTextView.append(" C/$collegeID-D/$departmentID-CO/$courseID")

            updateButton.isEnabled = false

            if(isRotate){
                courseNameTextLayout.editText!!.setText(course.name)
            }
            else{
                courseNameTextLayout.editText!!.setText(courseNameText)
            }

            if(courseNameTextLayout.editText!!.text.toString() != course.name){
                updateButton.isEnabled = true
            }
            courseNameTextLayout.editText!!.addTextChangedListener(textListener(course.name, courseNameTextLayout))

            closeButton.setOnClickListener {
                dismiss()
            }
            updateButton.setOnClickListener {
                var flag = true

                courseNameText = courseNameTextLayout.editText?.text.toString()

                if (courseNameText.isEmpty()) {
                    flag = false
                    courseNameTextLayout.error = "Don't leave name field blank"
                }
                if (flag) {
                    val newCourse = Course(
                        courseID,
                        courseNameText,
                        course.semester,
                        departmentID,
                        collegeID,
                        course.degree,
                        course.elective
                    )
                    courseDAO.update(newCourse)
                    setFragmentResult("CourseUpdateBottomSheet", bundleOf("courseID" to courseID))
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
        outState.putString("course_update_name_text",courseNameTextLayout.editText?.text.toString())
    }
}