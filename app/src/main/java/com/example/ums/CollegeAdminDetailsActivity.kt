package com.example.ums

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.bottomsheetdialogs.CollegeAdminUpdateBottomSheet
import com.example.ums.model.databaseAccessObject.CollegeAdminDAO
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CollegeAdminDetailsActivity: AppCompatActivity() {

    private var collegeId: Int? = null
    private var collegeAdminId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.college_admin_profile_page)
        val arguments = intent.extras
        collegeAdminId = arguments?.getInt("college_admin_profile_college_admin_id")
        collegeId = arguments?.getInt("college_admin_profile_college_id")
        val collegeId = collegeId
        val collegeAdminId = collegeAdminId
        if(collegeId != null && collegeAdminId!=null){
            val userIdTextView = findViewById<TextView>(R.id.course_id_text_view)
            val userEmailIdTextView = findViewById<TextView>(R.id.user_email)
            val userNameTextView = findViewById<TextView>(R.id.user_name)
            val contactNumberTextView = findViewById<TextView>(R.id.contact_number)
            val dateOfBirthTextView = findViewById<TextView>(R.id.date_of_birth)
            val genderTextView  = findViewById<TextView>(R.id.gender)
            val userAddressTextView = findViewById<TextView>(R.id.address)

            val collegeIdTextView = findViewById<TextView>(R.id.college_id)
            val collegeNameTextView = findViewById<TextView>(R.id.college_name)

            val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            val databaseHelper = DatabaseHelper(this)
            val collegeAdminDAO = CollegeAdminDAO(databaseHelper)
            val collegeDAO = CollegeDAO(databaseHelper)
            val collegeAdmin = collegeAdminDAO.get(collegeAdminId)
            val college = collegeDAO.get(collegeId)
            val user = collegeAdmin?.user

            toolBar.setNavigationOnClickListener {
                finish()
            }

            userIdTextView.append(" C/$collegeId-U/$collegeAdminId")
            userEmailIdTextView.append(" ${collegeAdmin?.user?.emailID}")
            userNameTextView.text = user?.name
            contactNumberTextView.text = user?.contactNumber
            dateOfBirthTextView.text = user?.dateOfBirth
            genderTextView.text = user?.gender
            userAddressTextView.text = user?.address

            collegeIdTextView.text  = college?.id.toString()
            collegeNameTextView.text = college?.name

            floatingActionButton.setOnClickListener{
                val collegeAdminUpdateBottomSheet = CollegeAdminUpdateBottomSheet.newInstance(collegeAdminId)
                collegeAdminUpdateBottomSheet?.show(supportFragmentManager, "collegeAdminUpdateDialog")
            }

            supportFragmentManager.setFragmentResultListener("collegeAdminUpdateFragmentPosition", this){_, _->
                val newCollegeAdmin = collegeAdminDAO.get(collegeAdminId)
                val newUser = newCollegeAdmin?.user
                userNameTextView.text = newUser?.name
                contactNumberTextView.text = newUser?.contactNumber
                userAddressTextView.text = newUser?.address
            }
        }
    }
}