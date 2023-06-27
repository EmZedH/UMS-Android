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
import com.example.ums.model.Test
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.model.databaseAccessObject.TestDAO
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class TestAddBottomSheet: FullScreenBottomSheetDialog() {


    private lateinit var testMark : TextInputLayout
    private var studentID: Int? = null
    private var courseID: Int? = null
    private var departmentID: Int? = null

    private lateinit var testMarkTextView: String

    private var testMarkError: String? = null

    companion object{
        fun newInstance(studentID: Int, courseID: Int, departmentID: Int): TestAddBottomSheet{
            return TestAddBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt("test_add_student_id", studentID)
                    putInt("test_add_course_id", courseID)
                    putInt("test_add_department_id", departmentID)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        studentID = arguments?.getInt("test_add_student_id")
        courseID = arguments?.getInt("test_add_course_id")
        departmentID = arguments?.getInt("test_add_department_id")
        testMarkTextView = savedInstanceState?.getString("test_add_name_text") ?: ""

        testMarkError = savedInstanceState?.getString("test_add_name_error")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_test, container, false)
        val bottomSheetCloseButton =
            view.findViewById<ImageButton>(R.id.close_button)

        bottomSheetCloseButton!!.setOnClickListener {
            dismiss()
        }

        testMark =
            view.findViewById(R.id.test_marks_text_layout)

        if(testMarkTextView.isNotEmpty()){
            testMark.editText?.setText(testMarkTextView)
        }

        val addCollegeButton =
            view.findViewById<MaterialButton>(R.id.update_college_button)

        setView(view)

        testMark.editText?.addTextChangedListener(textListener(testMark) {
            testMarkError = null
        })

        addCollegeButton.setOnClickListener {
            var flag = true

            testMarkTextView = testMark.editText?.text.toString()

            if (testMarkTextView.isEmpty()) {
                flag = false
                testMarkError = "Don't leave test mark field blank"
                testMark.error = testMarkError
            }
            else if(testMarkTextView.toInt() !in 0..25){
                flag = false
                testMarkError = "Enter mark below 25"
                testMark.error = testMarkError
            }

            if (flag) {
                val testDAO = TestDAO(DatabaseHelper(requireActivity()))
                val newID = testDAO.getNewID(
                    studentID ?: return@setOnClickListener,
                    courseID ?: return@setOnClickListener,
                    departmentID ?: return@setOnClickListener
                )

                val studentDAO = StudentDAO(DatabaseHelper(requireActivity()))
                val collegeID = studentDAO.get(studentID)?.collegeID ?: return@setOnClickListener

                if(studentID!=null){
                    testDAO.insert(
                        Test(
                            newID,
                            studentID!!,
                            courseID!!,
                            departmentID!!,
                            collegeID,
                            testMarkTextView.toInt()
                        )
                    )
                }

                setView(view)

                setFragmentResult("TestAddFragmentPosition", bundleOf("id" to newID))
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
        testMark.error = null
    }

    private fun setView(view : View){
        val testDAO = TestDAO(DatabaseHelper(requireActivity()))
        view.findViewById<TextView>(R.id.course_id_text_view)?.setText(R.string.id_string)
        view.findViewById<TextView>(R.id.course_id_text_view)
            ?.append(" T/${testDAO.getNewID(
                studentID ?: return, 
                courseID ?: return, 
                departmentID ?: return)}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("test_add_name_text",testMark.editText?.text.toString())
        outState.putString("test_add_name_error", testMarkError)

        testMark.error = testMarkError
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