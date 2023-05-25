package com.example.ums.model.databaseAccessObject

import android.app.Activity
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ums.model.College

class CollegeDAO(context: Activity) : SQLiteOpenHelper(context, "ums.db",null,1){

    private val createTableCollege = "CREATE TABLE IF NOT EXISTS \"COLLEGE\" (\n" +
            "\t\"C_ID\"\tINTEGER PRIMARY KEY,\n" +
            "\t\"C_NAME\"\tTEXT,\n" +
            "\t\"C_ADDRESS\"\tTEXT,\n" +
            "\t\"C_TELEPHONE\"\tTEXT\n" +
            ")"

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(createTableCollege)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    fun getList() : List<College>{
        val collegeList = mutableListOf<College>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM COLLEGE", null)
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
        close()
        return collegeList
    }

    fun insertCollege(college : College){
        val contentValues = ContentValues().apply {
            put("C_ID", college.collegeID)
            put("C_NAME",college.collegeName)
            put("C_ADDRESS",college.collegeAddress)
            put("C_TELEPHONE",college.collegeTelephone)
        }

        writableDatabase.insert("COLLEGE",null, contentValues)
    }

    fun delete(collegeID : Int){
        println("COLLEGE ID = $collegeID")
        val deleteQuery = "DELETE FROM COLLEGE WHERE C_ID = $collegeID"

        writableDatabase.execSQL(deleteQuery)

    }

    fun getNewID() : Int {

        val cursor = readableDatabase.rawQuery("WITH RECURSIVE number_range AS (\n" +
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