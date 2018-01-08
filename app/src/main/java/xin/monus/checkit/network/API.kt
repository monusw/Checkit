package xin.monus.checkit.network

import com.google.gson.Gson
import com.google.gson.JsonParser
import xin.monus.checkit.data.entity.User
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

}