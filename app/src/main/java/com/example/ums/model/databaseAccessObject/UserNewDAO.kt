package com.example.ums.model.databaseAccessObject

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ums.model.User

class UserNewDAO(context : Activity) : SQLiteOpenHelper(context, "ums.db",null,1) {

    private val createTableUser = "CREATE TABLE IF NOT EXISTS \"USER\" (\n" +
            "\t\"U_ID\"\tTEXT,\n" +
            "\t\"U_NAME\"\tTEXT,\n" +
            "\t\"U_CONTACT\"\tTEXT,\n" +
            "\t\"U_DOB\"\tDATE,\n" +
            "\t\"U_GENDER\"\tTEXT,\n" +
            "\t\"U_ADDRESS\"\tTEXT,\n" +
            "\t\"U_PASSWORD\"\tTEXT,\n" +
            "\t\"U_ROLE\"\tTEXT,\n" +
            "\t\"U_EMAIL_ID\"\tTEXT,\n" +
            "\tPRIMARY KEY(\"U_ID\")\n" +
            ")"

    private val createStarterSuperAdmin = "INSERT OR IGNORE INTO USER VALUES (\"23-SA-1\",\"AAA\",\"9090909090\",\"2001-01-01\",\"M\",\"CHENNAI\",\"EASY\",\"SUPER_ADMIN\",\"aaa@email.com\")"

    override fun onCreate(database: SQLiteDatabase) {

        database.execSQL(createTableUser)

//        if(this.starterSuperAdminUser != null){
        database.execSQL(createStarterSuperAdmin)
//        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }


    fun getUser(userEmailID : String, password : String) : User? {

        var user : User? = null

        val cursor = readableDatabase.rawQuery("SELECT * FROM USER WHERE U_EMAIL_ID = \"$userEmailID\" AND U_PASSWORD = \"$password\"", null)

        if(cursor.moveToFirst()){

            user = User(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8)
            )

            print(cursor.getString(1))
        }

        cursor.close()

        return user
    }
}