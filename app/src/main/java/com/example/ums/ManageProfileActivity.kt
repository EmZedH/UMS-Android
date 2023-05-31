package com.example.ums

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.BottomSheetDialogs.ChangePasswordBottomSheet
import com.example.ums.model.databaseAccessObject.UserDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ManageProfileActivity : AppCompatActivity() {

    private lateinit var confirmButton : MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_profile_page)

        val appBarLayout = findViewById<MaterialToolbar>(R.id.material_toolbar)
        val userDAO = UserDAO(DatabaseHelper(this))

        val bundle = intent.extras
        val userID = bundle!!.getInt("userID")
        val user = userDAO.get(userID)!!
        val changePasswordBottomSheet = ChangePasswordBottomSheet(userID, userDAO)

        val userIDTextView = findViewById<TextView>(R.id.college_id_text_view)
        val userEmailIDTextView = findViewById<TextView>(R.id.user_email)
        val userNameTextLayout = findViewById<TextInputLayout>(R.id.college_name_layout)
        val userContactTextLayout = findViewById<TextInputLayout>(R.id.college_address_layout)
        val userAddressTextLayout = findViewById<TextInputLayout>(R.id.college_telephone_layout)
        val changePasswordButton = findViewById<MaterialButton>(R.id.change_password)

        confirmButton = findViewById(R.id.confirm_button)

        userIDTextView.setText(R.string.user_id_string)
        userIDTextView.append(" SA/$userID")
        userEmailIDTextView.text = user.emailID

        userNameTextLayout.editText!!.setText(user.name)
        userContactTextLayout.editText!!.setText(user.contactNumber)
        userAddressTextLayout.editText!!.setText(user.address)

        appBarLayout.setNavigationOnClickListener {
            finish()
        }

        confirmButton.setOnClickListener {
            var flag = true
            if(userNameTextLayout.editText!!.text.isEmpty()){
                userNameTextLayout.error = "Don't leave Name field blank"
                flag = false
            }
            if(userContactTextLayout.editText!!.text.isEmpty()){
                flag = false
                userContactTextLayout.error = "Don't leave Contact field blank"
            }
            else if(!Utility.isValidContactNumber(userContactTextLayout.editText!!.text.toString())){
                flag = false
                userContactTextLayout.error = "Enter 10 digit contact number"
            }
            if(userAddressTextLayout.editText!!.text.isEmpty()){
                flag = false
                userAddressTextLayout.error = "Don't leave Address field blank"
            }
            if(flag){

                user.name = userNameTextLayout.editText!!.text.toString()
                user.contactNumber = userContactTextLayout.editText!!.text.toString()
                user.address = userAddressTextLayout.editText!!.text.toString()

                userDAO.update(userID, user)

                Toast.makeText(this, "Details Updated!", Toast.LENGTH_SHORT).show()
                userContactTextLayout.error = null
                userAddressTextLayout.error = null
                userNameTextLayout.error = null
            }
        }

        changePasswordButton.setOnClickListener {
            changePasswordBottomSheet.show(supportFragmentManager, "bottomSheetDialog")
        }
    }
}