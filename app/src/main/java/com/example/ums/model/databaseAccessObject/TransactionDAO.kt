package com.example.ums.model.databaseAccessObject

import android.content.ContentValues
import com.example.ums.DatabaseHelper
import com.example.ums.model.Transactions

class TransactionDAO(private val databaseHelper: DatabaseHelper) {

    private val tableName = "TRANSACTIONS"
    private val primaryKey = "T_ID"
    private val studentKey = "STUDENT_ID"

    fun get(transactionID: Int?): Transactions?{
        transactionID ?: return null
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM $tableName WHERE $primaryKey = $transactionID",
                null)
        if(cursor!=null && cursor.moveToFirst()){
            return Transactions(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getString(3),
                cursor.getInt(4),
            )
        }
        cursor.close()
        return null
    }

    fun getList(studentID: Int): List<Transactions>{
        val transactions = mutableListOf<Transactions>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT * FROM $tableName WHERE $studentKey = $studentID",
                null)

        if(cursor!=null && cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                transactions.add(
                    Transactions(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return transactions
    }

    fun getCurrentSemesterTransactionList(studentID: Int) : List<Transactions>{
        val transactions = mutableListOf<Transactions>()
        val cursor = databaseHelper.readableDatabase
            .rawQuery("SELECT TRANSACTIONS.* FROM TRANSACTIONS INNER JOIN " +
                    "STUDENT ON (TRANSACTIONS.STUDENT_ID = STUDENT.STUDENT_ID) WHERE " +
                    "TRANSACTIONS.STUDENT_ID = $studentID AND TRANSACTIONS.T_SEM = STUDENT.S_SEM",
                null)
        if(cursor!=null && cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                transactions.add(
                    Transactions(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return transactions
    }

//    fun hasPaidForCurrentSemester(studentID: Int?): Boolean {
//        studentID ?: return false
//        val cursor = databaseHelper.readableDatabase
//            .rawQuery("SELECT COUNT(*) FROM $tableName INNER JOIN STUDENT ON " +
//                    "($tableName.STUDENT_ID = STUDENT.STUDENT_ID) WHERE " +
//                    "STUDENT.S_SEM = $tableName.T_SEM", null)
//        cursor.moveToFirst()
//
//        return cursor.getInt(0) > 0
//    }

    fun getNewID(): Int{
        val cursor = databaseHelper.readableDatabase.rawQuery("SELECT COALESCE(MAX($primaryKey), 0) + 1 FROM $tableName"
            , null)

        cursor.moveToFirst()
        val newID = cursor.getInt(0)
        cursor.close()
        return newID
    }

    fun delete(transactionID: Int?){
        transactionID ?: return

        val db = databaseHelper.writableDatabase
        db.beginTransaction()
        try{
            db.delete(tableName, "$primaryKey = ?", arrayOf(transactionID.toString()))
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

    fun insert(transactions: Transactions){
        val contentValues = ContentValues().apply {
            put("T_ID", transactions.id)
            put("STUDENT_ID", transactions.studentID)
            put("T_SEM", transactions.semester)
            put("T_DATE", transactions.date)
            put("T_AMOUNT", transactions.amount)
        }
        databaseHelper.writableDatabase.insert(tableName, null, contentValues)
    }

}