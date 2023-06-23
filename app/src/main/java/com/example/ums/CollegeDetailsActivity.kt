package com.example.ums

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.bottomsheetdialogs.CollegeUpdateBottomSheet
import com.example.ums.model.databaseAccessObject.CollegeDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CollegeDetailsActivity: AppCompatActivity() {

    private var collegeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.college_profile_page)
        collegeId = intent.extras?.getInt("college_details_college_id")
        val collegeId = collegeId
        if(collegeId != null){
            val userIdTextView = findViewById<TextView>(R.id.course_id_text_view)
            val userNameTextView = findViewById<TextView>(R.id.user_name)
            val contactNumberTextView = findViewById<TextView>(R.id.contact_number)
            val userAddressTextView = findViewById<TextView>(R.id.address)

            val floatingActionButton = findViewById<FloatingActionButton>(R.id.edit_floating_action_button)
            val toolBar = findViewById<MaterialToolbar>(R.id.top_app_bar)
            val databaseHelper = DatabaseHelper(this)
            val collegeDAO = CollegeDAO(databaseHelper)
            val college = collegeDAO.get(collegeId)

            toolBar.setNavigationOnClickListener {
                finish()
            }

            userIdTextView.append(collegeId.toString())
            userNameTextView.text = college?.name
            contactNumberTextView.text = college?.telephone
            userAddressTextView.text = college?.address

            floatingActionButton.setOnClickListener{
                val collegeUpdateBottomSheet = CollegeUpdateBottomSheet()
                collegeUpdateBottomSheet.arguments = Bundle().apply {
                    putInt("college_update_college_id", collegeId)
                }
                collegeUpdateBottomSheet.show(supportFragmentManager, "CollegeUpdateDialog")
            }

            supportFragmentManager.setFragmentResultListener("CollegeUpdateBottomSheet", this){_, _->
                val newCollege = collegeDAO.get(collegeId)
                userNameTextView.text = newCollege?.name
                contactNumberTextView.text = newCollege?.telephone
                userAddressTextView.text = newCollege?.address
            }
        }
    }
}