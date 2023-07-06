package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.interfaces.DeletableDAO
import com.example.ums.model.College

class CollegeDAO(private val databaseHelper: DatabaseHelper): DeletableDAO {

    private val tableName = "COLLEGE"
    private val primaryKey = "C_ID"
    private val collegeName = "C_NAME"
    private val collegeAddress = "C_ADDRESS"
    private val collegeTelephone = "C_TELEPHONE"
    private val departmentTable = "DEPARTMENT"
    private val courseTable = "COURSE"
    private val collegeKey = "COLLEGE_ID"

    private val collegeAdminTable = "COLLEGE_ADMIN"
    private val courseProfessorTable = "COURSE_PROFESSOR_TABLE"
    private val professorTable = "PROFESSOR"
    private val recordTable = "RECORDS"
    private val studentTable = "STUDENT"
    private val testTable = "TEST"
    fun get(collegeID : Int?) : College?{
        collegeID ?: return null
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
            db.execSQL("DELETE FROM USER WHERE U_ID = (SELECT CA_ID AS ID FROM $collegeAdminTable WHERE $collegeKey = $collegeID UNION SELECT PROF_ID AS ID FROM $professorTable WHERE $collegeKey = $collegeID UNION SELECT STUDENT_ID AS ID FROM $studentTable WHERE $collegeKey = $collegeID)")
            db.execSQL("DELETE FROM $collegeAdminTable WHERE $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $departmentTable WHERE $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $courseTable WHERE $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $tableName WHERE $primaryKey = $collegeID")
            db.execSQL("DELETE FROM $courseProfessorTable WHERE $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $professorTable WHERE $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $recordTable WHERE $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $studentTable WHERE $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $testTable WHERE $collegeKey = $collegeID")
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

    override fun deleteList(idList: MutableList<List<Int>>) {
        for (id in idList){
            val collegeID = id[0]
            delete(collegeID)
        }
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