package com.combos.pcpowermanager

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

suspend fun checkConnectionOld() : Boolean {
    return withContext(Dispatchers.IO) {
        val urlString = "http://10.0.0.79:8000/status"
        var connection: HttpURLConnection? = null

        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
            }

            val responseCode = connection.responseCode

            responseCode == HttpURLConnection.HTTP_OK
        } catch (_: IOException) {
            false
        } finally {
            connection?.disconnect()
        }
    }
}

suspend fun sendTestCommandOld() : Boolean {
    return withContext(Dispatchers.IO) {
        val urlString = "http://10.0.0.79:8000/test"
        var connection: HttpURLConnection? = null

        try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "POST"
                connectTimeout = 5000
                readTimeout = 5000
            }

            val responseCode = connection.responseCode

            responseCode == HttpURLConnection.HTTP_OK
        } catch (_: IOException) {
            false
        } finally {
            connection?.disconnect()
        }
    }
}