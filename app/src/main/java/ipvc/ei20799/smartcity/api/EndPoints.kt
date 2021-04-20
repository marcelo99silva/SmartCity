package ipvc.ei20799.smartcity.api

import ipvc.ei20799.smartcity.dataclasses.LoginResponse
import ipvc.ei20799.smartcity.dataclasses.Report
import retrofit2.http.*
import retrofit2.Call

interface EndPoints {

    @FormUrlEncoded
    @POST("/api/login")
    fun userLogin(
            @Field("email") email: String,
            @Field("password") password: String
    ):Call<LoginResponse>

    @GET("api/reports")
    fun getReports(): Call<List<Report>>
}