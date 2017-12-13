package xin.monus.checkit.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.*
import xin.monus.checkit.data.source.DailyDataSource
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

//        val dbHelper = LocalDbHelper(this)
//        with(dbHelper.writableDatabase) {
//            execSQL(LocalDbHelper.OPEN_FOREIGN_KEYS)
//            execSQL("INSERT INTO USER(username, password, nickname)" +
//                    "    VALUES ('test', 'hello', 'myth');")
//            execSQL("INSERT INTO USER(username, password, nickname)" +
//                    "    VALUES ('test1', 'hello', 'myth');")
//            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
//                    "  VALUES ('test1', 'complete homework', DATETIME('2017-12-08 17:00'), 1 );")
//            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
//                    "  VALUES ('test1', 'homework', DATETIME('2017-12-08 17:00'), 1 );")
//            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
//                    "  VALUES ('test1', 'review', DATETIME('2017-12-08 17:00'), 1 );")
////            execSQL("DELETE FROM USER WHERE username = 'test1';")
//        }
        FakeData.generateData(this)

        val item = InboxItem(username = "test",
                content = "smart ass",
                deadline = "2017-12-23 18:00",
                complete = false,
                flag = true
                )

        val action = Action(
                projectId = 1,
                content = "smart ass",
                deadline = "2017-12-23 18:00",
                complete = false,
                flag = true,
                subActionList = ArrayList(0)
        )

        val project = Project(
                username = "test",
                content = "ass",
                type = ProjectType.PARALLEL,
                deadline = "2017-12-23 18:00",
                complete = false,
                flag = true,
                actionList = ArrayList(0)
        )

        val dailyItem = Daily(
                username = "test",
                content = "daily test",
                remindTime = "18:00",
                complete = false,
                flag = true
        )

//        val ldb = InboxItemLocalDataSource.getInstance(this)
//        val ldb = Injection.getInboxItemRepository(this)
//        val ldb = Injection.getProjectsRepository(this)
        val ldb = Injection.getDailyRepository(this)
//
//        ldb.getDailyItemById(4, object : DailyDataSource.GetDailyItemCallback {
//            override fun onDailyItemLoaded(item: Daily) {
//                println("get it: ${item.content}")
//            }
//
//            override fun onDataNotAvailable() {
//                println("ass")
//            }
//        })
        ldb.addDailyItem(dailyItem, object : DailyDataSource.OperationCallback {
            override fun success() {
                println("add success")
            }

            override fun fail() {
                println("add failed")
            }
        })

        ldb.deleteDailyItem(2, object : DailyDataSource.OperationCallback {
            override fun success() {
                println("delete success")
            }

            override fun fail() {
                println("delete failed")
            }
        })


        ldb.getDailyItemById(1, object : DailyDataSource.GetDailyItemCallback {
            override fun onDailyItemLoaded(item: Daily) {
                item.content = "update test?"
                ldb.updateDailyItem(item, object : DailyDataSource.OperationCallback {
                    override fun success() {
                        println("update success")
                    }

                    override fun fail() {
                        println("update failed")
                    }
                })
            }

            override fun onDataNotAvailable() {
                println("no such item")
            }
        })

        ldb.getDailyItems(object : DailyDataSource.LoadDailyItemsCallback {
            override fun onDailyItemsLoaded(items: List<Daily>) {
                for (i in items) {
                    println(i.content)
                }
            }

            override fun onDataNotAvailable() {
                println("fuck")
            }

        })






//        val user = UserProfile.getUser(this)
    }


}