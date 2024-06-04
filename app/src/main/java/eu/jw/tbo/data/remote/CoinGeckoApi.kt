package eu.jw.tbo.data.remote

import eu.jw.tbo.data.remote.dto.CurrentPriceDto
import eu.jw.tbo.data.remote.dto.PriceHistoryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApi {
    @GET("/api/v3/coins/{coinId}/market_chart?interval=daily&precision=2")
    suspend fun getPriceHistory(
        @Path("coinId") coinId: String,
        @Query("vs_currency") currency: String,
        @Query("days") numberOfDays: Int
    ): Response<PriceHistoryDto>

    @GET("/api/v3/simple/price?include_market_cap=false&include_24hr_vol=false&include_24hr_change=false&include_last_updated_at=true&precision=2")
    suspend fun getCurrentPrice(
        @Query("ids") coinId: String,
        @Query("vs_currencies") currency: String
    ): Response<CurrentPriceDto>

    @GET("/api/v3/simple/supported_vs_currencies")
    suspend fun getSupportedCurrencies(): Response<List<String>>
}