package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.Test

class TestDAO(private val databaseHelper: DatabaseHelper) {

    private val tableName = "TEST"
    private val primaryKey = "TEST_ID"

    fun get(testID: Int?, studentID: Int?, courseID: Int?, departmentID: Int?): Test?{
        testID ?: return null
        studentID ?: return null
        courseID ?: return null
        departmentID ?: return null
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM TEST WHERE TEST_ID = ? AND STUDENT_ID = ? AND COURSE_ID = ? AND DEPT_ID = ? AND TEST.COLLEGE_ID = (SELECT COLLEGE_ID FROM STUDENT WHERE STUDENT_ID = STUDENT_ID)",
                arrayOf(testID.toString(), studentID.toString(), courseID.toString(), departmentID.toString()))
        if(cursor!=null && cursor.moveToFirst()){
            return Test(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5)
            )
        }
        cursor.close()
        return null
    }

    fun getList(studentID: Int?, courseID: Int?, departmentID: Int?): List<Test>{
        studentID ?: return emptyList()
        courseID ?: return emptyList()
        departmentID ?: return emptyList()
        val tests = mutableListOf<Test>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM TEST WHERE STUDENT_ID = ? AND COURSE_ID = ? AND DEPT_ID = ?",
                arrayOf(studentID.toString(), courseID.toString(), departmentID.toString()))

        if(cursor!=null && cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                tests.add(
                    Test(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getInt(5)
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return tests
    }

    fun getNewID(studentID: Int, courseID: Int, departmentID: Int): Int{
        val cursor = databaseHelper.readableDatabase.rawQuery(
            "WITH RECURSIVE number_range AS (\n" +
                "  SELECT 1 AS number\n" +
                "  UNION ALL\n" +
                "  SELECT number + 1\n" +
                "  FROM number_range\n" +
                "  WHERE number <= (SELECT MAX($primaryKey) FROM $tableName WHERE STUDENT_ID = $studentID AND COURSE_ID = $courseID AND DEPT_ID = $departmentID) \n" +
                ")\n" +
                "SELECT number FROM number_range EXCEPT \n" +
                "SELECT $primaryKey AS number FROM $tableName WHERE STUDENT_ID = $studentID AND COURSE_ID = $courseID AND DEPT_ID = $departmentID"
            , null)

        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        return newID
    }

    fun delete(testID: Int?, studentID: Int?, courseID: Int?, departmentID: Int?){
        testID ?: return
        studentID ?: return
        courseID ?: return
        departmentID ?: return

        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try{
            db.delete(tableName, "$primaryKey = ? AND STUDENT_ID = ? AND COURSE_ID = ? AND DEPT_ID = ?", arrayOf(testID.toString(), studentID.toString(), courseID.toString(), departmentID.toString()))
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

    fun insert(test: Test){
        val contentValues = ContentValues().apply {
            put("TEST_ID", test.id)
            put("STUDENT_ID", test.studentID)
            put("COURSE_ID", test.courseID)
            put("DEPT_ID", test.departmentID)
            put("COLLEGE_ID", test.collegeID)
            put("TEST_MARKS", test.mark)
        }
        databaseHelper.writableDatabase.insert(tableName, null, contentValues)
    }

    fun update(test: Test){
        val db = databaseHelper.writableDatabase
        val contentValues = ContentValues().apply{
            put("TEST_MARKS", test.mark)
        }

        db.update(tableName,contentValues, "$primaryKey = ? AND STUDENT_ID = ? AND COURSE_ID = ? AND DEPT_ID = ?", arrayOf(test.id.toString(), test.studentID.toString(), test.courseID.toString(), test.departmentID.toString()))
        db.close()
    }

    fun getAverageTestMark(studentID: Int?, courseID: Int?, departmentID: Int?): Int?{
        studentID ?: return null
        courseID ?: return null
        departmentID ?: return null
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT AVG(TEST_MARKS) FROM TEST WHERE STUDENT_ID = ? AND COURSE_ID = ? AND DEPT_ID = ?", arrayOf(studentID.toString(), courseID.toString(), departmentID.toString()))
        if(cursor!=null && cursor.moveToFirst()){
            return cursor.getInt(0)
        }
        cursor.close()
        return null
    }

}