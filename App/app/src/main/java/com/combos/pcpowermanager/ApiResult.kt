package com.combos.pcpowermanager

import com.combos.pcpowermanager.api.RetrofitClient
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