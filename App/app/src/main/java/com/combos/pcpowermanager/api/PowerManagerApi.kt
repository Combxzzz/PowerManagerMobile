package com.combos.pcpowermanager.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PowerManagerApi {
    @GET("status")
    suspend fun getStatus() : Response<StatusResponse>

    @POST("test")
    suspend fun sendTestCommand() : Response<TestResponse>

    @GET("shutdown/status")
    suspend fun getShutdownStatus() : Response<ShutdownStatusResponse>

    @POST("shutdown/schedule")
    suspend fun scheduleShutdown(
        @Body request: ShutdownScheduleRequest
    ) : Response<ShutdownScheduleResponse>

    @POST("shutdown/cancel")
    suspend fun cancelShutdown() : Response<ShutdownCancelResponse>

    @POST("shutdown/now")
    suspend fun shutdownNow() : Response<ShutdownNowResponse>
}