package xin.monus.checkit.test

import android.content.Context
import xin.monus.checkit.db.LocalDbHelper


object FakeData {

    @JvmStatic fun generateData(context : Context) {
        if (LocalDbHelper.deleteDatabase(context)) {
            println("delete database success")
        } else {
            println("no database to be deleted")
        }

        val dbHelper = LocalDbHelper(context)
        with(dbHelper.writableDatabase) {
            execSQL(LocalDbHelper.OPEN_FOREIGN_KEYS)

            // USER
            execSQL("INSERT INTO USER(username, password, nickname)" +
                    "    VALUES ('test1', 'hello', 'myth');")
            execSQL("INSERT INTO USER(username, password, nickname)" +
                    "    VALUES ('test2', 'hello', 'myth');")
            execSQL("INSERT INTO USER(username, password, nickname)" +
                    "    VALUES ('test3', 'hello', 'myth');")
            // INBOX_ITEM
            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
                    "  VALUES ('test1', 'complete homework', DATETIME('2017-12-08 17:00'), 0 );")
            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
                    "  VALUES ('test1', 'complete homework', DATETIME('2017-12-08 17:00'), 0 );")
            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
                    "  VALUES ('test1', 'complete homework', DATETIME('2017-12-08 17:00'), 1 );")

            // PROJECT
            execSQL("INSERT INTO PROJECT (username, content, deadline, flag)" +
                    "  VALUES ('test1', 'android project', DATETIME('2017-12-08 17:00'), 1 );")
            execSQL("INSERT INTO PROJECT (username, content, deadline, flag)" +
                    "  VALUES ('test1', 'proposal', DATETIME('2017-12-08 17:00'), 2 );")

            //  ACTION (parent)
            execSQL("INSERT INTO ACTION (project_id, content, deadline)" +
                    "  VALUES ('1', 'complete homework', DATETIME('2017-12-08 17:00'));")
            execSQL("INSERT INTO ACTION (project_id, content, deadline)" +
                    "  VALUES ('1', 'complete homework agian', DATETIME('2017-12-08 17:00'));")

            // ACTION (son)
            execSQL("INSERT INTO ACTION(project_id, content, deadline, parent_action_id)" +
                    "    VALUES ('1', 'complete homework', DATETIME('2017-12-08 17:00'), '1');")
            execSQL("INSERT INTO ACTION(project_id, content, deadline, parent_action_id)" +
                    "    VALUES ('1', 'complete homework', DATETIME('2017-12-08 17:00'), '1');")
            execSQL("INSERT INTO ACTION(project_id, content, deadline, parent_action_id)" +
                    "    VALUES ('1', 'complete homework', DATETIME('2017-12-08 17:00'), '1');")

            //DAILY
            execSQL("INSERT INTO DAILY (username, content, remind_time)" +
                    "    VALUES ('test1', 'learn english', TIME('21:30', 'localtime'));")
            execSQL("INSERT INTO DAILY (username, content, remind_time)" +
                    "    VALUES ('test1', 'running', TIME('21:30', 'localtime'));")
            execSQL("INSERT INTO DAILY (username, content, remind_time)" +
                    "    VALUES ('test1', 'review', TIME('21:30', 'localtime'));")

            close()
        }
        dbHelper.close()
    }

}