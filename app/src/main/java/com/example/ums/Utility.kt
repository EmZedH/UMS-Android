package com.example.ums

import android.util.Log

class Utility {
    companion object{
        fun isValidContactNumber(contactNumber : String): Boolean {
            val number = contactNumber.replace("\\D".toRegex(), "")
            return number.length == 10
        }

//        fun isEmailAddressFree(email: String, context: Activity): Boolean{
//            val databaseHelper = DatabaseHelper.newInstance(context)
//            val userDAO = UserDAO(databaseHelper)
//            val userList = userDAO.getList()
//            for (user in userList){
//                if(user.emailID.trim()==email.trim()){
//                    return false
//                }
//            }
//            return true
//        }

        fun isEmailDotCom(string: String): Boolean {
            Log.i("UtilityClass", "inside isEmailDotCom")
            val pattern = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
            return pattern.matches(string.trim())
        }

        fun isCorrectDateFormat(dateString: String): Boolean {
            val regex = Regex("""^\d{4}-([1-9]|1[0-9]|2[0-9]|3[01])-([1-9]|1[0-2])$""")
            return regex.matches(dateString)

        }

        fun stringToIds(idString: String): List<Int>{
            return idString.split("-").map { it.toInt() }
        }
    }
}