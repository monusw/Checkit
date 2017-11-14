package xin.monus.checkit.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.util.Injection


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
        with(dbHelper.writableDatabase) {
            execSQL(LocalDbHelper.OPEN_FOREIGN_KEYS)
            execSQL("INSERT INTO USER(username, password, nickname)" +
                    "    VALUES ('test', 'hello', 'myth');")
            execSQL("INSERT INTO USER(username, password, nickname)" +
                    "    VALUES ('test1', 'hello', 'myth');")
            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
                    "  VALUES ('test1', 'complete homework', DATETIME('2017-12-08 17:00'), 1 );")
            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
                    "  VALUES ('test1', 'homework', DATETIME('2017-12-08 17:00'), 1 );")
            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
                    "  VALUES ('test1', 'review', DATETIME('2017-12-08 17:00'), 1 );")
//            execSQL("DELETE FROM USER WHERE username = 'test1';")
        }

        val item = InboxItem(username = "test",
                content = "smart ass",
                deadline = "2017-12-23 18:00",
                complete = false,
                flag = true
                )

//        val ldb = InboxItemLocalDataSource.getInstance(this)
        val ldb = Injection.getInboxItemRepository(this)

        ldb.getInboxItems(object : InboxItemDataSource.LoadInboxItemsCallback {
            override fun onInboxItemsLoaded(items: List<InboxItem>) {
                for (i in items) {
                    println(i.content)
                }
            }

            override fun onDataNotAvailable() {
                println("fuck")
            }
        })

        ldb.addInboxItem(item, object : InboxItemDataSource.OperationCallback {
            override fun success() {
                println("add success")
            }
            override fun fail() {
                println("add failed")
            }
        })

        ldb.deleteInboxItem(1, object : InboxItemDataSource.OperationCallback {
            override fun success() {
                println("delete success")
            }

            override fun fail() {
                println("delete failed")
            }
        })

        ldb.updateInboxItem(InboxItem(2, "test1", "stick your finger", "2018-1-1 00:00", true, true),
                object : InboxItemDataSource.OperationCallback {
                    override fun success() {
                        println("update success")
                    }

                    override fun fail() {
                        println("update failed")
                    }
                })

        ldb.getInboxItems(object : InboxItemDataSource.LoadInboxItemsCallback {
            override fun onInboxItemsLoaded(items: List<InboxItem>) {
                for (i in items) {
                    println(i.content)
                }
            }

            override fun onDataNotAvailable() {
                println("fuck")
            }
        })



    }


}