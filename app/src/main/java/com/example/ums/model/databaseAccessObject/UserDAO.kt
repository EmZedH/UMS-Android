package com.example.ums.model.databaseAccessObject

import com.example.ums.DatabaseHelper
import com.example.ums.model.User

class UserDAO(private val databaseHelper : DatabaseHelper) {

    fun getUser(userEmailID : String, password : String) : User? {

        var user : User? = null
        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM USER WHERE U_EMAIL_ID = \"$userEmailID\" AND U_PASSWORD = \"$password\"", null)

        if(cursor.moveToFirst()){

            user = User(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8)
            )

            print(cursor.getString(1))
        }

        cursor.close()
        db.close()

        return user
    }

}