package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.College

class CollegeDAO(private val databaseHelper : DatabaseHelper) {

    fun getList() : List<College>{
        val collegeList = mutableListOf<College>()
        val db = databaseHelper
        val cursor = db.readableDatabase.rawQuery("SELECT * FROM COLLEGE", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            collegeList.add(College(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3))
            )
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return collegeList
    }

    fun insert(college : College){
        val db = databaseHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("C_ID", college.collegeID)
            put("C_NAME",college.collegeName)
            put("C_ADDRESS",college.collegeAddress)
            put("C_TELEPHONE",college.collegeTelephone)
        }

        db.insert("COLLEGE",null, contentValues)
        db.close()
    }

    fun delete(collegeID : Int){
        val db = databaseHelper.writableDatabase
        val deleteQuery = "DELETE FROM COLLEGE WHERE C_ID = $collegeID"

        db.execSQL(deleteQuery, null)

        db.close()
    }

    fun getNewID() : Int{
        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery("SELECT MIN(C_ID) + 1 AS smallest_number\n" +
                "FROM COLLEGE\n" +
                "WHERE (C_ID + 1) NOT IN (SELECT C_ID FROM COLLEGE)\n", null)

        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        db.close()
        if(newID==0)
            return 1
        return newID
    }
}