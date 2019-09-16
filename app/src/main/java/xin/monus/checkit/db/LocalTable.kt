package xin.monus.checkit.db

import android.provider.BaseColumns

/**
 * Tables in local database
 */

object LocalTable {

    /* Inner classes define the table contents */

    object UserTable : BaseColumns {
        const val TABLE_NAME = "USER"
        const val COLUMN_USERNAME = "username"       // primary key
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_NICKNAME = "nickname"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_CALORIE = "daily_calorie"
        const val COLUMN_STATUS = "status"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    object InboxItemTable : BaseColumns {
        const val TABLE_NAME = "INBOX_ITEM"
        const val COLUMN_ID = "id"                  // primary key autoincrement
        const val COLUMN_USERNAME = "username"       // foreign key on delete cascade on update cascade
        const val COLUMN_CONTENT = "content"
        const val COLUMN_DEADLINE = "deadline"
        const val COLUMN_COMPLETE = "complete"
        const val COLUMN_FLAG = "flag"
        const val COLUMN_STATUS = "status"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    object ProjectTable : BaseColumns {
        const val TABLE_NAME = "PROJECT"
        const val COLUMN_ID = "id"                // primary key autoincrement
        const val COLUMN_USERNAME = "username"    // foreign key on delete cascade on update cascade
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TYPE = "type"
        const val COLUMN_DEADLINE = "deadline"
        const val COLUMN_COMPLETE = "complete"
        const val COLUMN_FLAG = "flag"
        const val COLUMN_STATUS = "status"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    object ActionTable : BaseColumns {
        const val TABLE_NAME = "ACTION"
        const val COLUMN_ID = "id"
        const val COLUMN_PROJECT_ID = "project_id"
        const val COLUMN_PARENT_ACTION_ID = "parent_action_id"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_DEADLINE = "deadline"
        const val COLUMN_COMPLETE = "complete"
        const val COLUMN_FLAG = "flag"
        const val COLUMN_STATUS = "status"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    object DailyTable : BaseColumns {
        const val TABLE_NAME = "DAILY"
        const val COLUMN_ID = "id"                  // primary key autoincrement
        const val COLUMN_USERNAME = "username"       // foreign key on delete cascade on update cascade
        const val COLUMN_CONTENT = "content"
        const val COLUMN_REMIND_TIME = "remind_time"
        const val COLUMN_COMPLETE = "complete"
        const val COLUMN_FLAG = "flag"
        const val COLUMN_STATUS = "status"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

}