package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.User

class UserDAO(private val databaseHelper : DatabaseHelper) {

    fun get(userEmailID : String, password : String) : User? {

        var user : User? = null
        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM USER WHERE U_EMAIL_ID = \"$userEmailID\" AND U_PASSWORD = \"$password\"", null)

        if(cursor.moveToFirst()){

            user = User(
                cursor.getInt(0),
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

    fun get(userID : Int) : User?{


        var user : User? = null
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM USER WHERE U_ID = $userID", null)

        if(cursor.moveToFirst()){

            user = User(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8)
            )
        }

        cursor.close()
        db.close()

        return user
    }

    fun getList() : List<User>{
        val userList = mutableListOf<User>()
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM USER", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            userList.add(
                User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8)
                )
            )
            cursor.moveToNext()
        }
        cursor.close()
        return userList
    }

    fun update(userID : Int, user : User){

        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues().apply{
            put("U_NAME", user.name)
            put("U_CONTACT", user.contactNumber)
            put("U_ADDRESS", user.address)
            put("U_PASSWORD", user.password)
        }

        db.update("USER",contentValues, "U_ID=?", arrayOf(userID.toString()))
        db.close()
    }

}