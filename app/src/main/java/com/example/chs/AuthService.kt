package com.example.chs

//import okhttp3.Call
import io.reactivex.Single
import retrofit2.http.*
import java.time.LocalDateTime

interface AuthService {

    @POST("api/Auth/Login")
    fun login (@Body loginRequest: LoginRequest): Single<LoginResponse>

    @GET("/api/Heater")
    fun getTemperature() : Single<TemperatureResponse>

    @GET("/api/HeaterSchedule")
    fun getSchedule() : Single<Long>


    @POST("/api/Heater")
    fun setTemperature(@Query("temperature") temp:Int): Single<TemperatureResponse>

    @POST("/Schedule")
    fun schedule(@Body heaterSchedule: HeaterSchedule ): Single <HeaterTimeResponse>

}