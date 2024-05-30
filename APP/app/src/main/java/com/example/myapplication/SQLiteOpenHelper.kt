package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class SQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)

        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Sample.db"



        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${DatabaseContract.Entry.TABLE_NAME} (" +
                    "${DatabaseContract.Entry.COLUMN_NAME_ID} INTEGER PRIMARY KEY," +
                    "${DatabaseContract.Entry.COLUMN_NAME_TITLE} TEXT)"+
                    "${DatabaseContract.Entry.COLUMN_NAME_DESCRIPTION} TEXT," +
                    "${DatabaseContract.Entry.COLUMN_NAME_TIMESTAMP} TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"

        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${DatabaseContract.Entry.TABLE_NAME}"
    }
    object DatabaseContract {
        object Entry : BaseColumns {

            const val TABLE_NAME = "entry"
            const val COLUMN_NAME_ID = "id"
            const val COLUMN_NAME_TITLE = "title"
            const val COLUMN_NAME_DESCRIPTION = "description"
            const val COLUMN_NAME_TIMESTAMP = "timestamp"

        }
    }
    data class Entry(val id: Long, val title: String)
}


