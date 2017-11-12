package xin.monus.checkit.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xin.monus.checkit.R
import xin.monus.checkit.data.source.local.LocalDbHelper

/**
 * @author wu
 * @date   2017/11/12
 */
class TestActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        if (LocalDbHelper.deleteDatabase(this)) {
            println("delete database success")
        } else {
            println("no database to be deleted")
        }

        val dbHelper = LocalDbHelper(this)
        with(dbHelper.readableDatabase) {
            execSQL(LocalDbHelper.OPEN_FOREIGN_KEYS)
            execSQL("INSERT INTO USER(username, password, nickname)" +
                    "    VALUES ('test', 'hello', 'myth');")
            execSQL("INSERT INTO USER(username, password, nickname)" +
                    "    VALUES ('test1', 'hello', 'myth');")
            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
                    "  VALUES ('test1', 'complete homework', DATETIME('2017-12-08 17:00'), 1 );")
            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
                    "  VALUES ('test1', 'complete homework', DATETIME('2017-12-08 17:00'), 1 );")
            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
                    "  VALUES ('test1', 'complete homework', DATETIME('2017-12-08 17:00'), 1 );")
            execSQL("DELETE FROM USER WHERE username = 'test1';")
        }
    }
}