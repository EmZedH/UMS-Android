package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.Course

class CourseDAO(private val databaseHelper: DatabaseHelper) {
    private val tableName = "COURSE"
    private val courseKey = "COURSE_ID"
    private val departmentKey = "DEPT_ID"
    private val collegeKey = "COLLEGE_ID"
    private val courseNameColumn = "COURSE_NAME"
    private val courseSemColumn = "COURSE_SEM"
    private val degreeColumn = "DEGREE"
    private val electiveColumn = "ELECTIVE"

    private val testTable = "TEST"
    private val recordTable = "RECORDS"
    private val courseProfessorTable = "COURSE_PROFESSOR_TABLE"
    private val professorKey = "PROF_ID"

    fun get(courseID: Int?, departmentID : Int?, collegeID: Int?): Course?{
        val courseID = courseID ?: return null
        val departmentID = departmentID ?: return null
        val collegeID = collegeID ?: return null
        val cursor = databaseHelper.readableDatabase
            .query(
                tableName,
                arrayOf("*"),
                "$courseKey = ? AND $departmentKey = ? AND $collegeKey = ?",
                arrayOf(courseID.toString(), departmentID.toString(), collegeID.toString()),
                null,
                null,
                null
            )

        if(cursor != null && cursor.moveToFirst()){
            return Course(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getString(5),
                cursor.getString(6)
            )
        }
        cursor.close()
        return null
    }

    fun getList(departmentID : Int, collegeID: Int) : List<Course>{
        val courses = mutableListOf<Course>()
        val cursor = databaseHelper.readableDatabase
            .query(
                tableName,
                arrayOf("*"),
                "$departmentKey = ? AND $collegeKey = ?",
                arrayOf(departmentID.toString(), collegeID.toString()),
                null,
                null,
                null
            )
        if(cursor!=null && cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                courses.add(
                    Course(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getString(6)
                    )
                )
                cursor.moveToNext()
            }
            cursor.close()
        }
        return courses
    }

    fun getNewCourses(professorID: Int): List<Course>{
        val courses = mutableListOf<Course>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM $tableName WHERE ($courseKey, $departmentKey, $collegeKey) NOT IN (SELECT $courseKey, $departmentKey, $collegeKey FROM $courseProfessorTable WHERE $professorKey = $professorID)", null)

        if(cursor!=null && cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                courses.add(
                    Course(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getString(6)
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return courses
    }

    fun insert(course: Course){
        val contentValues = ContentValues().apply {
            put(courseKey, course.id)
            put(courseNameColumn, course.name)
            put(courseSemColumn, course.semester)
            put(departmentKey, course.departmentID)
            put(collegeKey, course.collegeID)
            put(degreeColumn, course.degree)
            put(electiveColumn, course.elective)
        }
        databaseHelper.writableDatabase.insert(tableName, null, contentValues)
    }

    fun delete(id: Int?, departmentID: Int?, collegeID: Int?){
        val id = id ?: return
        val departmentID = departmentID ?: return
        val collegeID = collegeID ?: return
        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try {
            db.execSQL("DELETE FROM $tableName WHERE $courseKey = $id AND $departmentKey = $departmentID AND $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $testTable WHERE $courseKey = $id AND $departmentKey = $departmentID AND $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $recordTable WHERE $courseKey = $id AND $departmentKey = $departmentID AND $collegeKey = $collegeID")
            db.execSQL("DELETE FROM $courseProfessorTable WHERE $courseKey = $id AND $departmentKey = $departmentID AND $collegeKey = $collegeID")
            db.setTransactionSuccessful()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.endTransaction()
        }
        db.close()
    }

    fun getNewID(departmentID: Int, collegeID: Int): Int{

        val cursor = databaseHelper.readableDatabase.rawQuery("WITH RECURSIVE number_range AS (\n" +
                "  SELECT 1 AS number\n" +
                "  UNION ALL\n" +
                "  SELECT number + 1\n" +
                "  FROM number_range\n" +
                "  WHERE number <= (SELECT MAX($courseKey) FROM $tableName WHERE $departmentKey = $departmentID AND $collegeKey = $collegeID) \n" +
                ")\n" +
                "SELECT number FROM number_range EXCEPT \n" +
                "SELECT $courseKey AS number FROM $tableName WHERE $departmentKey = $departmentID AND $collegeKey = $collegeID;"
            , null)


        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        return newID
    }

    fun update(course: Course){
        val db = databaseHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(courseNameColumn, course.name)
        }
        db.update(tableName, contentValues, "$courseKey = ? AND $departmentKey = ? AND $collegeKey = ?", arrayOf(course.id.toString(), course.departmentID.toString(), course.collegeID.toString()))
        db.close()
    }

}