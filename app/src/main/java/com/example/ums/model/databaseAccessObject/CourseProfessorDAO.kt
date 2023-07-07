package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.Course
import com.example.ums.model.CourseProfessor
import com.example.ums.model.Professor
import com.example.ums.model.User

class CourseProfessorDAO(private val databaseHelper: DatabaseHelper) {

    private val tableName = "COURSE_PROFESSOR_TABLE"
    private val courseTable = "COURSE"
    private val professorTable = "PROFESSOR"
    private val courseKey = "COURSE_ID"
    private val departmentKey = "DEPT_ID"
    private val collegeKey = "COLLEGE_ID"
    private val professorKey = "PROF_ID"
    private val userTable = "USER"

    private val userKey = "U_ID"

    fun get(professorID: Int?, courseID: Int?, departmentID: Int?, collegeID: Int?): CourseProfessor?{
        professorID ?: return null
        courseID ?: return null
        departmentID ?: return null
        collegeID ?: return null
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM $tableName " +
                    "INNER JOIN $courseTable ON " +
                    "($tableName.$courseKey = $courseTable.$courseKey AND " +
                    "$tableName.$departmentKey = $courseTable.$departmentKey AND " +
                    "$tableName.$collegeKey = $courseTable.$collegeKey) " +
                    "INNER JOIN $professorTable ON " +
                    "($tableName.$professorKey = $professorTable.$professorKey AND " +
                    "$tableName.$departmentKey = $professorTable.$departmentKey AND " +
                    "$tableName.$collegeKey = $professorTable.$collegeKey) " +
                    "INNER JOIN $userTable ON " +
                    "($userTable.$userKey = $tableName.$professorKey)" +
                    "WHERE $tableName.$courseKey = $courseID AND " +
                    "$tableName.$departmentKey = $departmentID AND " +
                    "$tableName.$collegeKey = $collegeID AND " +
                    "$tableName.$professorKey = $professorID", null)
        if(cursor!=null && cursor.moveToFirst()){
            return CourseProfessor(
                Professor(
                    User(
                        cursor.getInt(0),
                        cursor.getString(15),
                        cursor.getString(16),
                        cursor.getString(17),
                        cursor.getString(18),
                        cursor.getString(19),
                        cursor.getString(20),
                        cursor.getString(21),
                        cursor.getString(22)
                    ),
                    cursor.getInt(2),
                    cursor.getInt(3)
                ),
                Course(
                    cursor.getInt(1),
                    cursor.getString(5),
                    cursor.getInt(6),
                    cursor.getInt(7),
                    cursor.getInt(8),
                    cursor.getString(9),
                    cursor.getString(10)
                ))
        }
        cursor.close()
        return null
    }

    fun getList(courseID: Int?, departmentID : Int?, collegeID: Int?) : List<CourseProfessor>{
        if(courseID==null || departmentID==null || collegeID==null){
            return emptyList()
        }
        val courseProfessors = mutableListOf<CourseProfessor>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM $tableName " +
                "INNER JOIN $courseTable ON " +
                "($tableName.$courseKey = $courseTable.$courseKey AND " +
                "$tableName.$departmentKey = $courseTable.$departmentKey AND " +
                "$tableName.$collegeKey = $courseTable.$collegeKey) " +
                "INNER JOIN $professorTable ON " +
                "($tableName.$professorKey = $professorTable.$professorKey AND " +
                "$tableName.$departmentKey = $professorTable.$departmentKey AND " +
                "$tableName.$collegeKey = $professorTable.$collegeKey) " +
                "INNER JOIN $userTable ON " +
                "($userTable.$userKey = $tableName.$professorKey)" +
                "WHERE $tableName.$courseKey = $courseID AND " +
                    "$tableName.$departmentKey = $departmentID AND " +
                    "$tableName.$collegeKey = $collegeID", null)
        if(cursor!=null && cursor.moveToFirst()){
            while (!cursor.isAfterLast){

                courseProfessors.add(
                    CourseProfessor(
                        Professor(
                            User(
                                cursor.getInt(0),
                                cursor.getString(15),
                                cursor.getString(16),
                                cursor.getString(17),
                                cursor.getString(18),
                                cursor.getString(19),
                                cursor.getString(20),
                                cursor.getString(21),
                                cursor.getString(22)
                            ),
                            cursor.getInt(2),
                            cursor.getInt(3)
                        ),
                        Course(
                            cursor.getInt(1),
                            cursor.getString(5),
                            cursor.getInt(6),
                            cursor.getInt(7),
                            cursor.getInt(8),
                            cursor.getString(9),
                            cursor.getString(10)
                        )
                    )
                )
                cursor.moveToNext()
            }
            cursor.close()
        }
        return courseProfessors
    }

    fun getList(professorID: Int) : List<CourseProfessor>{
        val courseProfessors = mutableListOf<CourseProfessor>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM $tableName " +
                    "INNER JOIN $courseTable ON " +
                    "($tableName.$courseKey = $courseTable.$courseKey AND " +
                    "$tableName.$departmentKey = $courseTable.$departmentKey AND " +
                    "$tableName.$collegeKey = $courseTable.$collegeKey) " +
                    "INNER JOIN $professorTable ON " +
                    "($tableName.$professorKey = $professorTable.$professorKey AND " +
                    "$tableName.$departmentKey = $professorTable.$departmentKey AND " +
                    "$tableName.$collegeKey = $professorTable.$collegeKey) " +
                    "INNER JOIN $userTable ON " +
                    "($userTable.$userKey = $tableName.$professorKey)" +
                    "WHERE $tableName.$professorKey = $professorID", null)
        if(cursor!=null && cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                courseProfessors.add(
                    CourseProfessor(
                        Professor(
                            User(
                                cursor.getInt(0),
                                cursor.getString(15),
                                cursor.getString(16),
                                cursor.getString(17),
                                cursor.getString(18),
                                cursor.getString(19),
                                cursor.getString(20),
                                cursor.getString(21),
                                cursor.getString(22)
                            ),
                            cursor.getInt(2),
                            cursor.getInt(3)
                        ),
                        Course(
                            cursor.getInt(1),
                            cursor.getString(5),
                            cursor.getInt(6),
                            cursor.getInt(7),
                            cursor.getInt(8),
                            cursor.getString(9),
                            cursor.getString(10)
                        )
                    )
                )
                cursor.moveToNext()
            }
            cursor.close()
        }
        return courseProfessors
    }

    fun delete(professorID: Int?, courseID: Int?, departmentID: Int?, collegeID: Int?){

        professorID ?: return
        courseID ?: return
        departmentID ?: return
        collegeID ?: return
        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try {
            db.execSQL("DELETE FROM $tableName WHERE $professorKey = $professorID AND $courseKey = $courseID AND $departmentKey = $departmentID AND $collegeKey = $collegeID")
            db.execSQL("DELETE FROM RECORDS WHERE $professorKey = $professorID AND $courseKey = $courseID AND $departmentKey = $departmentID AND $collegeKey = $collegeID")
            db.setTransactionSuccessful()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.endTransaction()
        }
        db.close()
    }

//    fun delete(professorID: Int?, courseID: Int?, departmentID: Int?, collegeID: Int?, newProfessorID: Int?){
//
//        professorID ?: return
//        courseID ?: return
//        departmentID ?: return
//        collegeID ?: return
//        newProfessorID ?: return
//        val db = databaseHelper.writableDatabase
//        db.beginTransaction()
//        try {
//            db.execSQL("DELETE FROM $tableName WHERE $professorKey = $professorID AND $courseKey = $courseID AND $departmentKey = $departmentID AND $collegeKey = $collegeID")
//            db.execSQL("INSERT INTO COURSE_PROFESSOR_TABLE VALUES ($newProfessorID, $courseID, $departmentID, $collegeID)")
//            db.setTransactionSuccessful()
//        }catch (e: Exception){
//            e.printStackTrace()
//        }finally {
//            db.endTransaction()
//        }
//        db.close()
//    }

    fun insert(courseProfessor: CourseProfessor){
        val contentValues = ContentValues().apply {
            put(courseKey, courseProfessor.course.id)
            put(collegeKey, courseProfessor.course.collegeID)
            put(departmentKey, courseProfessor.course.departmentID)
            put(professorKey, courseProfessor.professor.user.id)
        }
        databaseHelper.writableDatabase.insert(tableName, null, contentValues)
    }
}