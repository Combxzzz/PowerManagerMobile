package com.combos.pcpowermanager

import com.combos.pcpowermanager.api.RetrofitClient
import com.combos.pcpowermanager.api.ShutdownScheduleRequest
import com.combos.pcpowermanager.api.ShutdownStatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun checkConnection() : Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.getStatus()
            response.isSuccessful && response.body()?.status == "Online"
        } catch (_: Exception) {
            false
        }
    }
}

suspend fun sendTestCommand() : Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.sendTestCommand()
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }
}

suspend fun scheduleShutdown(minutes : Int) : Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.scheduleShutdown(
                ShutdownScheduleRequest(minutes)
            )
            response.isSuccessful && response.body()?.status == true
        } catch (_: Exception) {
            false
        }
    }
}


suspend fun cancelShutdown() : Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.cancelShutdown()
            response.isSuccessful && response.body()?.cancelled == true
        } catch (_: Exception) {
            false
        }
    }
}

suspend fun getShutdownStatus() : ShutdownStatusResponse? {
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.getShutdownStatus()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
