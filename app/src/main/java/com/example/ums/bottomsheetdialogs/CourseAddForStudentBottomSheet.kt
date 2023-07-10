package com.example.ums.bottomsheetdialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.model.Course
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class CourseAddForStudentBottomSheet: FullScreenBottomSheetDialog() {

    companion object{

        fun newInstance(collegeID: Int?, departmentID: Int?, degree: String?, semester: Int?, elective: String?): CourseAddForStudentBottomSheet?{
            val bottomSheet = CourseAddForStudentBottomSheet()
            bottomSheet.arguments = Bundle().apply {
                putInt("collegeID", collegeID ?: return null)
                putInt("departmentID", departmentID ?: return null)
                putString("student_degree", degree ?: return null)
                putInt("student_semester", semester ?: return null)
                putString("student_elective", elective ?: return null)
            }
            return bottomSheet
        }

        fun newInstance(departmentDetailsBundle: Bundle?, degree: String?, semester: Int?, elective: String?): CourseAddForStudentBottomSheet?{
            val bottomSheet = CourseAddForStudentBottomSheet()
            bottomSheet.arguments = departmentDetailsBundle?.apply {
                putString("student_degree", degree ?: return null)
                putInt("student_semester", semester ?: return null)
                putString("student_elective", elective ?: return null)
            }
            return bottomSheet
        }
    }

    private lateinit var courseName: TextInputLayout
    private var semesterSpinner: Spinner? = null
    private lateinit var courseDAO: CourseDAO


    private lateinit var semesterTextView: TextView
    private var isSemesterErrorOn = false

    private lateinit var courseNameText: String

    private var courseNameError: String? = null

    private var collegeID: Int? = null
    private var departmentID: Int? = null
    private var degree: String? = null
    private var semester: Int? = null
    private var elective: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeID = arguments?.getInt("collegeID") ?: return
        departmentID = arguments?.getInt("departmentID") ?: return
        degree = arguments?.getString("student_degree") ?: return
        semester = arguments?.getInt("student_semester") ?: return
        elective = arguments?.getString("student_elective") ?: return

        courseNameText = savedInstanceState?.getString("course_add_course_name_text") ?: ""

        courseNameError = savedInstanceState?.getString("course_add_course_name_error")

        isSemesterErrorOn = savedInstanceState?.getBoolean("course_add_is_semester_error_on") ?: false

        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        courseDAO = CourseDAO(databaseHelper)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_course_for_student, container, false)

        val addCourseButton = view.findViewById<MaterialButton>(R.id.add_button)
        val bottomSheetCloseButton = view.findViewById<ImageButton>(R.id.close_button)

        courseName = view.findViewById(R.id.course_name_layout)
        semesterSpinner = view.findViewById(R.id.semester_spinner_id)
        semesterTextView = view.findViewById(R.id.semester_text_view)

        bottomSheetCloseButton?.setOnClickListener {
            dismiss()
        }

        if(courseNameText.isNotEmpty()){
            courseName.editText?.setText(courseNameText)
        }
        if(isSemesterErrorOn){
            semesterTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
            isSemesterErrorOn = true
        }

        setCollegeIDTextView(view)

        courseName.editText?.addTextChangedListener(textListener(courseName) {
            courseNameError = null
        })


        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            generateArray(semester!!)
        )
        semesterSpinner?.adapter = adapter

        addCourseButton.setOnClickListener {
            var flag = true
            val selectedSemester = semesterSpinner?.selectedItemPosition
            courseNameText = courseName.editText?.text.toString()

            if (courseNameText.isEmpty()) {
                flag = false
                courseNameError = getString(R.string.don_t_leave_name_field_blank_string)
                courseName.error = courseNameError
            }
            if (flag) {
                val newID = courseDAO.getNewID(departmentID!!, collegeID!!)
                if(collegeID!=null && departmentID!=null && elective!=null && degree!=null && selectedSemester!=null){
                    courseDAO.insert(
                        Course(
                            newID,
                            courseNameText,
                            (selectedSemester)+1,
                            departmentID!!,
                            collegeID!!,
                            degree!!,
                            elective!!
                        )
                    )
                }

                setCollegeIDTextView(view)
                setFragmentResult("CourseAddForStudentFragmentPosition", bundleOf("id" to newID))
                dismiss()
            }
        }
        return view
    }

    private fun setCollegeIDTextView(view : View){
        view.findViewById<TextView>(R.id.course_id_text_view)?.setText(R.string.id_string)
        view.findViewById<TextView>(R.id.course_id_text_view)?.append(" C/$collegeID-D/$departmentID-CO/${courseDAO.getNewID(departmentID!!, collegeID!!)}")
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

    private fun generateArray(number: Int): List<String> {
        val array = mutableListOf<String>()
        for (i in 1..number) {
            array.add("Semester $i")
        }
        return array
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("course_add_course_name_text",courseName.editText?.text.toString())

        outState.putString("course_add_course_name_error", courseNameError)

        outState.putBoolean("course_add_is_semester_error_on", isSemesterErrorOn)

        courseName.error = courseNameError
    }
}