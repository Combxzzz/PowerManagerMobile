package com.combos.pcpowermanager.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface PowerManagerApi {
    @GET("status")
    suspend fun getStatus() : Response<StatusResponse>

    @POST("test")
    suspend fun sendTestCommand() : Response<TestResponse>
}