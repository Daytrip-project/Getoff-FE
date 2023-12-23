package com.daytrip.getoff.config

import android.app.Application
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitConfig : Application() {
    companion object{
        const val KDATA_API_BRIIS_URL = "https://apis.data.go.kr/1613000/BusRouteInfoInqireService/"
        const val SEOULDATA_API_BRI_URL = "http://ws.bus.go.kr/api/rest/busRouteInfo/"

        lateinit var retrofitKdataBriis: Retrofit
        lateinit var retrofitSeoulBri: Retrofit
    }

    override fun onCreate() {
        super.onCreate()

        // 레트로핏 인스턴스를 생성하고, 레트로핏에 각종 설정값들을 지정해줍니다.
        // 연결 타임아웃시간은 5초로 지정이 되어있고, HttpLoggingInterceptor를 붙여서 어떤 요청이 나가고 들어오는지를 보여줍니다.
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS).build()

        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성
        retrofitKdataBriis = Retrofit.Builder()
            .baseUrl(KDATA_API_BRIIS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofitSeoulBri = Retrofit.Builder()
            .baseUrl(SEOULDATA_API_BRI_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
    //GSon은 엄격한 json type을 요구하는데, 느슨하게 하기 위한 설정. success, fail이 json이 아니라 단순 문자열로 리턴될 경우 처리..
//    val gson : Gson = GsonBuilder()
//        .setLenient()
//        .create()
}