package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.Student
import com.example.ums.model.User

class StudentDAO(private val databaseHelper: DatabaseHelper) {

    private val tableName = "STUDENT"
    private val primaryKey = "STUDENT_ID"
    private val userTable = "USER"
    private val userPrimaryKey = "U_ID"
    private val departmentKey = "DEPT_ID"
    private val collegeKey = "COLLEGE_ID"

    private val semesterColumn = "S_SEM"
    private val degreeColumn = "S_DEGREE"

    private val userNameColumn = "U_NAME"
    private val userContactColumn = "U_CONTACT"
    private val userAddressColumn = "U_ADDRESS"
    private val userDateOfBirthColumn = "U_DOB"
    private val userGenderColumn = "U_GENDER"
    private val userPasswordColumn = "U_PASSWORD"
    private val userRoleColumn = "U_ROLE"
    private val userEmailColumn = "U_EMAIL_ID"

    fun get(id : Int?) : Student?{
        id ?: return null
        var student : Student? = null
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM $tableName INNER JOIN $userTable ON " +
                    "($userTable.$userPrimaryKey = $tableName.$primaryKey) " +
                    "WHERE $primaryKey = $id", null)
        if(cursor.moveToFirst()){
            student = Student(
                User(
                    cursor.getInt(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getString(13)
                ),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getInt(4)
            )

        }
        cursor.close()
        return student
    }
    fun getList(departmentID: Int, collegeID: Int) : List<Student>{
        val students = mutableListOf<Student>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM $tableName INNER JOIN $userTable ON " +
                    "($userTable.$userPrimaryKey = $tableName.$primaryKey) WHERE " +
                    "($collegeKey = $collegeID AND $departmentKey = $departmentID)", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            students.add(
                Student(
                    User(
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13)
                    ),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4)
                )
            )
            cursor.moveToNext()
        }
        cursor.close()
        return students
    }

    fun getNewCurrentStudentsList(professorID: Int, courseID: Int): List<Student>{
        val students = mutableListOf<Student>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT STUDENT.*, USER.*, COURSE.COURSE_SEM FROM STUDENT INNER JOIN USER ON " +
                    "(USER.U_ID = STUDENT.STUDENT_ID) INNER JOIN COURSE ON " +
                    "(COURSE.COURSE_ID = $courseID AND " +
                    "COURSE.DEPT_ID = STUDENT.DEPT_ID AND " +
                    "COURSE.COLLEGE_ID = STUDENT.COLLEGE_ID) WHERE STUDENT_ID NOT IN " +
                    "(SELECT STUDENT_ID FROM RECORDS WHERE PROF_ID = $professorID AND COURSE_ID = $courseID) AND " +
                    "STUDENT.S_SEM = COURSE.COURSE_SEM", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            students.add(
                Student(
                    User(
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13)
                    ),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4)
                )
            )
            cursor.moveToNext()
        }
        cursor.close()
        return students
    }

    fun insert(student : Student){
        val user = student.user
        val contentValuesProfessor = ContentValues().apply {
            put(primaryKey, user.id)
            put(departmentKey, student.departmentID)
            put(semesterColumn, student.semester)
            put(degreeColumn, student.degree)
            put(collegeKey,student.collegeID)
        }
        val contentValuesUser = ContentValues().apply {
            put(userPrimaryKey, user.id)
            put(userNameColumn,user.name)
            put(userContactColumn, user.contactNumber)
            put(userDateOfBirthColumn, user.dateOfBirth)
            put(userGenderColumn, user.gender)
            put(userAddressColumn, user.address)
            put(userPasswordColumn, user.password)
            put(userRoleColumn,user.role)
            put(userEmailColumn, user.emailID)
        }
        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try{
            db.insert(tableName,null, contentValuesProfessor)
            db.insert(userTable, null, contentValuesUser)
            db.setTransactionSuccessful()
        }
        finally {
            db.endTransaction()
        }
        db.close()
    }

    fun delete(userID: Int){
        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try{
            db.delete(tableName, "$primaryKey=?", arrayOf(userID.toString()))
            db.delete(userTable, "$userPrimaryKey=?", arrayOf(userID.toString()))
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

    fun getNewID(): Int{
        val cursor = databaseHelper.readableDatabase
            .rawQuery(
                "SELECT COALESCE(MAX($userPrimaryKey), 0) + 1 FROM $userTable;",
                null
            )
        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        return newID
    }

    fun getCourseCompletedStudentListFromClass(professorID: Int, courseID: Int): List<Student>{
        val students = mutableListOf<Student>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT STUDENT.* FROM STUDENT INNER JOIN RECORDS ON (STUDENT.STUDENT_ID = RECORDS.STUDENT_ID) WHERE RECORDS.STATUS = \"COMPLETED\" AND RECORDS.PROF_ID = $professorID AND RECORDS.COURSE_ID = $courseID AND RECORDS.DEPT_ID = STUDENT.DEPT_ID AND RECORDS.COLLEGE_ID = STUDENT.COLLEGE_ID",
                null)
        cursor.close()
        return students
    }

    fun update(student: Student?){

        if(student!=null){
            val db = databaseHelper.writableDatabase
            val contentValues = ContentValues().apply{
                put(userNameColumn, student.user.name)
                put(userContactColumn, student.user.contactNumber)
                put(userAddressColumn, student.user.address)
            }

            db.update(userTable,contentValues, "$userPrimaryKey=?", arrayOf(student.user.id.toString()))
            db.close()
        }

    }
}