package com.example.ums.superAdminCollegeAdminActivities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.model.databaseAccessObject.TestDAO
import com.google.android.material.appbar.MaterialToolbar

class StudentCompletedCourseRecordActivity: AppCompatActivity() {

    private var courseId: Int? = null
    private var studentId: Int? = null
    private var departmentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_completed_course_record_page)
        val extras = intent.extras
        courseId = extras?.getInt("student_course_record_course_id")
        studentId = extras?.getInt("student_course_record_student_id")
        departmentId = extras?.getInt("student_course_record_department_id")
        val departmentId = departmentId
        val studentId = studentId
        val courseID = courseId
        if(courseID!=null && studentId != null && departmentId!=null){
            val courseNameTextView = findViewById<TextView>(R.id.course_name_text_view)
            val attendanceTextView = findViewById<TextView>(R.id.attendance)
            val internalMarksTextView = findViewById<TextView>(R.id.internal_marks)
            val externalMarksTextView = findViewById<TextView>(R.id.external_marks)
            val assignmentMarksTextView = findViewById<TextView>(R.id.assignment_marks)

            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            val databaseHelper = DatabaseHelper(this)
            val recordsDAO = RecordsDAO(databaseHelper)
            val courseDAO = CourseDAO(databaseHelper)
            val testDAO = TestDAO(databaseHelper)

            val record = recordsDAO.get(studentId, courseID, departmentId)
            val course = courseDAO.get(courseID, departmentId, record?.courseProfessor?.course?.collegeID)

            toolBar.setNavigationOnClickListener {
                finish()
            }

            courseNameTextView.text = course?.name

            attendanceTextView.text = "${(record?.attendance ?: 0)} (${(record?.attendance ?: 0)*2}%)"
            assignmentMarksTextView.text = "${record?.assignmentMarks ?: 0} (Out of 10)"
            courseNameTextView.text = course?.name
            internalMarksTextView.text = "${((record?.attendance ?: 0)/20) + (record?.assignmentMarks ?: 0) +((testDAO.getAverageTestMark(
                studentId, courseID, departmentId) ?: 0))} (Out of 40)"
            externalMarksTextView.text = "${record?.externalMarks ?: 0} (Out of 60)"
        }
    }
}