package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.CollegeAdmin
import com.example.ums.model.User

class CollegeAdminDAO(private val databaseHelper: DatabaseHelper) {

    private val tableName = "COLLEGE_ADMIN"
    private val primaryKey = "CA_ID"
    private val userTable = "USER"
    private val userPrimaryKey = "U_ID"
    private val collegeKey = "COLLEGE_ID"

//    fun get(id : Int, collegeID: Int) : CollegeAdmin?{
//        var collegeAdmin : CollegeAdmin? = null
//        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName INNER JOIN $userTable ON ($userTable.$userPrimaryKey = $tableName.$primaryKey) WHERE $primaryKey = $id AND $collegeKey = $collegeID", null)
//        if(cursor.moveToFirst()){
//            collegeAdmin = CollegeAdmin(
//                User(
//                    cursor.getInt(2),
//                    cursor.getString(3),
//                    cursor.getString(4),
//                    cursor.getString(5),
//                    cursor.getString(6),
//                    cursor.getString(7),
//                    cursor.getString(8),
//                    cursor.getString(9),
//                    cursor.getString(10)
//                ),
//                cursor.getInt(1)
//            )
//
//        }
//        cursor.close()
//        return collegeAdmin
//    }
    fun get(id : Int) : CollegeAdmin?{
        var collegeAdmin : CollegeAdmin? = null
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName INNER JOIN $userTable ON ($userTable.$userPrimaryKey = $tableName.$primaryKey) WHERE $primaryKey = $id", null)
        if(cursor.moveToFirst()){
            collegeAdmin = CollegeAdmin(
                User(
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10)
                ),
                cursor.getInt(1)
            )

        }
        cursor.close()
        return collegeAdmin
    }
    fun getList(collegeID: Int) : List<CollegeAdmin>{
        val collegeAdmins = mutableListOf<CollegeAdmin>()
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName INNER JOIN $userTable ON ($userTable.$userPrimaryKey = $tableName.$primaryKey) WHERE $collegeKey = $collegeID", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            collegeAdmins.add(
                CollegeAdmin(
                    User(
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10)
                    ),
                    cursor.getInt(1))
            )
            cursor.moveToNext()
        }
        cursor.close()
        return collegeAdmins
    }

    fun insert(collegeAdmin : CollegeAdmin){
        val user = collegeAdmin.user
        val contentValuesCollegeAdmin = ContentValues().apply {
            put(primaryKey, user.id)
            put(collegeKey,collegeAdmin.collegeID)
        }
        val contentValuesUser = ContentValues().apply {
            put(userPrimaryKey, user.id)
            put("U_NAME",user.name)
            put("U_CONTACT", user.contactNumber)
            put("U_DOB", user.dateOfBirth)
            put("U_GENDER", user.gender)
            put("U_ADDRESS", user.address)
            put("U_PASSWORD", user.password)
            put("U_ROLE",user.role)
            put("U_EMAIL_ID", user.emailID)
        }
        val db = databaseHelper.writableDatabase
        db.beginTransaction()
            try{
                db.insert(tableName,null, contentValuesCollegeAdmin)
                db.insert(userTable, null, contentValuesUser)
                db.setTransactionSuccessful()
            }
            finally {
                db.endTransaction()
            }
        db.close()
    }

    fun delete(userID: Int){
        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try{
            db.execSQL("DELETE FROM USER WHERE U_ID = $userID")
            db.execSQL("DELETE FROM COLLEGE_ADMIN WHERE CA_ID = $userID")
            db.setTransactionSuccessful()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        finally {
            db.endTransaction()
        }
        db.close()
    }

    fun getNewID(): Int{

        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT COALESCE(MAX($userPrimaryKey), 0) + 1 FROM $userTable;", null)
        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        return newID
    }
}