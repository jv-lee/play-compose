package com.lee.playcompose.base.net

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.lee.playcompose.base.core.ApplicationExtensions.app
import com.lee.playcompose.base.net.adapter.DoubleDefaultAdapter
import com.lee.playcompose.base.net.adapter.GsonDefaultAdapterFactory
import com.lee.playcompose.base.net.adapter.IntegerDefaultAdapter
import com.lee.playcompose.base.net.adapter.LongDefaultAdapter
import com.lee.playcompose.base.net.client.OkHttpClientBuilder
import com.lee.playcompose.base.net.factory.CoroutineCallAdapterFactory
import com.lee.playcompose.base.net.factory.FlowCallAdapterFactory
import com.lee.playcompose.base.net.factory.ProtoConverterFactory
import com.lee.playcompose.base.net.request.IRequest
import com.lee.playcompose.base.net.request.Request
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File

/**
 * @author jv.lee
 * @date 2020/3/20
 * @description Retrofit库封装
 */
class HttpManager private constructor() {

    companion object {
        private const val MAX_CACHE: Long = 10 * 1024 * 1024

        @Volatile
        private var instance: HttpManager? = null
        private var gson: Gson? = null

        @JvmStatic
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: HttpManager().also { instance = it }
            }

        fun getGson() = gson ?: GsonBuilder()
            .registerTypeAdapterFactory(GsonDefaultAdapterFactory())
            .registerTypeAdapter(Int::class.java, IntegerDefaultAdapter())
            .registerTypeAdapter(Double::class.java, DoubleDefaultAdapter())
            .registerTypeAdapter(Long::class.java, LongDefaultAdapter())
            .create().also { gson = it }

    }

    private val mServiceMap by lazy { HashMap<String, Any>() }
    private val mInterceptors by lazy { ArrayList<Interceptor>() }
    private var mClient: OkHttpClient? = null
    private var mDownloadClient: OkHttpClient? = null
    private var isUnSafeClient = false

    fun setUnSafeClient(enable: Boolean) {
        this.isUnSafeClient = enable
    }

    fun putInterceptor(interceptor: Interceptor) {
        mInterceptors.add(interceptor)
    }

    fun getClient(): OkHttpClient {
        return getOkHttpClient(Request("https://android.cn", IRequest.ConverterType.JSON))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getService(serviceClass: Class<T>, request: Request): T {
        if (request.isDownload) {
            return createService(serviceClass, request)
        }
        return if (mServiceMap.containsKey(serviceClass.name + request.key)) {
            mServiceMap[serviceClass.name + request.key] as T
        } else {
            createService(serviceClass, request).also {
                mServiceMap[serviceClass.name + request.key] = it!!
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getService(serviceClass: Class<T>, request: Request, client: OkHttpClient): T {
        if (request.isDownload) {
            return createService(serviceClass, request, client)
        }
        return if (mServiceMap.containsKey(serviceClass.name)) {
            mServiceMap[serviceClass.name + request.key] as T
        } else {
            createService(serviceClass, request, client).also {
                mServiceMap[serviceClass.name + request.key] = it!!
            }
        }
    }

    private fun <T> createService(serviceClass: Class<T>, request: Request): T {
        return createService(serviceClass, request, getOkHttpClient(request))
    }

    private fun <T> createService(
        serviceClass: Class<T>, request: Request, client: OkHttpClient
    ): T {
        val builder = Retrofit.Builder()
            .baseUrl(request.baseUrl)

        request.converterTypes?.run { map { builder.addConverterFactory(getConverterFactory(it)) } }
            ?: kotlin.run { builder.addConverterFactory(getConverterFactory(request.converterType)) }

        request.callTypes?.run { map { builder.addCallAdapterFactory(getCallAdapter(it)) } }
            ?: kotlin.run { builder.addCallAdapterFactory(getCallAdapter(request.callType)) }

        return builder.client(client).build().create(serviceClass)
    }

    @Synchronized
    private fun getOkHttpClient(request: Request): OkHttpClient {
        if (request.isDownload && mDownloadClient != null) return mDownloadClient as OkHttpClient
        if (!request.isDownload && mClient != null) return mClient as OkHttpClient

        val builder = if (isUnSafeClient) OkHttpClientBuilder().getUnSafeClient().newBuilder()
        else OkHttpClientBuilder().getSafeClient().newBuilder()

        //cache
        val httpCacheDirectory = File(app.cacheDir, "OkHttpCache")
        builder.cache(Cache(httpCacheDirectory, MAX_CACHE))

        mInterceptors.map { builder.addInterceptor(it) }

        val client = builder.build()
        if (request.isDownload) mDownloadClient = client else mClient = client

        return client
    }

    private fun getConverterFactory(type: Int): Converter.Factory {
        return when (type) {
            IRequest.ConverterType.STRING -> ScalarsConverterFactory.create()
            IRequest.ConverterType.JSON -> GsonConverterFactory.create(getGson())
            IRequest.ConverterType.PROTO -> ProtoConverterFactory.create()
            else -> ScalarsConverterFactory.create()
        }
    }

    private fun getCallAdapter(type: Int): CallAdapter.Factory {
        return when (type) {
            IRequest.CallType.COROUTINE -> CoroutineCallAdapterFactory()
            IRequest.CallType.FLOW -> FlowCallAdapterFactory()
            else -> CoroutineCallAdapterFactory()
        }
    }

    /**
     * 获取服务器错误码信息
     */
    fun getServerMessage(throwable: Throwable, filedName: String = "msg"): String {
        if (throwable is HttpException) {
            try {
                throwable.response()?.errorBody()?.let {
                    val json = JsonParser.parseString(it.string())
                    val msg = json.asJsonObject.get(filedName).asString

                    return if (TextUtils.isEmpty(msg)) {
                        throwable.message ?: throwable.toString()
                    } else {
                        msg ?: throwable.message ?: throwable.toString()
                    }
                }
            } catch (e: Exception) {
                return throwable.message ?: throwable.toString()
            }
        }
        return throwable.message ?: throwable.toString()
    }

}