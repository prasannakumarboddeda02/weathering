package com.illegal.weathering.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
     suspend fun weatherApi(@Query("q") city : String,@Query("appid") key : String) : Response<ResultData>

}