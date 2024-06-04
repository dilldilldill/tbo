package eu.jw.tbo.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.jw.tbo.BuildConfig
import eu.jw.tbo.data.remote.CoinGeckoApi
import eu.jw.tbo.data.remote.dto.CoinDto
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideStarWarsApi(): CoinGeckoApi {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com")
            .addConverterFactory(GsonConverterFactory.create(getCustomDeserializer()))
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

/**
 * Custom deserializer is needed to be able to parse/deserialize different currencies, since the
 * JSON object changes it's property names according to the currency that is requested.
 * For example from "eur" to "usd":
 * --------------------------------------------
 *  {
 *   "bitcoin": {
 *     "eur": 63757.19,
 *     "last_updated_at": 1717431714
 *   }
 * }
 * --------------------------------------------
 * {
 *   "bitcoin": {
 *     "usd": 65457.19,
 *     "last_updated_at": 1717431714
 *   }
 * }
 * --------------------------------------------
 */
fun getCustomDeserializer(): Gson = GsonBuilder().apply {
        registerTypeAdapter(CoinDto::class.java, object : JsonDeserializer<CoinDto> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): CoinDto {
                var lastUpdatedAt: Long? = null
                var currency: Double? = null

                json?.getAsJsonObject()?.let {
                    for (key in it.keySet()) {
                        if (key == "last_updated_at") {
                            // This property is last_updated_at
                            lastUpdatedAt = json.asJsonObject.get(key).asLong

                        } else {
                            // The other property is always currency
                            currency = json.asJsonObject.get(key).asDouble
                        }
                    }
                }

                return CoinDto(currency = currency!!, lastUpdatedAt = lastUpdatedAt!!)
            }
        })}.create()