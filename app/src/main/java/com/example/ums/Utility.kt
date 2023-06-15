package com.example.ums

import android.app.Activity
import com.example.ums.model.databaseAccessObject.UserDAO

class Utility {
    companion object{
        fun isValidContactNumber(contactNumber : String): Boolean {
            val number = contactNumber.replace("\\D".toRegex(), "")
            return number.length == 10
        }

        fun isEmailAddressFree(email: String, context: Activity): Boolean{
            val userDAO = UserDAO(DatabaseHelper(context))
            val userList = userDAO.getList()
            for (user in userList){
                if(user.emailID.trim()==email.trim()){
                    return false
                }
            }
            return true
        }

        fun isEmailDotCom(string: String): Boolean {
            val pattern = Regex("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.com")
            return pattern.matches(string.trim())
        }
    }
}