package com.example.ums

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.bottomsheetdialogs.ChangePasswordBottomSheet
import com.example.ums.model.databaseAccessObject.UserDAO
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class ManageProfileActivity : AppCompatActivity() {

    private lateinit var confirmButton : MaterialButton

    private var userNameText: String? = null
    private var contactNumberText: String? = null
    private var userAddressText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userNameText = savedInstanceState?.getString("manage_profile_user_name_text")
        contactNumberText = savedInstanceState?.getString("manage_profile_contact_number_text")
        userAddressText = savedInstanceState?.getString("manage_profile_user_address_text")

        setContentView(R.layout.manage_profile_page)

        val appBarLayout = findViewById<MaterialToolbar>(R.id.material_toolbar)
        val userDAO = UserDAO(DatabaseHelper(this))

        val bundle = intent.extras
        val userID = bundle!!.getInt("userID")
        val user = userDAO.get(userID)!!

        val userIDTextView = findViewById<TextView>(R.id.course_id_text_view)
        val userEmailIDTextView = findViewById<TextView>(R.id.user_email)
        val userNameTextLayout = findViewById<TextInputLayout>(R.id.course_name_layout)
        val userContactTextLayout = findViewById<TextInputLayout>(R.id.college_address_layout)
        val userAddressTextLayout = findViewById<TextInputLayout>(R.id.college_telephone_layout)
        val passwordChangeButton = findViewById<MaterialButton>(R.id.change_password)

        confirmButton = findViewById(R.id.confirm_button)

        userIDTextView.setText(R.string.user_id_string)
        userIDTextView.append(" SA/$userID")
        userEmailIDTextView.text = user.emailID

        userNameTextLayout.editText?.setText(userNameText ?: user.name)
        userContactTextLayout.editText?.setText(contactNumberText ?: user.contactNumber)
        userAddressTextLayout.editText?.setText(userAddressText ?: user.address)

        appBarLayout.setNavigationOnClickListener {
            finish()
        }

        confirmButton.isEnabled =
            (userNameTextLayout.editText?.text.toString() != user.name) ||
                    (userContactTextLayout.editText?.text.toString() != user.contactNumber) ||
                    (userAddressTextLayout.editText?.text.toString() != user.address)

        userNameTextLayout.editText?.addTextChangedListener(textListener(user.name, userNameTextLayout) {
            userNameText = userNameTextLayout.editText?.text.toString()
        })
        userContactTextLayout.editText?.addTextChangedListener(textListener(user.contactNumber, userContactTextLayout) {
            contactNumberText = userContactTextLayout.editText?.text.toString()
        })
        userAddressTextLayout.editText?.addTextChangedListener(textListener(user.address, userAddressTextLayout) {
            userAddressText = userAddressTextLayout.editText?.text.toString()
        })

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
                confirmButton.isEnabled = false
                finish()
            }
        }

        passwordChangeButton.setOnClickListener {
            val changePasswordBottomSheet = ChangePasswordBottomSheet.newInstance(userID)
            changePasswordBottomSheet?.show(supportFragmentManager, "bottomSheetDialog")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("manage_profile_user_name_text", userNameText)
        outState.putString("manage_profile_contact_number_text", contactNumberText)
        outState.putString("manage_profile_user_address_text", userAddressText)
    }
    private fun textListener(userDetails: String, textInputLayout: TextInputLayout, action: (()->Unit)): TextWatcher{
        return object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                confirmButton.isEnabled = p0?.toString() != userDetails
                textInputLayout.error = null
                action()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }
    }
}