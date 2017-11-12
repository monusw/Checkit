package xin.monus.checkit.data.source.local

import android.provider.BaseColumns

/**
 * Tables in local database
 */

object LocalTable {

    /* Inner classes define the table contents */

    object UserTable : BaseColumns {
        val TABLE_NAME = "USER"
        val COLUMN_USERNAME = "username"       // primary key
        val COLUMN_PASSWORD = "password"
        val COLUMN_NICKNAME = "nickname"
        val COLUMN_HEIGHT = "height"
        val COLUMN_WEIGHT = "weight"
        val COLUMN_CALORIE = "daily_calorie"
        val COLUMN_STATUS = "status"
        val COLUMN_TIMESTAMP = "timestamp"
    }

    object InboxItemTable : BaseColumns {
        val TABLE_NAME = "INBOX_ITEM"
        val COLUMN_ID = "id"                  // primary key autoincrement
        val COLUMN_USERNAME = "username"       // foreign key on delete cascade on update cascade
        val COLUMN_CONTENT = "content"
        val COLUMN_DEADLINE = "deadline"
        val COLUMN_COMPLETE = "complete"
        val COLUMN_FLAG = "flag"
        val COLUMN_STATUS = "status"
        val COLUMN_TIMESTAMP = "timestamp"
    }

    object ProjectTable : BaseColumns {
        val TABLE_NAME = "PROJECT"
        val COLUMN_ID = "id"                // primary key autoincrement
        val COLUMN_USERNAME = "username"    // foreign key on delete cascade on update cascade
        val COLUMN_CONTENT = "content"
        val COLUMN_TYPE = "type"
        val COLUMN_DEADLINE = "deadline"
        val COLUMN_COMPLETE = "complete"
        val COLUMN_FLAG = "flag"
        val COLUMN_STATUS = "status"
        val COLUMN_TIMESTAMP = "timestamp"
    }

    object ActionTable : BaseColumns {
        val TABLE_NAME = "ACTION"
        val COLUMN_ID = "id"
        val COLUMN_PROJECT_ID = "project_id"
        val COLUMN_PARENT_ACTION_ID = "parent_action_id"
        val COLUMN_CONTENT = "content"
        val COLUMN_DEADLINE = "deadline"
        val COLUMN_COMPLETE = "complete"
        val COLUMN_FLAG = "flag"
        val COLUMN_STATUS = "status"
        val COLUMN_TIMESTAMP = "timestamp"
    }

    object DailyTable : BaseColumns {
        val TABLE_NAME = "DAILY"
        val COLUMN_ID = "id"                  // primary key autoincrement
        val COLUMN_USERNAME = "username"       // foreign key on delete cascade on update cascade
        val COLUMN_CONTENT = "content"
        val COLUMN_REMIND_TIME = "remind_time"
        val COLUMN_COMPLETE = "complete"
        val COLUMN_FLAG = "flag"
        val COLUMN_STATUS = "status"
        val COLUMN_TIMESTAMP = "timestamp"
    }

}