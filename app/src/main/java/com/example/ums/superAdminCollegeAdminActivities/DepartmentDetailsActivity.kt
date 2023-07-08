package com.example.ums.superAdminCollegeAdminActivities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.DatabaseHelper
import com.example.ums.R
import com.example.ums.bottomsheetdialogs.DepartmentUpdateBottomSheet
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.example.ums.model.databaseAccessObject.DepartmentDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DepartmentDetailsActivity: AppCompatActivity() {

    private var collegeId: Int? = null
    private var departmentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.department_profile_page)
        collegeId = intent.extras?.getInt("department_details_college_id")
        departmentId = intent.extras?.getInt("department_details_department_id")
        val departmentId = departmentId
        val collegeId = collegeId
        if(collegeId != null && departmentId!=null){
            val departmentIDTextView = findViewById<TextView>(R.id.department_id_text_view)
            val departmentNameTextView = findViewById<TextView>(R.id.name)
            val collegeIDTextView = findViewById<TextView>(R.id.college_id)
            val collegeNameTextView = findViewById<TextView>(R.id.college_name)

            val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            val databaseHelper = DatabaseHelper.newInstance(this)
            val departmentDAO = DepartmentDAO(databaseHelper)
            val collegeDAO = CollegeDAO(databaseHelper)

            val college = collegeDAO.get(collegeId)
            val department = departmentDAO.get(departmentId, collegeId)

            toolBar.setNavigationOnClickListener {
                finish()
            }

            departmentIDTextView.text = getString(R.string.id_string)
            departmentIDTextView.append(" C/$collegeId-D/$departmentId")

            departmentNameTextView.text = department?.name
            collegeIDTextView.text = collegeId.toString()
            collegeNameTextView.text = college?.name

            floatingActionButton.setOnClickListener{
                val departmentUpdateBottomSheet = DepartmentUpdateBottomSheet.newInstance(departmentId, collegeId)
                departmentUpdateBottomSheet?.show(supportFragmentManager, "DepartmentUpdateDialog")
            }

            supportFragmentManager.setFragmentResultListener("DepartmentUpdateBottomSheet", this){_, _->
                val newDepartment = departmentDAO.get(departmentId, collegeId)
                departmentNameTextView.text = newDepartment?.name
            }
        }
    }
}