package com.example.forecastmvvm.data.network

import android.content.Context
import android.net.ConnectivityManager
import com.example.forecastmvvm.internal.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Custom interceptor for checking the internet connection.
 * Implements ConnectivityInterceptor which implements Interceptor form OkHttp3
 */
class ConnectivityInterceptorImpl(context: Context) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    /**
     * Throws custom exception when is not online.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        if (isOnline().not()) throw NoConnectivityException() else return chain.proceed(chain.request())
    }

    /**
     * Checks connection to the Internet.
     * 'isConnectedOrConnecting' var checks also for wifi connection.
     */
    private fun isOnline(): Boolean {
        val connectivityManager =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }
}