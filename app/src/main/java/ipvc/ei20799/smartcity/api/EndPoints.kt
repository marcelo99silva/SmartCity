package ipvc.ei20799.smartcity.api

import ipvc.ei20799.smartcity.dataclasses.LoginResponse
import ipvc.ei20799.smartcity.dataclasses.NewReport
import ipvc.ei20799.smartcity.dataclasses.Report
import ipvc.ei20799.smartcity.dataclasses.UpdateReport
import retrofit2.http.*
import retrofit2.Call
import java.math.BigDecimal

interface EndPoints {

    @FormUrlEncoded
    @POST("/api/login")
    fun userLogin(
            @Field("email") email: String,
            @Field("password") password: String
    ):Call<LoginResponse>

    @GET("api/reports")
    fun getReports(): Call<List<Report>>

    @FormUrlEncoded
    @POST("/api/addReport")
    fun addReport(
            @Field("user_id") user_id: String,
            @Field("type_id") type_id: Int,
            @Field("title") title: String,
            @Field("description") description: String,
            @Field("encodedImage") encodedImage: String,
            @Field("latitude") latitude: BigDecimal,
            @Field("longitude") longitude: BigDecimal
    ):Call<NewReport>

    @FormUrlEncoded
    @POST("/api/updateReport/{id}")
    fun updateReport(
            @Path("id") id: String,
            @Field("type_id") type_id: Int,
            @Field("title") title: String,
            @Field("description") description: String,
            @Field("encodedImage") encodedImage: String
    ): Call<UpdateReport>

    @GET("/api/deleteReport/{id}")
    fun deleteReport(
            @Path("id") id: String
    ): Call<UpdateReport>
}