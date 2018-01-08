package xin.monus.checkit.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.*
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.*
import xin.monus.checkit.data.source.DailyDataSource
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.network.API
import xin.monus.checkit.network.api.NetWorkApi
import xin.monus.checkit.util.Injection
import java.io.IOException
import java.util.concurrent.TimeUnit


class TestActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

//        dbTest()

//        networkTest()
        test()
    }

    private val url = "http://101.132.99.105/CMS/api/index.php/login"

    private fun test() {
        API.checkLogin("test", "hello", object : NetWorkApi.UserCallback {
            override fun success(user: User) {
                println("login success")
            }

            override fun fail(msg: String) {
                println("login fail")
            }

        })
    }

    private fun networkTest() {
        val mOkHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()
        val body = FormBody.Builder()
                .add("username", "test")
                .add("password", "hello")
                .build()
        val request = Request.Builder().url(url).post(body).build()
        val call = mOkHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                println("fuck")
            }

            override fun onResponse(call: Call?, response: Response) {
                if (response.isSuccessful) {
                    println("success")
                    val jsonString = response.body()!!.string()
                    // 解析器
                    val parser = JsonParser()
                    // 获得根节点元素
                    val element = parser.parse(jsonString)
                    // 判断是什么类型的Gson节点对象
                    val root = element.asJsonObject
                    // 基本数据类型
                    val msgJson = root.getAsJsonPrimitive("msg")
                    val msg = msgJson.asString
                    println("msg: $msg")
                    // Object类型
                    val dataJson = root.getAsJsonObject("data")
                    val user = Gson().fromJson(dataJson, User::class.java)
                    println(user.nickname)

                } else {
                    println("fail")
                }
            }

        })
    }


    private fun dbTest() {
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