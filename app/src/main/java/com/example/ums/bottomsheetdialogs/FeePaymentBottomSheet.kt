package com.example.ums.bottomsheetdialogs

import android.os.Bundle
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
import com.example.ums.model.Records
import com.example.ums.model.Transactions
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.CourseProfessorDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.model.databaseAccessObject.TransactionDAO
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class FeePaymentBottomSheet: FullScreenBottomSheetDialog() {

    companion object{
        fun newInstance(studentID: Int?): FeePaymentBottomSheet?{
            val bottomSheet = FeePaymentBottomSheet()
            bottomSheet.arguments = Bundle().apply {
                putInt("student_id", studentID ?: return null)
            }
            return bottomSheet
        }
    }

    private var studentID: Int? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentID = arguments?.getInt("student_id")

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fee_payment, container, false)

        val studentID = studentID ?: return view
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val transactionDAO = TransactionDAO(databaseHelper)
        val addStudentButton = view.findViewById<MaterialButton>(R.id.add_button)
        val bottomSheetCloseButton = view.findViewById<ImageButton>(R.id.close_button)

        val amountTextView = view.findViewById<TextView>(R.id.fees_amount_text_view)
        val semesterTextView = view.findViewById<TextView>(R.id.semester_text_view)

        bottomSheetCloseButton?.setOnClickListener {
            dismiss()
        }

        setTextView(view)

        val calendar = Calendar.getInstance()
        val calendarYear = calendar.get(Calendar.YEAR)
        val calendarMonth = calendar.get(Calendar.MONTH)
        val calendarDay = calendar.get(Calendar.DAY_OF_MONTH)

        amountTextView.text = getString(R.string.amount_string)
        amountTextView.append(" ₹20000")

        val studentDAO = StudentDAO(databaseHelper)
        val student = studentDAO.get(studentID)
        semesterTextView.text = getString(R.string.semester_string)
        semesterTextView.append(" ${student?.semester}")

        addStudentButton.setOnClickListener {
            val newID = transactionDAO.getNewID()
            val semester = studentDAO.get(studentID)?.semester
            transactionDAO.insert(
                Transactions(
                    newID,
                    studentID,
                    semester ?: return@setOnClickListener,
                    "${calendarYear}-${calendarDay}-${calendarMonth + 1}",
                    20000
                )
            )

            setTextView(view)

            val courseDAO = CourseDAO(databaseHelper)

            val courseProfessorDAO = CourseProfessorDAO(databaseHelper)

            val professionalCourses = courseDAO.getNewProfessionalCoursesWithProfessors(studentID)
            val transactions = transactionDAO.getCurrentSemesterTransactionList(studentID)
            val recordsDAO = RecordsDAO(databaseHelper)

            for (course in professionalCourses){
                val courseProfessors = courseProfessorDAO.getList(course.id, course.departmentID, course.collegeID)
                if(courseProfessors.isNotEmpty() && transactions.isNotEmpty()){
                    val courseProfessor = courseProfessors.random()
                    recordsDAO.insert(
                        Records(
                            studentID,
                            courseProfessor,
                            transactions[0].id,
                            0,
                            0,
                            0,
                            "NOT_COMPLETED",
                            0
                        )
                    )
                }
            }
            setFragmentResult("FeePaymentBottomSheetResult", bundleOf("id" to newID))
            Toast.makeText(requireContext(), "Fee payment and course registration successful", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        return view
    }
    private fun setTextView(view : View){
        val databaseHelper = DatabaseHelper.newInstance(requireContext())
        val transactionDAO = TransactionDAO(databaseHelper)
        view.findViewById<TextView>(R.id.id_text_view)?.setText(R.string.id_string)
        view.findViewById<TextView>(R.id.id_text_view)?.append(" T/${transactionDAO.getNewID()}")
    }
}