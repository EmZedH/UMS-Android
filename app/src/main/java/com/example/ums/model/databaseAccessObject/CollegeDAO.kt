package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.College
import java.lang.Exception

class CollegeDAO(private val databaseHelper: DatabaseHelper) {

    private val tableName = "COLLEGE"
    private val primaryKey = "C_ID"
    private val collegeName = "C_NAME"
    private val collegeAddress = "C_ADDRESS"
    private val collegeTelephone = "C_TELEPHONE"
    private val departmentTable = "DEPARTMENT"
    private val departmentForeignKey = "COLLEGE_ID"

    fun get(collegeID : Int) : College?{
        var college : College? = null
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName WHERE $primaryKey = $collegeID", null)
        if(cursor.moveToFirst()){
            college = College(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
            )

        }
        cursor.close()
        return college
    }
    fun getList() : List<College>{
        val collegeList = mutableListOf<College>()
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            collegeList.add(
                College(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3))
            )
            cursor.moveToNext()
        }
        cursor.close()
        return collegeList
    }

    fun insert(college : College){
        val contentValues = ContentValues().apply {
            put(primaryKey, college.id)
            put(collegeName,college.name)
            put(collegeAddress,college.address)
            put(collegeTelephone,college.telephone)
        }
        databaseHelper.writableDatabase.insert(tableName,null, contentValues)
    }

    fun delete(collegeID : Int){
        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try{
            db.execSQL("DELETE FROM USER WHERE U_ID = (SELECT CA_ID FROM COLLEGE_ADMIN WHERE COLLEGE_ID = $collegeID)")
            db.execSQL("DELETE FROM COLLEGE_ADMIN WHERE COLLEGE_ID = $collegeID")
            db.execSQL("DELETE FROM $departmentTable WHERE $departmentForeignKey = $collegeID")
            db.execSQL("DELETE FROM $tableName WHERE $primaryKey = $collegeID")
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

    fun getNewID() : Int {

        val cursor = databaseHelper.readableDatabase.rawQuery("WITH RECURSIVE number_range AS (\n" +
                "  SELECT 1 AS number\n" +
                "  UNION ALL\n" +
                "  SELECT number + 1\n" +
                "  FROM number_range\n" +
                "  WHERE number <= (SELECT MAX($primaryKey) FROM $tableName) \n" +
                ")\n" +
                "SELECT number FROM number_range EXCEPT \n" +
                "SELECT $primaryKey AS number FROM $tableName;"
            , null)


        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        return newID
    }

    fun update(college : College){
        val db = databaseHelper.writableDatabase
        val contentValues = ContentValues().apply{
            put(collegeName, college.name)
            put(collegeAddress, college.address)
            put(collegeTelephone, college.telephone)
        }

        db.update(tableName,contentValues, "$primaryKey=?", arrayOf(college.id.toString()))
        db.close()
    }
}