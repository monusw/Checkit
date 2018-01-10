package xin.monus.checkit.network.api

import android.content.Context
import xin.monus.checkit.data.entity.User


interface NetWorkApi {

    // 请求回调，成功则返回json字符串
    // 失败则返回错误信息
    interface ReqCallback {
        fun success(jsonString: String)
        fun fail(msg: String)
    }

    interface UserCallback {
        fun success(user: User)
        fun fail(msg: String)
    }

    interface SyncResult {
        fun success()
        fun fail()
    }

    fun checkLogin(username: String, password: String, callback: UserCallback)

    fun checkRegister(username: String, password: String, callback: UserCallback)

    fun syncInboxItems(username: String, context: Context, callback: SyncResult)

    fun syncUserInfo(username: String, context: Context, callback: SyncResult)


}