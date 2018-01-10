package xin.monus.checkit.login

import android.content.ContentValues
import android.content.Context
import xin.monus.checkit.data.entity.User
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.db.LocalTable.UserTable
import xin.monus.checkit.network.DataStatus

object UserProfile {
    private lateinit var INSTANCE: User
    private var firstLoad = true
    var isLogin = false

    fun getUser(context: Context): User {
        return if (!firstLoad) {
            INSTANCE
        } else {
            var user = getUserFromDatabase(context)
            if (user == null) {
                user = generateDefaultUser(context)
            }
            firstLoad = false
            INSTANCE = user
            user
        }
    }

    fun saveUser(user: User, context: Context): Boolean {
        isLogin = true
        val oldUser = getUserFromDatabase(context)
        if (oldUser != null) {
            println(user.username)
            println(oldUser.nickname)
            if (oldUser.username == user.username) {
                return true
            }
        } else {
            deleteUser(context)
        }
        val dbHelper = LocalDbHelper(context)
        val values = ContentValues().apply {
            put(UserTable.COLUMN_USERNAME, user.username)
            put(UserTable.COLUMN_PASSWORD, user.password)
            put(UserTable.COLUMN_NICKNAME, user.nickname)
            put(UserTable.COLUMN_HEIGHT, user.height)
            put(UserTable.COLUMN_WEIGHT, user.weight)
            put(UserTable.COLUMN_CALORIE, user.daily_calorie)
            put(UserTable.COLUMN_STATUS, DataStatus.SYNC)
        }
        with(dbHelper.writableDatabase) {
            return if (insert(UserTable.TABLE_NAME, null, values) != 0L) {
                close()
                true
            } else {
                close()
                false
            }
        }
    }

    fun deleteUser(context: Context) {
        val dbHelper = LocalDbHelper(context)
        with(dbHelper.writableDatabase) {
            //开启级联删除，删除一个用户会把跟他相关的所有表信息删除
            execSQL(LocalDbHelper.OPEN_FOREIGN_KEYS)
            delete(UserTable.TABLE_NAME, null, null)
        }
        firstLoad = true
    }

    fun update(user: User, context: Context): Boolean {
        val dbHelper = LocalDbHelper(context)
        val values = ContentValues().apply {
            put(UserTable.COLUMN_PASSWORD, user.password)
            put(UserTable.COLUMN_NICKNAME, user.nickname)
            put(UserTable.COLUMN_HEIGHT, user.height)
            put(UserTable.COLUMN_WEIGHT, user.weight)
            put(UserTable.COLUMN_CALORIE, user.daily_calorie)
            put(UserTable.COLUMN_STATUS, DataStatus.UPDATE)
        }

        with(dbHelper.writableDatabase) {
            val row = update(UserTable.TABLE_NAME, values,
                    "${UserTable.COLUMN_USERNAME} = ?",
                    arrayOf(user.username))
            close()
            return if (row > 0) {
                INSTANCE = getUserFromDatabase(context)!!
                true
            } else {
                false
            }
        }
    }

    private fun getUserFromDatabase(context: Context): User? {
        val dbHelper = LocalDbHelper(context)
        val db = dbHelper.readableDatabase

        val projection = arrayOf(UserTable.COLUMN_USERNAME, UserTable.COLUMN_PASSWORD,
                UserTable.COLUMN_NICKNAME, UserTable.COLUMN_WEIGHT,
                UserTable.COLUMN_HEIGHT, UserTable.COLUMN_CALORIE)

        val cursor = db.query(UserTable.TABLE_NAME, projection,
                null, null, null, null, null)
        var user: User? = null
        with(cursor) {
            if (moveToFirst()) {
                val username = getString(getColumnIndexOrThrow(UserTable.COLUMN_USERNAME))
                val password = getString(getColumnIndexOrThrow(UserTable.COLUMN_PASSWORD))
                val nickname = getString(getColumnIndexOrThrow(UserTable.COLUMN_NICKNAME))
                val height = getDouble(getColumnIndexOrThrow(UserTable.COLUMN_HEIGHT))
                val weight = getDouble(getColumnIndexOrThrow(UserTable.COLUMN_WEIGHT))
                val calorie = getDouble(getColumnIndexOrThrow(UserTable.COLUMN_CALORIE))
                user = User(username, password, nickname, height, weight, calorie)
            }
            close()
        }
        db.close()
        return user
    }

    fun generateDefaultUser(context: Context): User {
        isLogin = false
        val dbHelper = LocalDbHelper(context)
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserTable.COLUMN_USERNAME, "admin")
            put(UserTable.COLUMN_PASSWORD, "admin")
            put(UserTable.COLUMN_NICKNAME, "user")
            put(UserTable.COLUMN_HEIGHT, 0.0)
            put(UserTable.COLUMN_WEIGHT, 0.0)
            put(UserTable.COLUMN_CALORIE, 0.0)
        }
        with(db) {
            insert(UserTable.TABLE_NAME, null, values)
            close()
        }
        return User("admin", "admin", "user", 0.0, 0.0, 0.0)
    }


}