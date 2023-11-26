package com.example.finalproject

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "WhoOwesMeDatabase.db"
        private val TABLE_TRANSACTIONS = "TransactionTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_AMOUNT = "amount"
        private val KEY_BORROWED = "borrowed"
        private val KEY_RETURNED = "returned"
        private val KEY_ARCHIVED = "archived"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TRANSACTIONS_TABLE = ("CREATE TABLE $TABLE_TRANSACTIONS (" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "$KEY_NAME TEXT, " +
                "$KEY_AMOUNT TEXT, " +
                "$KEY_BORROWED DATE, " +
                "$KEY_RETURNED DATE, " +
                "$KEY_ARCHIVED INTEGER DEFAULT 0)")
        db?.execSQL(CREATE_TRANSACTIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        onCreate(db)
    }

    fun addTransaction(data: UserData): Long{
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, data.userName)
            put(KEY_AMOUNT, data.userMb)
            put(KEY_BORROWED, data.dateBorrowed)
            put(KEY_RETURNED, data.datePayment)
        }
        //insert these values papunta sa database
        val success = db.insert(TABLE_TRANSACTIONS, null, values)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun viewNonArchivedTransaction(): ArrayList<UserData> {
        val unarchivedList = ArrayList<UserData>()
        val selectQuery = "SELECT * FROM $TABLE_TRANSACTIONS WHERE $KEY_ARCHIVED = 0"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val userId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val userName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val userMb = cursor.getString(cursor.getColumnIndex(KEY_AMOUNT))
                val dateBorrowed = cursor.getString(cursor.getColumnIndex(KEY_BORROWED))
                val datePayment = cursor.getString(cursor.getColumnIndex(KEY_RETURNED))

                val userData = UserData(userId, userName, userMb, dateBorrowed, datePayment)
                unarchivedList.add(userData)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return unarchivedList
    }

    @SuppressLint("Range")
    fun viewArchivedTransaction(): ArrayList<UserData> {
        val userList = ArrayList<UserData>()
        val selectQuery = "SELECT * FROM $TABLE_TRANSACTIONS WHERE $KEY_ARCHIVED = 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val userId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val userName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val userMb = cursor.getString(cursor.getColumnIndex(KEY_AMOUNT))
                val dateBorrowed = cursor.getString(cursor.getColumnIndex(KEY_BORROWED))
                val datePayment = cursor.getString(cursor.getColumnIndex(KEY_RETURNED))

                val userData = UserData(userId, userName, userMb, dateBorrowed, datePayment)
                userList.add(userData)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return userList
    }

    fun updateTransaction(data: UserData): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, data.userName)
            put(KEY_AMOUNT, data.userMb)
            put(KEY_BORROWED, data.dateBorrowed)
            put(KEY_RETURNED, data.datePayment)
        }
        val success = db.update(TABLE_TRANSACTIONS, values, "$KEY_ID=?", arrayOf(data.userId.toString()))
        db.close()
        return success
    }

    fun deleteTransaction(userId: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_TRANSACTIONS, "$KEY_ID=?", arrayOf(userId.toString()))
        db.close()
        return success
    }

    fun archiveTransaction(userId: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_ARCHIVED, 1)
        }
        val success = db.update(TABLE_TRANSACTIONS, values, "$KEY_ID=?", arrayOf(userId.toString()))
        db.close()
        return success
    }

}

//    @SuppressLint("Range")
//    fun viewTransaction(): ArrayList<UserData> {
//        val userList = ArrayList<UserData>()
//        val selectQuery = "SELECT * FROM $TABLE_TRANSACTIONS"
//        val db = this.readableDatabase
//        val cursor = db.rawQuery(selectQuery, null)
//
//        if (cursor.moveToFirst()) {
//            do {
//                val userId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
//                val userName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
//                val userMb = cursor.getString(cursor.getColumnIndex(KEY_AMOUNT))
//                val dateBorrowed = cursor.getString(cursor.getColumnIndex(KEY_BORROWED))
//                val datePayment = cursor.getString(cursor.getColumnIndex(KEY_RETURNED))
//
//                val userData = UserData(userId, userName, userMb, dateBorrowed, datePayment)
//                userList.add(userData)
//            } while (cursor.moveToNext())
//        }
//
//        cursor.close()
//        return userList
//    }