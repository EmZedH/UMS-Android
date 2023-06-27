package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.Course
import com.example.ums.model.CourseProfessor
import com.example.ums.model.Professor
import com.example.ums.model.Records
import com.example.ums.model.User

class RecordsDAO(private val databaseHelper: DatabaseHelper) {
    private val tableName = "RECORDS"
    private val courseProfessorTable = "COURSE_PROFESSOR_TABLE"
    private val professorTable = "PROFESSOR"
    private val userTable = "USER"
    private val courseTable = "COURSE"
    private val professorKey = "PROF_ID"
    private val userKey = "U_ID"
    private val courseKey = "COURSE_ID"
    private val departmentKey = "DEPT_ID"
    private val collegeKey = "COLLEGE_ID"
    private val studentKey = "STUDENT_ID"

    fun get(studentID: Int?, courseID: Int?): Records?{
        studentID ?: return null
        courseID ?: return null
        var records : Records? = null
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT $tableName.*, $userTable.*, $courseTable.*  FROM $tableName " +
                    "INNER JOIN $courseProfessorTable ON " +
                    "($tableName.$professorKey = $courseProfessorTable.$professorKey AND " +
                    "$tableName.$courseKey = $courseProfessorTable.$courseKey AND " +
                    "$tableName.$departmentKey = $courseProfessorTable.$departmentKey AND " +
                    "$tableName.$collegeKey = $courseProfessorTable.$collegeKey) " +
                    "INNER JOIN $userTable ON " +
                    "($userTable.$userKey = $tableName.$professorKey)" +
                    "INNER JOIN $professorTable ON " +
                    "($tableName.$professorKey = $professorTable.$professorKey) " +
                    "INNER JOIN $courseTable ON " +
                    "($courseTable.$courseKey = $courseProfessorTable.$courseKey AND " +
                    "$courseTable.$departmentKey = $courseProfessorTable.$departmentKey AND " +
                    "$courseTable.$collegeKey = $courseProfessorTable.$collegeKey) WHERE " +
                    "$tableName.$studentKey = $studentID AND " +
                    "$tableName.$courseKey = $courseID AND " +
                    "$tableName.$collegeKey = $courseTable.$collegeKey",
                null)

        if(cursor.moveToFirst()){
            records = Records(
                cursor.getInt(0),
                CourseProfessor(
                    Professor(
                        User(
                            cursor.getInt(11),
                            cursor.getString(12),
                            cursor.getString(13),
                            cursor.getString(14),
                            cursor.getString(15),
                            cursor.getString(16),
                            cursor.getString(17),
                            cursor.getString(18),
                            cursor.getString(19)
                        ),
                        cursor.getInt(2),
                        cursor.getInt(4)
                    ),
                    Course(
                        cursor.getInt(20),
                        cursor.getString(21),
                        cursor.getInt(22),
                        cursor.getInt(23),
                        cursor.getInt(24),
                        cursor.getString(25),
                        cursor.getString(26)
                    )
                ),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getInt(7),
                cursor.getInt(8),
                cursor.getString(9),
                cursor.getInt(10)
            )
        }
        cursor.close()
        return records
    }

    fun getList(professorID: Int, courseID: Int, isComplete: Boolean): List<Records>{
        val completeString = if(isComplete) "COMPLETED" else "NOT_COMPLETED"
        val records = mutableListOf<Records>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT $tableName.*, $userTable.*, $courseTable.* FROM $tableName INNER JOIN $userTable ON " +
                    "($userTable.$userKey = $tableName.$professorKey) INNER JOIN $courseTable ON " +
                    "($courseTable.$courseKey = $tableName.$courseKey AND " +
                    "$courseTable.$departmentKey = $tableName.$departmentKey AND " +
                    "$courseTable.$collegeKey = $tableName.$collegeKey) WHERE " +
                    "$tableName.$professorKey = $professorID AND " +
                    "$tableName.$courseKey = $courseID AND " +
                    "$tableName.$departmentKey = $courseTable.$departmentKey AND " +
                    "$tableName.$collegeKey = $courseTable.$collegeKey AND " +
                    "$tableName.STATUS = \"$completeString\"",
                null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            records.add(
                Records(
                    cursor.getInt(0),
                    CourseProfessor(
                        Professor(
                            User(
                                cursor.getInt(11),
                                cursor.getString(12),
                                cursor.getString(13),
                                cursor.getString(14),
                                cursor.getString(15),
                                cursor.getString(16),
                                cursor.getString(17),
                                cursor.getString(18),
                                cursor.getString(19)
                            ),
                            cursor.getInt(2),
                            cursor.getInt(4)
                        ),
                        Course(
                            cursor.getInt(20),
                            cursor.getString(21),
                            cursor.getInt(22),
                            cursor.getInt(23),
                            cursor.getInt(24),
                            cursor.getString(25),
                            cursor.getString(26)
                        )
                    ),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getString(9),
                    cursor.getInt(10)
                )
            )
            cursor.moveToNext()
        }
        cursor.close()
        return records
    }

    fun getList(studentID: Int): List<Records>{
        val records = mutableListOf<Records>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT RECORDS.*, USER.*, COURSE.* FROM RECORDS WHERE STUDENT_ID = $studentID",
                null)
        cursor.moveToFirst()
//        while (!cursor.isAfterLast){
//            records.add(
//                Records(
//
//                )
//            )
//            cursor.moveToNext()
//        }
        cursor.close()
        return records
    }

    fun insert(records: Records?){
        records ?: return
        val contentValues = ContentValues().apply {
            put("STUDENT_ID", records.studentID)
            put("COURSE_ID", records.courseProfessor.course.id)
            put("DEPT_ID", records.courseProfessor.course.departmentID)
            put("PROF_ID", records.courseProfessor.professor.user.id)
            put("COLLEGE_ID", records.courseProfessor.course.collegeID)
            put("TRANSACT_ID", records.transactionID)
            put("EXT_MARK", records.externalMarks)
            put("ATTENDANCE", records.attendance)
            put("ASSIGNMENT", records.assignmentMarks)
            put("STATUS", records.status)
            put("SEM_COMPLETED", records.semCompleted)

        }
        databaseHelper.writableDatabase.insert(tableName, null, contentValues)
    }

    fun delete(studentID: Int?, courseID: Int?, departmentID: Int?){
        studentID ?: return
        courseID ?: return
        departmentID ?: return

        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try{
            db.delete(tableName, "$studentKey= ? AND $courseKey = ? AND $departmentKey = ?", arrayOf(studentID.toString(), courseID.toString(), departmentID.toString()))
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
}