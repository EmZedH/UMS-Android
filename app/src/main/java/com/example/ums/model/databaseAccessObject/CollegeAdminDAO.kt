package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.CollegeAdmin

class CollegeAdminDAO(private val databaseHelper: DatabaseHelper) {

    private val tableName = "COLLEGE_ADMIN"
    private val primaryKey = "CA_ID"
    private val collegeID = "COLLEGE_ID"

    fun get(collegeID : Int) : CollegeAdmin?{
        var collegeAdmin : CollegeAdmin? = null
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName WHERE $primaryKey = $collegeID", null)
        if(cursor.moveToFirst()){
            collegeAdmin = CollegeAdmin(
                cursor.getInt(0),
                cursor.getInt(1)
            )

        }
        cursor.close()
        return collegeAdmin
    }
    fun getList() : List<CollegeAdmin>{
        val collegeAdmins = mutableListOf<CollegeAdmin>()
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            collegeAdmins.add(
                CollegeAdmin(
                    cursor.getInt(0),
                    cursor.getInt(1))
            )
            cursor.moveToNext()
        }
        cursor.close()
        return collegeAdmins
    }

    fun insert(college : CollegeAdmin){
        val contentValues = ContentValues().apply {
            put(primaryKey, college.id)
            put(collegeID,college.collegeID)
        }
        databaseHelper.writableDatabase.insert(tableName,null, contentValues)
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
}