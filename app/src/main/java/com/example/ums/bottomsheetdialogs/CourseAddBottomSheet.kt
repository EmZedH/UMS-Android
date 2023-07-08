package com.example.ums.bottomsheetdialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
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

class CourseAddBottomSheet: FullScreenBottomSheetDialog() {

    companion object{

        fun newInstance(departmentID: Int?, collegeID: Int?): CourseAddBottomSheet?{
            val courseAddBottomSheet = CourseAddBottomSheet()
            courseAddBottomSheet.arguments = Bundle().apply {
                putInt("department_activity_college_id", collegeID ?: return null)
                putInt("department_activity_department_id", departmentID ?: return null)
            }
            return courseAddBottomSheet
        }
    }

    private lateinit var courseName: TextInputLayout
    private lateinit var electiveRadio: RadioGroup
    private lateinit var degreeRadio: RadioGroup
    private var semesterSpinner: Spinner? = null
    private lateinit var courseDAO: CourseDAO

    private var degree: String? = null

    private lateinit var degreeTextView: TextView
    private lateinit var electiveTextView: TextView
    private lateinit var semesterTextView: TextView
    private var isSemesterErrorOn = false

    private lateinit var courseNameText: String

    private var courseNameError: String? = null

    private var isElectiveErrorOn = false
    private var electiveOptionId: Int = -1

    private var isDegreeErrorOn = false
    private var degreeOptionId: Int = -1

    private var selectedSemester: Int? = null

    private var collegeID: Int? = null
    private var departmentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collegeID = arguments?.getInt("department_activity_college_id")
        departmentID = arguments?.getInt("department_activity_department_id")

        courseNameText = savedInstanceState?.getString("course_add_course_name_text") ?: ""

        courseNameError = savedInstanceState?.getString("course_add_course_name_error")

        electiveOptionId = savedInstanceState?.getInt("course_add_elective_option_id") ?: -1
        isElectiveErrorOn = savedInstanceState?.getBoolean("course_add_is_elective_error_on") ?: false

        degreeOptionId = savedInstanceState?.getInt("course_add_degree_option_id") ?: -1
        isDegreeErrorOn = savedInstanceState?.getBoolean("course_add_is_degree_error_on") ?: false

        isSemesterErrorOn = savedInstanceState?.getBoolean("course_add_is_semester_error_on") ?: false

        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        courseDAO = CourseDAO(databaseHelper)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_course, container, false)

        val addCourseButton = view.findViewById<MaterialButton>(R.id.add_button)
        val bottomSheetCloseButton = view.findViewById<ImageButton>(R.id.close_button)

        courseName = view.findViewById(R.id.course_name_layout)
        semesterSpinner = view.findViewById(R.id.semester_spinner_id)
        degreeRadio = view.findViewById(R.id.degree_radio_group)
        electiveRadio = view.findViewById(R.id.elective_radio_group)
        degreeTextView = view.findViewById(R.id.degree_text_view)
        electiveTextView = view.findViewById(R.id.elective_text_view)
        semesterTextView = view.findViewById(R.id.semester_text_view)
        semesterSpinner?.isEnabled = false

        bottomSheetCloseButton?.setOnClickListener {
            dismiss()
        }

        if(courseNameText.isNotEmpty()){
            courseName.editText?.setText(courseNameText)
        }

        if(isDegreeErrorOn){
            degreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
            isDegreeErrorOn = true
        }
        if(isElectiveErrorOn){
            electiveTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
            isElectiveErrorOn = true
        }
        if(isSemesterErrorOn){
            semesterTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
            isSemesterErrorOn = true
        }

        setCollegeIDTextView(view)

        courseName.editText?.addTextChangedListener(textListener(courseName) {
            courseNameError = null
        })

        var elective: String? = null

        degreeRadio.setOnCheckedChangeListener { _, _ ->
            semesterSpinner?.isEnabled = true
            if(degreeRadio.checkedRadioButtonId!=-1){
                isDegreeErrorOn = false
                when(view.findViewById<RadioButton>(degreeRadio.checkedRadioButtonId).text.toString()){
                    "B. Tech" -> degree = "B. Tech"
                    "M. Tech" -> degree = "M. Tech"
                }
            }
            degreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_onSurface))

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                if(degree == "B. Tech")
                    arrayOf("Semester 1","Semester 2","Semester 3","Semester 4","Semester 5","Semester 6","Semester 7","Semester 8")
                else
                    arrayOf("Semester 1","Semester 2","Semester 3","Semester 4")
            )
            semesterSpinner?.adapter = adapter
        }


        electiveRadio.setOnCheckedChangeListener { _, _ ->
            if(electiveRadio.checkedRadioButtonId!=-1){
                isElectiveErrorOn = false
                when(view.findViewById<RadioButton>(electiveRadio.checkedRadioButtonId).text.toString()){
                    "Open" -> elective = "Open"
                    "Professional" -> elective =  "Professional"
                }
            }
            electiveTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_onSurface))
        }

        addCourseButton.setOnClickListener {
            var flag = true
            selectedSemester = semesterSpinner?.selectedItemPosition
            courseNameText = courseName.editText?.text.toString()
            degreeOptionId = degreeRadio.checkedRadioButtonId
            electiveOptionId = electiveRadio.checkedRadioButtonId

            if (courseNameText.isEmpty()) {
                flag = false
                courseNameError = "Don't leave name field blank"
                courseName.error = courseNameError
            }
            if(electiveOptionId==-1){
                flag = false
                electiveTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
                isElectiveErrorOn = true
            }
            if(degreeOptionId==-1){
                flag = false
                degreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_error))
                isDegreeErrorOn = true
            }
            if (flag) {
                val newID = courseDAO.getNewID(departmentID!!, collegeID!!)
                if(collegeID!=null && departmentID!=null && elective!=null && degree!=null && selectedSemester!=null){
                    courseDAO.insert(
                        Course(
                            newID,
                            courseNameText,
                            (selectedSemester!!)+1,
                            departmentID!!,
                            collegeID!!,
                            degree!!,
                            elective!!
                        )
                    )
                }

                setCollegeIDTextView(view)
                setFragmentResult("CourseAddFragmentPosition", bundleOf("id" to newID))
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("course_add_course_name_text",courseName.editText?.text.toString())

        outState.putString("course_add_course_name_error", courseNameError)

        outState.putBoolean("course_add_is_elective_error_on", isElectiveErrorOn)
        outState.putBoolean("course_add_is_semester_error_on", isSemesterErrorOn)
        outState.putBoolean("course_add_is_degree_error_on", isDegreeErrorOn)

        if(degreeOptionId!=0 && degreeOptionId!=-1){
            degreeRadio.check(degreeOptionId)
        }
        outState.putInt("course_add_degree_option_id", degreeOptionId)

        if(electiveOptionId!=0 && electiveOptionId!=-1){
            electiveRadio.check(electiveOptionId)
        }
        outState.putInt("course_add_elective_option_id", electiveOptionId)

        courseName.error = courseNameError
    }
}