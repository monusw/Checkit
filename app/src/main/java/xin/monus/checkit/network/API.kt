package xin.monus.checkit.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.entity.User
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.data.source.local.InboxItemLocalDataSource
import xin.monus.checkit.network.api.NetWorkApi
import xin.monus.checkit.network.api.NetWorkApi.ReqCallback


object API : NetWorkApi {

    /**
     * 验证登录接口
     */
    override fun checkLogin(username: String, password: String, callback: NetWorkApi.UserCallback) {
        val params = HashMap<String, String>()
        params.put("username", username)
        params.put("password", password)
        RequestManager.getInstance().requestAsync("login", 1, params, object :ReqCallback {
            override fun success(jsonString: String) {
                val root = JsonParser().parse(jsonString).asJsonObject

                // 基本数据类型
                val statusJson = root.getAsJsonPrimitive("status")
                val statusCode = statusJson.asInt

                // 返回数据正常
                if (statusCode != 200) {
                    val msgJson = root.getAsJsonPrimitive("msg")
                    val msg = msgJson.asString
                    callback.fail(msg)
                } else {
                    // Object类型
                    val dataJson = root.getAsJsonObject("data")
                    val user = Gson().fromJson(dataJson, User::class.java)
                    callback.success(user)
                }
            }

            override fun fail(msg: String) {
                callback.fail(msg)
            }
        })
    }

    /**
     * 验证注册接口，返回注册后的用户信息
     */
    override fun checkRegister(username: String, password: String, callback: NetWorkApi.UserCallback) {
        val params = HashMap<String, String>()
        params.put("username", username)
        params.put("password", password)
        RequestManager.getInstance().requestAsync("register", 1, params, object : ReqCallback {
            override fun success(jsonString: String) {
                val root = JsonParser().parse(jsonString).asJsonObject

                // 基本数据类型，返回的状态码
                val statusJson = root.getAsJsonPrimitive("status")
                val statusCode = statusJson.asInt
                // 返回数据正常
                if (statusCode != 200) {
                    val msgJson = root.getAsJsonPrimitive("msg")
                    val msg = msgJson.asString
                    callback.fail(msg)
                } else {
                    // Object类型，数据主体
                    val dataJson = root.getAsJsonObject("data")
                    val user = Gson().fromJson(dataJson, User::class.java)
                    callback.success(user)
                }
            }

            override fun fail(msg: String) {
                callback.fail(msg)
            }

        })
    }

    /**
     * 针对更新或者新增的，上传到服务端
     */
    override fun syncInboxItems(username: String, context: Context, callback: NetWorkApi.SyncResult) {
        val database = InboxItemLocalDataSource.getInstance(context)
        database.getInboxItems(object : InboxItemDataSource.LoadInboxItemsCallback {
            override fun onInboxItemsLoaded(items: List<InboxItem>) {
                val tempList = ArrayList<InboxItem>()
                items.filterTo(tempList) { it.status < DataStatus.SYNC }
                if (tempList.isNotEmpty()) {
                    val jsonString = Gson().toJson(tempList)
                    RequestManager.getInstance().requestPostAsyncWithJson("inbox_item/test1", jsonString, object : NetWorkApi.ReqCallback {
                        override fun success(jsonString: String) {
                            println(jsonString)
                            val root = JsonParser().parse(jsonString).asJsonObject
                            // 基本数据类型
                            val statusJson = root.getAsJsonPrimitive("status")
                            val statusCode = statusJson.asInt

                            // 返回数据
                            if (statusCode != 200) {
                                callback.fail()
                            } else {
                                val itemList = ArrayList<InboxItem>()
                                val jsonArray = root.getAsJsonArray("data")
                                jsonArray.mapTo(itemList) { Gson().fromJson(it, InboxItem::class.java) }
                                for (item in itemList) {
                                    if (item.status == DataStatus.SYNC) {
                                        database.updateInboxItem(item, object : InboxItemDataSource.OperationCallback {
                                            override fun success() {
                                                println("update success")
                                            }
                                            override fun fail() {
                                                println("update fail")
                                            }
                                        })
                                    }
                                }

                                callback.success()
                            }
                        }
                        // 网络请求失败
                        override fun fail(msg: String) {
                            println(msg)
                        }

                    })


                } else {
                    callback.fail()
                }
            }

            override fun onDataNotAvailable() {
                callback.fail()
            }

        })
    }

}