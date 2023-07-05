package com.example.ums.superAdminCollegeAdminActivities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.bottomsheetdialogs.ProfessorUpdateBottomSheet
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.example.ums.model.databaseAccessObject.ProfessorDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProfessorDetailsActivity: AppCompatActivity() {

    private var professorID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.professor_profile_page)
        val extras = intent.extras
        professorID = extras?.getInt("professor_details_professor_id")
        val professorID = professorID
        if(professorID!=null){
            val professorIDTextView = findViewById<TextView>(R.id.id_text_view)
            val professorNameTextView = findViewById<TextView>(R.id.name)

            val userEmailIdTextView = findViewById<TextView>(R.id.user_email)
            val contactNumberTextView = findViewById<TextView>(R.id.contact_number)
            val dateOfBirthTextView = findViewById<TextView>(R.id.date_of_birth)
            val genderTextView  = findViewById<TextView>(R.id.gender)
            val userAddressTextView = findViewById<TextView>(R.id.address)

            val departmentIDTextView = findViewById<TextView>(R.id.department_id)
            val departmentNameTextView = findViewById<TextView>(R.id.department_name)
            val collegeIDTextView = findViewById<TextView>(R.id.college_id)
            val collegeNameTextView = findViewById<TextView>(R.id.college_name)

            val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            val databaseHelper = DatabaseHelper(this)
            val professorDAO = ProfessorDAO(databaseHelper)
            val departmentDAO = DepartmentDAO(databaseHelper)
            val collegeDAO = CollegeDAO(databaseHelper)

            val professor = professorDAO.get(professorID)
            val department = departmentDAO.get(professor?.departmentID, professor?.collegeID)
            val college = collegeDAO.get(professor?.collegeID)

            toolBar.setNavigationOnClickListener {
                finish()
            }

            professorIDTextView.text = getString(R.string.id_string)
            professorIDTextView.append(" C/${professor?.collegeID}-D/${professor?.departmentID}-U/${professor?.user?.id}")

            userEmailIdTextView.text = professor?.user?.emailID
            contactNumberTextView.text = professor?.user?.contactNumber
            dateOfBirthTextView.text = professor?.user?.dateOfBirth
            genderTextView.text = professor?.user?.gender
            userAddressTextView.text = professor?.user?.address

            professorNameTextView.text = professor?.user?.name
            departmentIDTextView.text = professor?.departmentID.toString()
            departmentNameTextView.text = department?.name
            collegeIDTextView.text = professor?.collegeID.toString()
            collegeNameTextView.text = college?.name

            floatingActionButton.setOnClickListener{
                val departmentUpdateBottomSheet = ProfessorUpdateBottomSheet.newInstance(professorID)
                departmentUpdateBottomSheet?.show(supportFragmentManager, "ProfessorUpdateDialog")
            }

            supportFragmentManager.setFragmentResultListener("ProfessorUpdateFragmentPosition", this){_, _->
                val newProfessor = professorDAO.get(professorID)
                professorNameTextView.text = newProfessor?.user?.name
                contactNumberTextView.text = newProfessor?.user?.contactNumber
                userAddressTextView.text = newProfessor?.user?.address
            }
        }
    }
}