package com.example.ums

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.model.databaseAccessObject.UserDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ManageProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_profile_page)

        val appBarLayout = findViewById<MaterialToolbar>(R.id.material_toolbar)
        val userDAO = UserDAO(DatabaseHelper(this))

        val bundle = intent.extras
        val userID = bundle!!.getInt("userID")
        val user = userDAO.getUser(userID)!!
        val changePasswordBottomSheet = ChangePasswordBottomSheet(userID, userDAO)

        val userIDTextView = findViewById<TextView>(R.id.user_email_textView)
        val userEmailIDTextView = findViewById<TextView>(R.id.user_email)
        val userNameTextLayout = findViewById<TextInputLayout>(R.id.current_password)
        val userContactTextLayout = findViewById<TextInputLayout>(R.id.new_password)
        val userAddressTextLayout = findViewById<TextInputLayout>(R.id.confirm_new_password)
        val confirmButton = findViewById<MaterialButton>(R.id.confirm_button)
        val changePasswordButton = findViewById<MaterialButton>(R.id.change_password)

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
            if(userNameTextLayout.editText!!.text.isEmpty()){
                userNameTextLayout.error = "Don't leave Name field blank"
            }
            if(userContactTextLayout.editText!!.text.isEmpty()){
                userContactTextLayout.error = "Don't leave Contact field blank"
            }
            if(userAddressTextLayout.editText!!.text.isEmpty()){
                userAddressTextLayout.error = "Don't leave Address field blank"
            }
            if(userNameTextLayout.editText!!.text.isNotEmpty() and
                userContactTextLayout.editText!!.text.isNotEmpty() and
                userAddressTextLayout.editText!!.text.isNotEmpty()
            ){

                user.name = userNameTextLayout.editText!!.text.toString()
                user.contactNumber = userContactTextLayout.editText!!.text.toString()
                user.address = userAddressTextLayout.editText!!.text.toString()

                userDAO.update(userID, user)

                Toast.makeText(this, "Details Updated!!!", Toast.LENGTH_SHORT).show()
            }
        }

        changePasswordButton.setOnClickListener {
            changePasswordBottomSheet.show(supportFragmentManager, "bottomSheetDialog")
        }
    }
}