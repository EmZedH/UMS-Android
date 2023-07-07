package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.Professor
import com.example.ums.model.User

class ProfessorDAO (private val databaseHelper: DatabaseHelper) {

    private val tableName = "PROFESSOR"
    private val primaryKey = "PROF_ID"
    private val userTable = "USER"
    private val userPrimaryKey = "U_ID"
    private val departmentKey = "DEPT_ID"
    private val collegeKey = "COLLEGE_ID"

    private val recordsTable = "RECORDS"
    private val courseProfessorTable = "COURSE_PROFESSOR_TABLE"
    private val courseKey  = "COURSE_ID"
    private val userNameColumn = "U_NAME"
    private val userContactColumn = "U_CONTACT"
    private val userAddressColumn = "U_ADDRESS"
    private val userDateOfBirthColumn = "U_DOB"
    private val userGenderColumn = "U_GENDER"
    private val userPasswordColumn = "U_PASSWORD"
    private val userRoleColumn = "U_ROLE"
    private val userEmailColumn = "U_EMAIL_ID"

    fun get(id : Int?) : Professor?{
        id ?: return null
        var professor : Professor? = null
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName INNER JOIN $userTable ON ($userTable.$userPrimaryKey = $tableName.$primaryKey) WHERE $primaryKey = $id", null)
        if(cursor.moveToFirst()){
            professor = Professor(
                User(
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11)
                ),
                cursor.getInt(1),
                cursor.getInt(2)
            )

        }
        cursor.close()
        return professor
    }
    fun getList(departmentID: Int?, collegeID: Int?) : List<Professor>{
        departmentID ?: return emptyList()
        collegeID ?: return emptyList()
        val collegeAdmins = mutableListOf<Professor>()
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT * FROM $tableName INNER JOIN $userTable ON ($userTable.$userPrimaryKey = $tableName.$primaryKey) WHERE ($collegeKey = $collegeID AND $departmentKey = $departmentID)", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            collegeAdmins.add(
                Professor(
                    User(
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11)
                    ),
                    cursor.getInt(1),
                    cursor.getInt(2)
                )
            )
            cursor.moveToNext()
        }
        cursor.close()
        return collegeAdmins
    }

    fun insert(professor : Professor){
        val user = professor.user
        val contentValuesProfessor = ContentValues().apply {
            put(primaryKey, user.id)
            put(departmentKey, professor.departmentID)
            put(collegeKey,professor.collegeID)
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

    fun getNewProfessors(courseID: Int, departmentID: Int, collegeID: Int): List<Professor>{
        val professors = mutableListOf<Professor>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM $tableName INNER JOIN $userTable ON " +
                    "($userTable.$userPrimaryKey = $tableName.$primaryKey) WHERE " +
                    "$primaryKey NOT IN " +
                    "(SELECT $primaryKey FROM $courseProfessorTable WHERE " +
                    "$courseKey = $courseID AND " +
                    "$departmentKey = $departmentID AND " +
                    "$collegeKey = $collegeID) " +
                    "AND $tableName.$departmentKey = $departmentID AND $tableName.$collegeKey = $collegeID",
                null)

        if(cursor!=null && cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                professors.add(
                    Professor(
                        User(
                            cursor.getInt(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cursor.getString(10),
                            cursor.getString(11)
                        ),
                        cursor.getInt(1),
                        cursor.getInt(2)
                    )
                )
            cursor.moveToNext()
            }
        }
        cursor.close()
        return professors
    }

//    fun getCourseProfessors(courseID: Int, departmentID: Int, collegeID: Int): List<Professor>{
//
//        val professors = mutableListOf<Professor>()
//        val cursor = databaseHelper
//            .readableDatabase
//            .rawQuery("SELECT PROFESSOR.*, USER.* FROM PROFESSOR INNER JOIN USER ON " +
//                    "(PROFESSOR.PROF_ID = USER.U_ID) INNER JOIN COURSE_PROFESSOR_TABLE ON " +
//                    "(COURSE_PROFESSOR_TABLE.PROF_ID = PROFESSOR.PROF_ID) " +
//                    "WHERE COURSE_PROFESSOR_TABLE.COURSE_ID = ? AND " +
//                    "COURSE_PROFESSOR_TABLE.DEPT_ID = ? AND " +
//                    "COURSE_PROFESSOR_TABLE.COLLEGE_ID = ?",
//                arrayOf(
//                    courseID.toString(),
//                    departmentID.toString(),
//                    collegeID.toString()
//                )
//            )
//
//        if(cursor!=null && cursor.moveToFirst()){
//            while (!cursor.isAfterLast){
//                professors.add(
//                    Professor(
//                        User(
//                            cursor.getInt(3),
//                            cursor.getString(4),
//                            cursor.getString(5),
//                            cursor.getString(6),
//                            cursor.getString(7),
//                            cursor.getString(8),
//                            cursor.getString(9),
//                            cursor.getString(10),
//                            cursor.getString(11)
//                        ),
//                        cursor.getInt(1),
//                        cursor.getInt(2)
//                    )
//                )
//                cursor.moveToNext()
//            }
//        }
//        cursor.close()
//        return professors
//    }

    fun delete(userID: Int){
        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try{
            db.delete(tableName, "$primaryKey=?", arrayOf(userID.toString()))
            db.delete(userTable, "$userPrimaryKey=?", arrayOf(userID.toString()))
            db.delete(courseProfessorTable, "$primaryKey=?", arrayOf(userID.toString()))
            db.delete(recordsTable, "$primaryKey=?", arrayOf(userID.toString()))
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

    fun update(professor: Professor?){

        if(professor!=null){
            val db = databaseHelper.writableDatabase
            val contentValues = ContentValues().apply{
                put(userNameColumn, professor.user.name)
                put(userContactColumn, professor.user.contactNumber)
                put(userAddressColumn, professor.user.address)
            }

            db.update(userTable,contentValues, "$userPrimaryKey=?", arrayOf(professor.user.id.toString()))
            db.close()
        }

    }
}