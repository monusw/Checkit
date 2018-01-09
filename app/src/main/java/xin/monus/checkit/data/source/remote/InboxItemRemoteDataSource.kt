package xin.monus.checkit.data.source.remote

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.entity.User
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.db.LocalTable
import xin.monus.checkit.network.RequestManager
import xin.monus.checkit.network.api.NetWorkApi

class InboxItemRemoteDataSource private constructor(context: Context)  : InboxItemDataSource {

    private val dbHelper: LocalDbHelper = LocalDbHelper(context)
    companion object {
        private var INSTANCE: InboxItemRemoteDataSource? = null
        @JvmStatic fun getInstance(context: Context) =
                INSTANCE ?: InboxItemRemoteDataSource(context).apply {
                    INSTANCE = this
                }
    }

    override fun getInboxItems(callback: InboxItemDataSource.LoadInboxItemsCallback) {
        //TODO: 测试
//        val username = "test1"
        val user = getUserFromDatabase()
        val username = user!!.username
        val url = "inbox_item/"+username
        val params = HashMap<String, String>()
        doAsync {
            RequestManager.getInstance().requestAsync(url, 0, params, object : NetWorkApi.ReqCallback {
                override fun success(jsonString: String) {
                    val root = JsonParser().parse(jsonString).asJsonObject
                    // 基本数据类型
                    val statusJson = root.getAsJsonPrimitive("status")
                    val statusCode = statusJson.asInt

                    // 返回数据
                    if (statusCode != 200) {
                        // 回调
                        uiThread {
                            callback.onDataNotAvailable()
                        }
                    } else {
                        val items = ArrayList<InboxItem>()
                        val jsonArray = root.getAsJsonArray("data")
                        jsonArray.mapTo(items) { Gson().fromJson(it, InboxItem::class.java) }
                        // 回调
                        uiThread {
                            callback.onInboxItemsLoaded(items)
                        }
                    }
                }

                override fun fail(msg: String) {
                    // 回调
                    uiThread {
                        callback.onDataNotAvailable()
                    }
                }

            })
        }
    }

    override fun getInboxItemById(id: Int, callback: InboxItemDataSource.GetInboxItemCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addInboxItem(item: InboxItem, callback: InboxItemDataSource.OperationCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteInboxItem(id: Int, callback: InboxItemDataSource.OperationCallback) {

    }

    override fun deleteCompleteItems(callback: InboxItemDataSource.OperationCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllItems(callback: InboxItemDataSource.OperationCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateInboxItem(item: InboxItem, callback: InboxItemDataSource.OperationCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getUserFromDatabase(): User? {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(LocalTable.UserTable.COLUMN_USERNAME, LocalTable.UserTable.COLUMN_PASSWORD,
                LocalTable.UserTable.COLUMN_NICKNAME, LocalTable.UserTable.COLUMN_WEIGHT,
                LocalTable.UserTable.COLUMN_HEIGHT, LocalTable.UserTable.COLUMN_CALORIE)

        val cursor = db.query(LocalTable.UserTable.TABLE_NAME, projection,
                null, null, null, null, null)
        var user: User? = null
        with(cursor) {
            if (moveToFirst()) {
                val username = getString(getColumnIndexOrThrow(LocalTable.UserTable.COLUMN_USERNAME))
                val password = getString(getColumnIndexOrThrow(LocalTable.UserTable.COLUMN_PASSWORD))
                val nickname = getString(getColumnIndexOrThrow(LocalTable.UserTable.COLUMN_NICKNAME))
                val height = getDouble(getColumnIndexOrThrow(LocalTable.UserTable.COLUMN_HEIGHT))
                val weight = getDouble(getColumnIndexOrThrow(LocalTable.UserTable.COLUMN_WEIGHT))
                val calorie = getDouble(getColumnIndexOrThrow(LocalTable.UserTable.COLUMN_CALORIE))
                user = User(username, password, nickname, height, weight, calorie)
            }
            close()
        }
        db.close()
        return user
    }
}