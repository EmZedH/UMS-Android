package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.Department

class DepartmentDAO(private val databaseHelper: DatabaseHelper) {

    private val tableName = "DEPARTMENT"
    private val primaryKey = "DEPT_ID"
    private val departmentName = "DEPT_NAME"
    private val collegeIDKey = "COLLEGE_ID"
    private val courseTable = "COURSE"

    private val courseProfessorTable = "COURSE_PROFESSOR_TABLE"
    private val professorTable = "PROFESSOR"
    private val recordTable = "RECORDS"
    private val studentTable = "STUDENT"
    private val testTable = "TEST"

    fun get(departmentID : Int?, collegeID: Int?) : Department?{
        departmentID ?: return null
        collegeID ?: return null
        var department : Department? = null
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName WHERE $primaryKey = $departmentID AND ${this.collegeIDKey} = $collegeID", null)
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
    fun getList(collegeID: Int?) : List<Department>{
        collegeID ?: return emptyList()
        val departments = mutableListOf<Department>()
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName WHERE ${this.collegeIDKey} = $collegeID", null)
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

    fun getOtherDepartment(departmentID: Int, collegeID: Int): List<Department>{
        val departments = mutableListOf<Department>()
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName WHERE $primaryKey != $departmentID AND $collegeIDKey = $collegeID", null)
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

    fun delete(id : Int, collegeID: Int){
        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try {
            db.execSQL("DELETE FROM $tableName WHERE $primaryKey = $id AND $collegeIDKey = $collegeID")
            db.execSQL("DELETE FROM $courseTable WHERE $primaryKey = $id AND $collegeIDKey = $collegeID")

            db.execSQL("DELETE FROM $courseProfessorTable WHERE $primaryKey = $id AND $collegeIDKey = $collegeID")
            db.execSQL("DELETE FROM $professorTable WHERE $primaryKey = $id AND $collegeIDKey = $collegeID")
            db.execSQL("DELETE FROM $recordTable WHERE $primaryKey = $id AND $collegeIDKey = $collegeID")
            db.execSQL("DELETE FROM USER WHERE U_ID = (SELECT STUDENT_ID FROM STUDENT WHERE DEPT_ID = $id AND COLLEGE_ID = $collegeID)")
            db.execSQL("DELETE FROM $studentTable WHERE $primaryKey = $id AND $collegeIDKey = $collegeID")
            db.execSQL("DELETE FROM $testTable WHERE $primaryKey = $id AND $collegeIDKey = $collegeID")
            db.setTransactionSuccessful()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.endTransaction()
        }
        db.close()
    }

    fun getNewID(collegeID: Int) : Int {

        val cursor = databaseHelper.readableDatabase.rawQuery("WITH RECURSIVE number_range AS (\n" +
                "  SELECT 1 AS number\n" +
                "  UNION ALL\n" +
                "  SELECT number + 1\n" +
                "  FROM number_range\n" +
                "  WHERE number <= (SELECT MAX($primaryKey) FROM $tableName WHERE ${this.collegeIDKey} = $collegeID) \n" +
                ")\n" +
                "SELECT number FROM number_range EXCEPT \n" +
                "SELECT $primaryKey AS number FROM $tableName WHERE ${this.collegeIDKey} = $collegeID;"
            , null)


        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        return newID
    }

    fun update(department: Department){
        val db = databaseHelper.writableDatabase
        val contentValues = ContentValues().apply{
            put(departmentName, department.name)
        }

        db.update(tableName,contentValues, "$primaryKey=? AND ${this.collegeIDKey} = ?", arrayOf(department.id.toString(), department.collegeID.toString()))
        db.close()
    }
}