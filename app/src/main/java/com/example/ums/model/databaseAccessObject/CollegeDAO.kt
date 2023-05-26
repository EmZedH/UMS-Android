package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.College

class CollegeDAO(private val databaseHelper: DatabaseHelper) {


    fun getList() : List<College>{
        val collegeList = mutableListOf<College>()
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM COLLEGE", null)
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

    fun insertCollege(college : College){
        val contentValues = ContentValues().apply {
            put("C_ID", college.id)
            put("C_NAME",college.name)
            put("C_ADDRESS",college.address)
            put("C_TELEPHONE",college.telephone)
        }
        databaseHelper.writableDatabase.insert("COLLEGE",null, contentValues)
    }

    fun delete(collegeID : Int){
        println("COLLEGE ID = $collegeID")
        val deleteQuery = "DELETE FROM COLLEGE WHERE C_ID = $collegeID"

        databaseHelper.writableDatabase.execSQL(deleteQuery)

    }

    fun getNewID() : Int {

        val cursor = databaseHelper.readableDatabase.rawQuery("WITH RECURSIVE number_range AS (\n" +
                "  SELECT 1 AS number\n" +
                "  UNION ALL\n" +
                "  SELECT number + 1\n" +
                "  FROM number_range\n" +
                "  WHERE number <= (SELECT MAX(C_ID) FROM COLLEGE) \n" +
                ")\n" +
                "SELECT number FROM number_range EXCEPT \n" +
                "SELECT C_ID AS number FROM COLLEGE;"
            , null)


        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        return newID
    }

}