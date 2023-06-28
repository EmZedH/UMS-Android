package com.example.ums

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.bottomsheetdialogs.StudentUpdateBottomSheet
import com.example.ums.dialogFragments.StudentAdvanceSemesterConfirmationDialog
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.model.databaseAccessObject.TestDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentDetailsActivity: AppCompatActivity() {

    private var studentID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_profile_page)
        val extras = intent.extras
        studentID = extras?.getInt("student_details_student_id")
        val studentID = studentID
        if(studentID!=null){
            val studentIDTextView = findViewById<TextView>(R.id.id_text_view)
            val studentNameTextView = findViewById<TextView>(R.id.name)

            val completedCourseButton = findViewById<MaterialButton>(R.id.completed_courses_button)
            val advanceSemesterButton = findViewById<MaterialButton>(R.id.advance_semester_button)

            val userEmailIdTextView = findViewById<TextView>(R.id.user_email)
            val contactNumberTextView = findViewById<TextView>(R.id.contact_number)
            val dateOfBirthTextView = findViewById<TextView>(R.id.date_of_birth)
            val genderTextView  = findViewById<TextView>(R.id.gender)
            val userAddressTextView = findViewById<TextView>(R.id.address)

            val semesterTextView = findViewById<TextView>(R.id.semester)
            val degreeTextView = findViewById<TextView>(R.id.degree)
            val cgpaTextView = findViewById<TextView>(R.id.cgpa)

            val departmentIDTextView = findViewById<TextView>(R.id.department_id)
            val departmentNameTextView = findViewById<TextView>(R.id.department_name)
            val collegeIDTextView = findViewById<TextView>(R.id.college_id)
            val collegeNameTextView = findViewById<TextView>(R.id.college_name)

            val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            val databaseHelper = DatabaseHelper(this)
            val studentDAO = StudentDAO(databaseHelper)
            val departmentDAO = DepartmentDAO(databaseHelper)
            val collegeDAO = CollegeDAO(databaseHelper)

            val student = studentDAO.get(studentID)
            val department = departmentDAO.get(student?.departmentID, student?.collegeID)
            val college = collegeDAO.get(student?.collegeID)

            toolBar.setNavigationOnClickListener {
                finish()
            }

            val recordsDAO = RecordsDAO(DatabaseHelper(this))
            val testDAO = TestDAO(DatabaseHelper(this))
            val records = recordsDAO.getList(studentID)
            var numberOfRecords = 0
            var externalMark = 0
            var internalMark = 0
            for(record in records){
                numberOfRecords++
                externalMark += record.externalMarks
                val testAverage = testDAO.getAverageTestMark(studentID, record.courseProfessor.course.id, record.courseProfessor.course.departmentID)
                internalMark += (record.assignmentMarks+record.attendance+(testAverage ?: break))
            }
            val cgpa: Float = ((externalMark+internalMark).toFloat()/(numberOfRecords*10).toFloat())
            studentIDTextView.text = getString(R.string.id_string)
            studentIDTextView.append(" C/${student?.collegeID}-D/${student?.departmentID}-U/${student?.user?.id}")

            userEmailIdTextView.text = student?.user?.emailID
            contactNumberTextView.text = student?.user?.contactNumber
            dateOfBirthTextView.text = student?.user?.dateOfBirth
            genderTextView.text = student?.user?.gender
            userAddressTextView.text = student?.user?.address

            semesterTextView.text = student?.semester.toString()
            degreeTextView.text = student?.degree
            cgpaTextView.text = cgpa.toString()

            studentNameTextView.text = student?.user?.name
            departmentIDTextView.text = student?.departmentID.toString()
            departmentNameTextView.text = department?.name
            collegeIDTextView.text = student?.collegeID.toString()
            collegeNameTextView.text = college?.name

            supportFragmentManager.setFragmentResultListener("StudentAdvanceSemesterConfirmationDialog", this){_, _ ->
                val advanceableRecords = recordsDAO.getList(studentID)
                for(record in advanceableRecords){
                    val mark = record.externalMarks + (record.attendance/20) + record.assignmentMarks + (testDAO.getAverageTestMark(record.studentID, record.courseProfessor.course.id, record.courseProfessor.course.departmentID) ?: 0)
                    if(mark >= 60){
                        student?.semester?.let {
                            record.status = "COMPLETED"
                            record.semCompleted = it
                            student.semester = it + 1
                            recordsDAO.update(record)
                            studentDAO.update(student)
                            semesterTextView.text = student.semester.toString()
                        }
                    }
                    else{
                        recordsDAO.delete(record.studentID, record.courseProfessor.course.id, record.courseProfessor.course.departmentID)
                    }
                }
            }

            floatingActionButton.setOnClickListener{
                val departmentUpdateBottomSheet = StudentUpdateBottomSheet()
                departmentUpdateBottomSheet.arguments = Bundle().apply {
                    putInt("department_activity_student_id", studentID)
                }
                departmentUpdateBottomSheet.show(supportFragmentManager, "StudentUpdateDialog")
            }

            completedCourseButton.setOnClickListener {
                val intent = Intent(this, StudentCompletedCourseActivity::class.java)
                intent.putExtras(Bundle().apply { putInt("student_id", studentID) })
                startActivity(intent)
            }

            advanceSemesterButton.setOnClickListener {
                student?.let {
                    if((it.semester >= 8 && it.degree == "B. Tech") || (it.semester >= 4 && it.degree == "M. Tech")){
                        Toast.makeText(this, "Student is advanced to maximum semester", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    val studentAdvanceSemesterConfirmationDialog = StudentAdvanceSemesterConfirmationDialog()
                    studentAdvanceSemesterConfirmationDialog.show(supportFragmentManager, "StudentAdvanceSemesterConfirmationDialog")
                }
            }

            supportFragmentManager.setFragmentResultListener("StudentUpdateFragmentPosition", this){_, _->
                val newStudent = studentDAO.get(studentID)
                studentNameTextView.text = newStudent?.user?.name
                contactNumberTextView.text = newStudent?.user?.contactNumber
                userAddressTextView.text = newStudent?.user?.address
            }
        }
    }
}