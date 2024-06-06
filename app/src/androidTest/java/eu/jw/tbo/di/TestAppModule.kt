package eu.jw.tbo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.jw.tbo.data.remote.CoinGeckoApi
import eu.jw.tbo.presentation.main_screen.mock_responses.currenciesJson
import eu.jw.tbo.presentation.main_screen.mock_responses.currentPriceEur
import eu.jw.tbo.presentation.main_screen.mock_responses.currentPriceUsd
import eu.jw.tbo.presentation.main_screen.mock_responses.priceHistoryEur
import eu.jw.tbo.presentation.main_screen.mock_responses.priceHistoryUsd
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    @Provides
    @Singleton
    fun provideMockWebServer(): MockWebServer {
        return MockWebServer().apply {
            dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    return with(request.path!!) {
                        when {
                            contains("supported_vs_currencies")
                                -> MockResponse().setBody(currenciesJson)
                            contains("market_chart") && contains("vs_currency=eur")
                                -> MockResponse().setBody(priceHistoryEur)
                            contains("market_chart") && contains("vs_currency=aud")
                                -> MockResponse().setBody(priceHistoryUsd)
                            contains("vs_currencies=eur")
                                -> MockResponse().setBody(currentPriceEur)
                            else -> MockResponse().setBody(currentPriceUsd)
                        }
                    }
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideCoinGeckoApi(
        mockWebServer: MockWebServer
    ): CoinGeckoApi {
        return Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(getCustomDeserializer()))
            .build()
            .create(CoinGeckoApi::class.java)
    }
}