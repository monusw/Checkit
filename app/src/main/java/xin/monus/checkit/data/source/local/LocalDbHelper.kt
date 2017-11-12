package xin.monus.checkit.data.source.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import xin.monus.checkit.data.source.local.LocalTable.ActionTable
import xin.monus.checkit.data.source.local.LocalTable.DailyTable
import xin.monus.checkit.data.source.local.LocalTable.InboxItemTable
import xin.monus.checkit.data.source.local.LocalTable.ProjectTable
import xin.monus.checkit.data.source.local.LocalTable.UserTable

class LocalDbHelper(context : Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        for (sql in SQL_ARRAY) {
            db.execSQL(sql)
        }
        println("create database success")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("not needed in version 1")
    }


    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "check_it.db"

        // Open foreign key constrain to make cascade available
        val OPEN_FOREIGN_KEYS = "PRAGMA FOREIGN_KEYS = ON;"
        // SQL to create tables
        val CREATE_TABLE_USER =
                "CREATE TABLE ${UserTable.TABLE_NAME} ("+
                        "${UserTable.COLUMN_USERNAME} VARCHAR(30) PRIMARY KEY NOT NULL ," +
                        "${UserTable.COLUMN_PASSWORD} VARCHAR(40) NOT NULL ," +
                        "${UserTable.COLUMN_NICKNAME} VARCHAR(20) NOT NULL ," +
                        "${UserTable.COLUMN_HEIGHT} DOUBLE NOT NULL DEFAULT 0.0,"+
                        "${UserTable.COLUMN_WEIGHT} DOUBLE NOT NULL DEFAULT 0.0,"+
                        "${UserTable.COLUMN_CALORIE} DOUBLE NOT NULL DEFAULT 0.0,"+
                        "${UserTable.COLUMN_STATUS} INTEGER NOT NULL DEFAULT 0,"+
                        "${UserTable.COLUMN_TIMESTAMP} INTEGER NOT NULL DEFAULT (STRFTIME('%s','now', 'utc'))"+
                        ");"
        val CREATE_TABLE_INBOX_ITEM =
                "CREATE TABLE ${InboxItemTable.TABLE_NAME} (" +
                        "${InboxItemTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        "${InboxItemTable.COLUMN_USERNAME} VARCHAR(30),"+
                        "${InboxItemTable.COLUMN_CONTENT} TEXT,"+
                        "${InboxItemTable.COLUMN_DEADLINE} DATETIME,"+
                        "${InboxItemTable.COLUMN_COMPLETE} BOOLEAN NOT NULL DEFAULT 0,"+
                        "${InboxItemTable.COLUMN_FLAG} BOOLEAN NOT NULL DEFAULT 0,"+
                        "${InboxItemTable.COLUMN_STATUS} INTEGER NOT NULL DEFAULT 0,"+
                        "${InboxItemTable.COLUMN_TIMESTAMP} INTEGER NOT NULL DEFAULT (STRFTIME('%s','now', 'utc')),"+
                        "FOREIGN KEY (${InboxItemTable.COLUMN_USERNAME}) REFERENCES " +
                        "${UserTable.TABLE_NAME} (${UserTable.COLUMN_USERNAME}) " +
                        "ON DELETE CASCADE ON UPDATE CASCADE"+
                        ");"
        val CREATE_TABLE_PROJECT =
                "CREATE TABLE ${ProjectTable.TABLE_NAME} (" +
                        "${ProjectTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                        "${ProjectTable.COLUMN_USERNAME} VARCHAR(30),"+
                        "${ProjectTable.COLUMN_CONTENT} TEXT,"+
                        "${ProjectTable.COLUMN_TYPE} INTEGER NOT NULL DEFAULT 0,"+
                        "${ProjectTable.COLUMN_DEADLINE} DATETIME,"+
                        "${ProjectTable.COLUMN_COMPLETE} BOOLEAN NOT NULL DEFAULT 0,"+
                        "${ProjectTable.COLUMN_FLAG} BOOLEAN NOT NULL DEFAULT 0,"+
                        "${ProjectTable.COLUMN_STATUS} INTEGER NOT NULL DEFAULT 0,"+
                        "${ProjectTable.COLUMN_TIMESTAMP} INTEGER NOT NULL DEFAULT (STRFTIME('%s','now', 'utc')),"+
                        "FOREIGN KEY (${ProjectTable.COLUMN_USERNAME}) REFERENCES " +
                        "${UserTable.TABLE_NAME} (${UserTable.COLUMN_USERNAME}) " +
                        "ON DELETE CASCADE ON UPDATE CASCADE"+
                        ");"
        val CREATE_TABLE_ACTION =
                "CREATE TABLE ${ActionTable.TABLE_NAME} ("+
                        "${ActionTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                        "${ActionTable.COLUMN_PROJECT_ID} INTEGER ,"+
                        "${ActionTable.COLUMN_PARENT_ACTION_ID} DEFAULT NULL ,"+
                        "${ActionTable.COLUMN_CONTENT} TEXT,"+
                        "${ActionTable.COLUMN_DEADLINE} DATETIME ,"+
                        "${ActionTable.COLUMN_COMPLETE} BOOLEAN NOT NULL DEFAULT 0,"+
                        "${ActionTable.COLUMN_FLAG} BOOLEAN NOT NULL DEFAULT 0,"+
                        "${ActionTable.COLUMN_STATUS} INTEGER NOT NULL DEFAULT 0,"+
                        "${ActionTable.COLUMN_TIMESTAMP} INTEGER NOT NULL DEFAULT (STRFTIME('%s','now', 'utc')),"+
                        "FOREIGN KEY (${ActionTable.COLUMN_PROJECT_ID}) REFERENCES "+
                        "${ProjectTable.TABLE_NAME} (${ProjectTable.COLUMN_ID})" +
                        "ON DELETE CASCADE ON UPDATE CASCADE"+
                        ");"
        val CREATE_TRIGGER_ACTION_DELETE =
                "CREATE TRIGGER action_delete BEFORE DELETE " +
                        "ON ${ActionTable.TABLE_NAME} "+
                        "BEGIN " +
                        "DELETE FROM ${ActionTable.TABLE_NAME} " +
                        "WHERE ${ActionTable.COLUMN_PARENT_ACTION_ID} = old.${ActionTable.COLUMN_ID};"+
                        "END;"
        val CREATE_TABLE_DAILY =
                "CREATE TABLE ${DailyTable.TABLE_NAME} (" +
                        "${DailyTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        "${DailyTable.COLUMN_USERNAME} VARCHAR(30),"+
                        "${DailyTable.COLUMN_CONTENT} TEXT ,"+
                        "${DailyTable.COLUMN_REMIND_TIME} DATETIME,"+
                        "${DailyTable.COLUMN_COMPLETE} BOOLEAN NOT NULL DEFAULT 0,"+
                        "${DailyTable.COLUMN_FLAG} BOOLEAN NOT NULL DEFAULT 0,"+
                        "${DailyTable.COLUMN_STATUS} INTEGER NOT NULL DEFAULT 0,"+
                        "${DailyTable.COLUMN_TIMESTAMP} INTEGER NOT NULL DEFAULT (STRFTIME('%s','now', 'utc')),"+
                        "FOREIGN KEY (${DailyTable.COLUMN_USERNAME}) REFERENCES " +
                        "${UserTable.TABLE_NAME} (${UserTable.COLUMN_USERNAME}) " +
                        "ON DELETE CASCADE ON UPDATE CASCADE"+
                        ");"


        val SQL_ARRAY = arrayOf(
                CREATE_TABLE_USER,
                CREATE_TABLE_INBOX_ITEM,
                CREATE_TABLE_PROJECT,
                CREATE_TABLE_ACTION,
                CREATE_TRIGGER_ACTION_DELETE,
                CREATE_TABLE_DAILY,
                OPEN_FOREIGN_KEYS
        )

        /**
         * Delete database.
         */
        fun deleteDatabase(context: Context) : Boolean {
            return  context.deleteDatabase(DATABASE_NAME)
        }
    }
}