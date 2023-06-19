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

    private val userNameColumn = "U_NAME"
    private val userContactColumn = "U_CONTACT"
    private val userAddressColumn = "U_ADDRESS"
    private val userDateOfBirthColumn = "U_DOB"
    private val userGenderColumn = "U_GENDER"
    private val userPasswordColumn = "U_PASSWORD"
    private val userRoleColumn = "U_ROLE"
    private val userEmailColumn = "U_EMAIL_ID"

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
            put(userNameColumn,user.name)
            put(userContactColumn, user.contactNumber)
            put(userDateOfBirthColumn, user.dateOfBirth)
            put(userGenderColumn, user.gender)
            put(userAddressColumn, user.address)
            put(userPasswordColumn, user.password)
            put(userRoleColumn,user.role)
            put(userEmailColumn, user.emailID)
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
            db.delete(tableName, "$primaryKey=?", arrayOf(userID.toString()))
            db.delete(userTable, "$userPrimaryKey=?", arrayOf(userID.toString()))
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

    fun update(collegeAdmin: CollegeAdmin?){

        if(collegeAdmin!=null){
            val db = databaseHelper.writableDatabase
            val contentValues = ContentValues().apply{
                put(userNameColumn, collegeAdmin.user.name)
                put(userContactColumn, collegeAdmin.user.contactNumber)
                put(userAddressColumn, collegeAdmin.user.address)
            }

            db.update(userTable,contentValues, "$userPrimaryKey=?", arrayOf(collegeAdmin.user.id.toString()))
            db.close()
        }

    }
}