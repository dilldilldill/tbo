package eu.jw.tbo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.jw.tbo.BuildConfig
import eu.jw.tbo.data.remote.CoinGeckoApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideStarWarsApi(): CoinGeckoApi {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val originalRequest = chain.request()

                        val requestBuilder = originalRequest.newBuilder()
                            .addHeader("accept", "application/json")
                            .addHeader("x-cg-demo-api-key", BuildConfig.API_KEY)

                        val request = requestBuilder.build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()
            .create(CoinGeckoApi::class.java)
    }
}