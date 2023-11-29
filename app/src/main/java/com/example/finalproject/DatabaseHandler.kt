package com.example.finalproject

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context

class DatabaseHandler(private val context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "WhoOwesMeDatabase.db"

        private val TABLE_ACCOUNTS = "AccountTable"
        private val KEY_FIRSTNAME = "firstname"
        private val KEY_LASTNAME = "lastname"
        private val KEY_EMAIL = "email"
        private val KEY_PASSWORD = "password"

        private val TABLE_TRANSACTIONS = "TransactionTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_AMOUNT = "amount"
        private val KEY_BORROWED = "borrowed"
        private val KEY_RETURNED = "returned"
        private val KEY_ARCHIVED = "archived"
        private val KEY_ACCOUNT_EMAIL_FK = "account_email_fk"

        private const val PREFS_NAME = "MyPrefs"
        private const val KEY_LOGGED_IN_USER_EMAIL = "loggedInUserEmail"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_ACCOUNT_TABLE = ("CREATE TABLE $TABLE_ACCOUNTS (" +
                "$KEY_FIRSTNAME TEXT, " +
                "$KEY_LASTNAME TEXT, " +
                "$KEY_EMAIL TEXT PRIMARY KEY , " +
                "$KEY_PASSWORD TEXT)")

        val CREATE_TRANSACTIONS_TABLE = ("CREATE TABLE $TABLE_TRANSACTIONS (" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "$KEY_NAME TEXT, " +
                "$KEY_AMOUNT TEXT, " +
                "$KEY_BORROWED DATE, " +
                "$KEY_RETURNED DATE, " +
                "$KEY_ARCHIVED INTEGER DEFAULT 0, " +
                "$KEY_ACCOUNT_EMAIL_FK TEXT, " +
                "FOREIGN KEY($KEY_ACCOUNT_EMAIL_FK) REFERENCES $TABLE_ACCOUNTS($KEY_EMAIL))")

        db?.execSQL(CREATE_ACCOUNT_TABLE)
        db?.execSQL(CREATE_TRANSACTIONS_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        onCreate(db)
    }

    // Add this method to set the currently logged-in user's email to SharedPreferences
    fun setLoggedInUserEmail(email: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_LOGGED_IN_USER_EMAIL, email)
        editor.apply()
    }

    // Add this method to get the currently logged-in user's email from SharedPreferences
    fun getLoggedInUserEmail(): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_LOGGED_IN_USER_EMAIL, null)
    }

    fun addTransaction(data: UserData): Long{
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, data.userName)
            put(KEY_AMOUNT, data.userMb)
            put(KEY_BORROWED, data.dateBorrowed)
            put(KEY_RETURNED, data.datePayment)

            val loggedInUserEmail = getLoggedInUserEmail()

            if (loggedInUserEmail != null) {
                put(KEY_ACCOUNT_EMAIL_FK, loggedInUserEmail)
            } else {
                throw IllegalStateException("No user logged in. Cannot add transaction.")
            }
        }
        //insert these values papunta sa database
        val success = db.insert(TABLE_TRANSACTIONS, null, values)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun viewNonArchivedTransaction(): ArrayList<UserData> {
        val unarchivedList = ArrayList<UserData>()
        val loggedInUserEmail = getLoggedInUserEmail()
        if (loggedInUserEmail != null) {
            val selectQuery ="SELECT * FROM $TABLE_TRANSACTIONS WHERE $KEY_ARCHIVED = 0 AND $KEY_ACCOUNT_EMAIL_FK = '$loggedInUserEmail'"
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
        } else {
            throw IllegalStateException("No user logged in. Cannot fetch transactions.")
        }

        return unarchivedList
    }

    @SuppressLint("Range")
    fun viewArchivedTransaction(): ArrayList<UserData> {
        val userList = ArrayList<UserData>()
        val loggedInUserEmail = getLoggedInUserEmail()
        if (loggedInUserEmail != null) {
        val selectQuery = "SELECT * FROM $TABLE_TRANSACTIONS WHERE $KEY_ARCHIVED = 1 AND $KEY_ACCOUNT_EMAIL_FK = '$loggedInUserEmail'"
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
        } else {
            throw IllegalStateException("No user logged in. Cannot fetch transactions.")
        }
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
    @SuppressLint("Range")
    fun addAccount(account: AccountData): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_FIRSTNAME, account.firstname)
            put(KEY_LASTNAME, account.lastname)
            put(KEY_EMAIL, account.email)
            put(KEY_PASSWORD, account.password)
        }
        val success = db.insert(TABLE_ACCOUNTS, null, values)
        db.close()
        return success
    }
    @SuppressLint("Range")
    fun getAccountByEmail(email: String): AccountData? {
        val db = this.readableDatabase

        // Check if the AccountTable exists, create it if it doesn't
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_ACCOUNTS (" +
                "$KEY_FIRSTNAME TEXT, " +
                "$KEY_LASTNAME TEXT, " +
                "$KEY_EMAIL TEXT PRIMARY KEY, " +
                "$KEY_PASSWORD TEXT)")

        // Perform the query
        val queryCursor = db.query(
            TABLE_ACCOUNTS,
            arrayOf(KEY_FIRSTNAME, KEY_LASTNAME, KEY_EMAIL, KEY_PASSWORD),
            "$KEY_EMAIL=?",
            arrayOf(email),
            null,
            null,
            null
        )

        if (queryCursor.moveToFirst()) {
            val firstname = queryCursor.getString(queryCursor.getColumnIndex(KEY_FIRSTNAME))
            val lastname = queryCursor.getString(queryCursor.getColumnIndex(KEY_LASTNAME))
            val storedEmail = queryCursor.getString(queryCursor.getColumnIndex(KEY_EMAIL))
            val storedPassword = queryCursor.getString(queryCursor.getColumnIndex(KEY_PASSWORD))

            return AccountData(firstname, lastname, storedEmail, storedPassword)
        }

        queryCursor.close()
        db.close()

        return null
    }
    @SuppressLint("Range")
    fun getAccountByEmailAndPassword(email: String, password: String): AccountData? {
        if (!isAccountTableExists()) {
            return null
        }

        val db = this.readableDatabase
        val queryCursor = db.query(
            TABLE_ACCOUNTS,
            arrayOf(KEY_FIRSTNAME, KEY_LASTNAME, KEY_EMAIL, KEY_PASSWORD),
            "$KEY_EMAIL=? AND $KEY_PASSWORD=?",
            arrayOf(email, password),
            null,
            null,
            null
        )

        if (queryCursor.moveToFirst()) {
            val firstname = queryCursor.getString(queryCursor.getColumnIndex(KEY_FIRSTNAME))
            val lastname = queryCursor.getString(queryCursor.getColumnIndex(KEY_LASTNAME))
            val storedEmail = queryCursor.getString(queryCursor.getColumnIndex(KEY_EMAIL))
            val storedPassword = queryCursor.getString(queryCursor.getColumnIndex(KEY_PASSWORD))

            return AccountData(firstname, lastname, storedEmail, storedPassword)
        }

        queryCursor.close()
        db.close()
        return null
    }

    fun isAccountTableExists(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='$TABLE_ACCOUNTS'", null)
        val tableExists = cursor.count > 0
        cursor.close()
        return tableExists
    }

}
