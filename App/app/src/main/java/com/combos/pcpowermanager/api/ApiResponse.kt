package com.combos.pcpowermanager.api

data class StatusResponse (
    val status : String
)

data class TestResponse (
    val message : String
)

data class ShutdownStatusResponse(
    val scheduled : Boolean,
    val minutes : Int?,
    val deadline : String?,
    val remaining : Int?
)

data class ShutdownScheduleRequest(
    val minutes : Int
)

data class ShutdownScheduleResponse(
    val message : String,
    val status : Boolean,
    val minutes : Int
)

data class ShutdownCancelResponse(
    val cancelled : Boolean,
    val message : String
)