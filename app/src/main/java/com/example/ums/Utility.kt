package com.example.ums

class Utility {
    companion object{
        fun isValidContactNumber(contactNumber : String): Boolean {
            val number = contactNumber.replace("\\D".toRegex(), "")
            return number.length == 10
        }
    }
}