package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.Department

class DepartmentDAO(private val databaseHelper: DatabaseHelper) {

    private val tableName = "DEPARTMENT"
    private val primaryKey = "DEPT_ID"
    private val departmentName = "DEPT_NAME"
    private val collegeID = "COLLEGE_ID"

    fun get(departmentID : Int, collegeID: Int) : Department?{
        var department : Department? = null
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName WHERE $primaryKey = $departmentID AND ${this.collegeID} = $collegeID", null)
        if(cursor.moveToFirst()){
            department = Department(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2)
            )

        }
        cursor.close()
        return department
    }
    fun getList(collegeID: Int) : List<Department>{
        val departments = mutableListOf<Department>()
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName WHERE ${this.collegeID} = $collegeID", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            departments.add(
                Department(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2)
                )
            )
            cursor.moveToNext()
        }
        cursor.close()
        return departments
    }

    fun insert(department : Department){
        val contentValues = ContentValues().apply {
            put("DEPT_ID", department.id)
            put("DEPT_NAME",department.name)
            put("COLLEGE_ID",department.collegeID)
        }
        databaseHelper.writableDatabase.insert(tableName,null, contentValues)
    }

    fun delete(id : Int){
        val deleteQuery = "DELETE FROM DEPARTMENT WHERE DEPT_ID = $id"

        databaseHelper.writableDatabase.execSQL(deleteQuery)

    }

    fun getNewID(collegeID: Int) : Int {

        val cursor = databaseHelper.readableDatabase.rawQuery("WITH RECURSIVE number_range AS (\n" +
                "  SELECT 1 AS number\n" +
                "  UNION ALL\n" +
                "  SELECT number + 1\n" +
                "  FROM number_range\n" +
                "  WHERE number <= (SELECT MAX($primaryKey) FROM $tableName WHERE ${this.collegeID} = $collegeID) \n" +
                ")\n" +
                "SELECT number FROM number_range EXCEPT \n" +
                "SELECT $primaryKey AS number FROM $tableName WHERE ${this.collegeID} = $collegeID;"
            , null)


        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        return newID
    }

    fun update(id : Int, department: Department){
        val db = databaseHelper.writableDatabase
        val contentValues = ContentValues().apply{
            put(departmentName, department.name)
        }

        db.update(tableName,contentValues, "$primaryKey=?", arrayOf(id.toString()))
        db.close()
    }
}