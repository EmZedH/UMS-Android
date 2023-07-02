package com.example.ums

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.bottomsheetdialogs.RecordUpdateBottomSheet
import com.example.ums.model.databaseAccessObject.RecordsDAO
import com.example.ums.model.databaseAccessObject.StudentDAO
import com.example.ums.model.databaseAccessObject.TestDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentCourseRecordPageActivity: AppCompatActivity() {

    private var courseId: Int? = null
    private var studentId: Int? = null
    private var departmentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_course_record_page)
        val extras = intent.extras
        courseId = extras?.getInt("student_course_record_course_id")
        studentId = extras?.getInt("student_course_record_student_id")
        departmentId = extras?.getInt("student_course_record_department_id")
        val departmentId = departmentId
        val studentId = studentId
        val courseId = courseId
        if(courseId!=null && studentId != null && departmentId!=null){
            val courseNameTextView = findViewById<TextView>(R.id.course_name_text_view)
            val attendanceTextView = findViewById<TextView>(R.id.attendance)
            val internalMarksTextView = findViewById<TextView>(R.id.internal_marks)
            val externalMarksTextView = findViewById<TextView>(R.id.external_marks)
            val assignmentMarksTextView = findViewById<TextView>(R.id.assignment_marks)
            val courseStatusTextView = findViewById<TextView>(R.id.course_status)
            val professorIDTextView = findViewById<TextView>(R.id.professor_id)
            val professorNameTextView = findViewById<TextView>(R.id.professor_name)

            val attendanceIncreaseButton = findViewById<MaterialButton>(R.id.attendance_increase_button)
            val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            val databaseHelper = DatabaseHelper(this)
            val recordsDAO = RecordsDAO(databaseHelper)
            val testDAO = TestDAO(databaseHelper)

            val studentDAO = StudentDAO(databaseHelper)
            var record = recordsDAO.get(studentId, courseId, departmentId)
            val course = record?.courseProfessor?.course
            val student = studentDAO.get(studentId)
            toolBar.setNavigationOnClickListener {
                finish()
            }

            courseNameTextView.text = course?.name

            attendanceTextView.text = "${(record?.attendance ?: 0)} (${(record?.attendance ?: 0)*2}%)"
            assignmentMarksTextView.text = "${record?.assignmentMarks ?: 0} (Out of 10)"
            courseNameTextView.text = course?.name
            internalMarksTextView.text = "${((record?.attendance ?: 0)/20) + (record?.assignmentMarks ?: 0) +((testDAO.getAverageTestMark(
                studentId, courseId, departmentId) ?: 0))} (Out of 40)"
            externalMarksTextView.text = "${record?.externalMarks ?: 0} (Out of 60)"
            professorIDTextView.text = record?.courseProfessor?.professor?.user?.id.toString()
            professorNameTextView.text = record?.courseProfessor?.professor?.user?.name

            if(course?.semester != null && student?.semester != null){
                courseStatusTextView.text = if(record?.status == "NOT_COMPLETED" && (student.semester > course.semester)) "Ongoing (Backlog)" else "Ongoing"
            }

            attendanceIncreaseButton.setOnClickListener {
                record?.attendance?.let {
                    if(it<50){
                        record?.attendance = it + 1
                        recordsDAO.update(record)
                        attendanceTextView.text = "${(record?.attendance ?: 0)} (${(record?.attendance ?: 0)*2}%)"
                        internalMarksTextView.text = "${((record?.attendance ?: 0)/20) + (record?.assignmentMarks ?: 0) +((testDAO.getAverageTestMark(
                            this.studentId, courseId, departmentId) ?: 0))} (Out of 40)"
                    }else{
                        Toast.makeText(this, "Maximum attendance days reached", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            floatingActionButton.setOnClickListener{
                val departmentUpdateBottomSheet = RecordUpdateBottomSheet.newInstance(studentId, courseId, departmentId)
                departmentUpdateBottomSheet?.show(supportFragmentManager, "DepartmentUpdateDialog")
            }

            supportFragmentManager.setFragmentResultListener("RecordUpdateBottomSheerFragment", this){_, _->
                record = recordsDAO.get(studentId, courseId, departmentId)
                attendanceTextView.text = "${(record?.attendance ?: 0)} (${(record?.attendance ?: 0)*2}%)"
                assignmentMarksTextView.text = "${record?.assignmentMarks ?: 0} (Out of 10)"
                internalMarksTextView.text = "${((record?.attendance ?: 0)/20) + (record?.assignmentMarks ?: 0) +((testDAO.getAverageTestMark(
                    this.studentId, courseId, departmentId) ?: 0))} (Out of 40)"
                externalMarksTextView.text = "${record?.externalMarks ?: 0} (Out of 60)"
            }
        }
    }
}