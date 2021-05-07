package ipvc.ei20799.smartcity.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceBuilder {
    private val webHost = "http://smartcityapi.000webhostapp.com/"
    private val localHost = "http://192.168.56.1/cm/SmartCityWebApi/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
            .baseUrl(webHost)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    fun <T> buildService(service: Class<T>) :T{
        return retrofit.create(service)
    }
}