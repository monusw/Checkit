package xin.monus.checkit.network

import android.util.Log
import okhttp3.*
import xin.monus.checkit.network.api.NetWorkApi
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * 网络请求管理单例类，对OkHttp的封装
 */
class RequestManager {
    val BASE_URL = "http://101.132.99.105/CMS/api/index.php"    // 请求接口根地址
    val TYPE_GET = 0                       // get请求
    val TYPE_POST_FORM = 1                 // post请求(表单)
    val TAG = RequestManager::class.java.simpleName

    val CONNECT_TIME_OUT:Long = 30                // 连接请求超时时间
    val READ_TIME_OUT:Long = 30                   // 读取超时时间
    val WRITE_TIME_OUT:Long = 30                   // 写入超时时间

    lateinit var mOkHttpClient: OkHttpClient              // okHttpClient实例

    companion object {
        private var INSTANCE: RequestManager? = null
        // 获取单例
        @JvmStatic fun getInstance() : RequestManager {
            return INSTANCE ?: RequestManager().apply {
                INSTANCE = this
                mOkHttpClient = OkHttpClient().newBuilder()
                        .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                        .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                        .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                        .build()

            }
        }
    }


    /**
     * 异步请求总入口
     * 用回调返回请求结果
     */
    fun requestAsync(actionUrl: String, requestType: Int,
                    paramsMap:HashMap<String, String>, callback: NetWorkApi.ReqCallback) {
        when(requestType) {
            TYPE_GET -> requestGetAsync(actionUrl, paramsMap, callback)
            TYPE_POST_FORM -> requestPostAsync(actionUrl, paramsMap, callback)
        }
    }


    private fun requestGetAsync(actionUrl: String, paramsMap:HashMap<String, String>, callback: NetWorkApi.ReqCallback) {
        val tempParams = StringBuilder()
        var pos = 0
        for (key in paramsMap.keys) {
            if (pos > 0) {
                tempParams.append("&")
            }
            tempParams.append(String.format("%s=%s", key,
                    URLEncoder.encode(paramsMap[key]!!, "utf-8")))
            pos += 1
        }
        // 请求地址
        val requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl, tempParams.toString())

        val request = Request.Builder().url(requestUrl).build()
        val call = mOkHttpClient.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback.fail("GET 请求失败")
                Log.e(TAG, e.toString())
            }

            override fun onResponse(call: Call?, response: Response) {
                if (response.isSuccessful) {
                    callback.success(response.body()!!.string())
                } else {
                    callback.fail("服务器错误")
                    Log.e(TAG, "服务器错误")
                }
            }
        })
    }

    private fun requestPostAsync(actionUrl: String, paramsMap:HashMap<String, String>, callback: NetWorkApi.ReqCallback) {
        val builder = FormBody.Builder()
        for (key in paramsMap.keys) {
            builder.add(key, paramsMap[key]!!)
        }
        val formBody = builder.build()
        val requestUrl = BASE_URL + "/" + actionUrl
        val request = Request.Builder().url(requestUrl).post(formBody).build()
        val call = mOkHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback.fail("POST 请求失败")
                Log.e(TAG, e.toString())
            }

            override fun onResponse(call: Call?, response: Response) {
                if (response.isSuccessful) {
                    callback.success(response.body()!!.string())
                } else {
                    callback.fail("服务器错误")
                    Log.e(TAG, "服务器错误")
                }
            }

        })
    }

    fun requestPostAsyncWithJson(actionUrl: String, jsonString: String, callback: NetWorkApi.ReqCallback) {
        val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!
        val body = RequestBody.create(JSON, jsonString)
        val requestUrl = BASE_URL + "/" + actionUrl
        val request = Request.Builder().url(requestUrl).post(body).build()
        val call = mOkHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback.fail("POST JSON 失败")
            }

            override fun onResponse(call: Call?, response: Response) {
                if (response.isSuccessful) {
                    callback.success(response.body()!!.string())
                } else {
                    callback.fail("服务器错误")
                    Log.e(TAG, "服务器错误")
                }
            }

        })

    }


    /**
     * 单个删除请求
     */
    fun requestDeleteAsync(actionUrl: String, paramsMap:HashMap<String, String>, callback: NetWorkApi.ReqCallback) {
        val builder = FormBody.Builder()
        for (key in paramsMap.keys) {
            builder.add(key, paramsMap[key]!!)
        }
        val formBody = builder.build()
        val requestUrl = BASE_URL + "/" + actionUrl
        val request = Request.Builder().url(requestUrl).delete(formBody).build()
        val call = mOkHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback.fail("DELETE 请求失败")
                Log.e(TAG, e.toString())
            }

            override fun onResponse(call: Call?, response: Response) {
                if (response.isSuccessful) {
                    callback.success(response.body()!!.string())
                } else {
                    callback.fail("服务器错误")
                    Log.e(TAG, "服务器错误")
                }
            }

        })
    }


}

object DataStatus {
    val DELETED = -1
    val NEW = 0         //本地新增
    val UPDATE = 1      //本地更新
    val SYNC = 9        //已经与服务器同步过
}