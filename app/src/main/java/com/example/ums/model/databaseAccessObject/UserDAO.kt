package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.User

class UserDAO(private val databaseHelper : DatabaseHelper) {

    private val tableName = "USER"
    private val primaryKey = "U_ID"
    private val userName = "U_NAME"
    private val userEmail = "U_EMAIL_ID"
    private val userPassword = "U_PASSWORD"
    private val userContact = "U_CONTACT"
    private val userAddress = "U_ADDRESS"

    fun get(userEmailID : String, password : String) : User? {

        var user : User? = null
        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $tableName WHERE $userEmail = \"$userEmailID\" AND $userPassword = \"$password\"", null)

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
        val cursor = db.rawQuery("SELECT * FROM $tableName WHERE $primaryKey = $userID", null)

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
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName", null)
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
            put(userName, user.name)
            put(userContact, user.contactNumber)
            put(userAddress, user.address)
            put(userPassword, user.password)
        }

        db.update(tableName,contentValues, "$primaryKey=?", arrayOf(userID.toString()))
        db.close()
    }

//    fun delete(userID: Int){
//        val db = databaseHelper.writableDatabase
//        db.beginTransaction()
//        try{
//            db.execSQL("DELETE FROM USER WHERE U_ID = $userID")
//            db.execSQL("DELETE FROM COLLEGE_ADMIN WHERE CA_ID = $userID")
//            db.execSQL("DELETE FROM PROFESSOR WHERE P_ID = $userID")
//            db.setTransactionSuccessful()
//        }
//        catch (e: Exception){
//            e.printStackTrace()
//        }
//        finally {
//            db.endTransaction()
//        }
//        db.close()
//    }
}