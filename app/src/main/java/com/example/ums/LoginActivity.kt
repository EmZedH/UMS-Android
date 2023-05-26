package com.example.ums

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ums.model.User
import com.example.ums.model.databaseAccessObject.UserDAO

class LoginActivity : AppCompatActivity() {

    private val userIDPasswordIncorrectString = "User email ID or Password incorrect"
    private val userIDPasswordProperString = "Enter fields properly"

    private val userDAO = UserDAO(DatabaseHelper(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val textViewUserNamePasswordIncorrect = findViewById<TextView>(R.id.textViewUserIDPasswordNotCorrect)
        val loginButton = findViewById<Button>(R.id.login_button)
        val userIDEditText = findViewById<EditText>(R.id.userIDEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        loginButton.setOnClickListener {

            textViewUserNamePasswordIncorrect.text = ""

            val userEmailID = userIDEditText.text.toString().lowercase()
            val password = passwordEditText.text.toString()

            val user = userDAO.getUser(userEmailID, password)

            if(user == null){

                textViewUserNamePasswordIncorrect.text =
                    if(userEmailID == "" || password == "")
                        userIDPasswordProperString
                    else
                        userIDPasswordIncorrectString

            }
            else{
                mainPage(user)
            }

        }
    }

    private fun mainPage(user : User){

        val intent = Intent(this, MainPageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userName", user.name)
        bundle.putInt("userID", user.id)
        bundle.putString("userRole", user.role)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()

    }

//    private fun addColleges(){
//
//        val collegeDAO = CollegeDAO(dbHelper)
//        collegeDAO.insert(College(collegeDAO.getNewID(),"PTU","PONDY","0000000000"))
//        collegeDAO.insert(College(collegeDAO.getNewID(),"EDU","CHENNAI","0000000002"))
//        collegeDAO.insert(College(collegeDAO.getNewID(),"DTU","DELHI","0000000003"))
//    }
}