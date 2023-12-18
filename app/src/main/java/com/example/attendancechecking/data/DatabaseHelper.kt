package com.example.attendancechecking.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "bank_attendance_new5.db"
        const val DATABASE_VERSION = 1
        const val CHECK_IN = "checkin"
        const val LOGIN = "login"
        // Add more constants for table names, column names, etc.
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables here
        db.execSQL("CREATE TABLE IF NOT EXISTS $CHECK_IN (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "age INTEGER, " +
                "gender TEXT, " +
                "role TEXT," +
                "access_time DATETIME," +
                "type TEXT," +
                "place TEXT" +
                ");")

        db.execSQL("CREATE TABLE IF NOT EXISTS $LOGIN (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "password TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
         db.execSQL("DROP TABLE IF EXISTS $CHECK_IN;")
         onCreate(db)
    }
}
