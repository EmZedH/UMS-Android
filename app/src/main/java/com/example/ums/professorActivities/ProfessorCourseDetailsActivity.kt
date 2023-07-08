package com.example.ums.professorActivities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.model.databaseAccessObject.CourseDAO
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.google.android.material.appbar.MaterialToolbar

class ProfessorCourseDetailsActivity: AppCompatActivity() {

    private var courseId: Int? = null
    private var collegeId: Int? = null
    private var departmentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.professor_course_details_page)
        val extras = intent.extras
        courseId = extras?.getInt("course_details_course_id")
        collegeId = extras?.getInt("course_details_college_id")
        departmentId = extras?.getInt("course_details_department_id")
        val departmentId = departmentId
        val collegeId = collegeId
        val courseID = courseId
        if(courseID!=null && collegeId != null && departmentId!=null){
            val courseIDTextView = findViewById<TextView>(R.id.id_text_view)
            val courseNameTextView = findViewById<TextView>(R.id.name)
            val departmentIDTextView = findViewById<TextView>(R.id.department_id)
            val departmentNameTextView = findViewById<TextView>(R.id.department_name)
            val collegeIDTextView = findViewById<TextView>(R.id.college_id)
            val collegeNameTextView = findViewById<TextView>(R.id.college_name)
            val electiveTextView = findViewById<TextView>(R.id.elective)
            val courseSemesterTextView = findViewById<TextView>(R.id.course_sem)
            val courseDegreeTextView = findViewById<TextView>(R.id.course_degree)

            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            val databaseHelper = DatabaseHelper.newInstance(this)
            val departmentDAO = DepartmentDAO(databaseHelper)
            val collegeDAO = CollegeDAO(databaseHelper)
            val courseDAO = CourseDAO(databaseHelper)

            val college = collegeDAO.get(collegeId)
            val department = departmentDAO.get(departmentId, collegeId)
            val course = courseDAO.get(courseID, departmentId, collegeId)

            toolBar.setNavigationOnClickListener {
                finish()
            }

            courseIDTextView.text = getString(R.string.id_string)
            courseIDTextView.append(" C/$collegeId-D/$departmentId-CO/$courseID")

            courseNameTextView.text = course?.name
            departmentIDTextView.text = departmentId.toString()
            departmentNameTextView.text = department?.name
            collegeIDTextView.text = collegeId.toString()
            collegeNameTextView.text = college?.name
            electiveTextView.text = course?.elective
            courseSemesterTextView.text = course?.semester.toString()
            courseDegreeTextView.text = course?.degree

            supportFragmentManager.setFragmentResultListener("CourseUpdateBottomSheet", this){_, _->
                val newCourse = courseDAO.get(courseID, departmentId, collegeId)
                courseNameTextView.text = newCourse?.name
            }
        }
    }
}