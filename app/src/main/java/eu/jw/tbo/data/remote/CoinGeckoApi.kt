package eu.jw.tbo.data.remote

import eu.jw.tbo.data.remote.dto.CurrentPriceDto
import eu.jw.tbo.data.remote.dto.PriceHistoryDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinGeckoApi {
    @GET("/coins/{coinId}/market_chart?vs_currency={currency}&days={numberOfDays}&interval=daily&precision=2")
    suspend fun getPriceHistory(
        @Path("coinId") coinId: String,
        @Path("currency") currency: String,
        @Path("numberOfDays") numberOfDays: Int
    ): Response<PriceHistoryDto>

    @GET("/simple/price?ids={coinId}&vs_currencies={currency}&include_market_cap=false&include_24hr_vol=false&include_24hr_change=false&include_last_updated_at=true&precision=2")
    suspend fun getCurrentPrice(
        @Path("coinId") coinId: String,
        @Path("currency") currency: String
    ): Response<CurrentPriceDto>
}