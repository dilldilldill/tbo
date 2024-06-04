package eu.jw.tbo.data.remote.dto

import eu.jw.tbo.domain.models.CoinPrice
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

data class PriceHistoryDto(
    val prices: List<List<Double>>
) {
    fun toCoinPriceList(): List<CoinPrice> {
        return prices.map {
            CoinPrice(
                price = it[1],
                time = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(it[0].toLong()),
                    TimeZone.getDefault().toZoneId()
                )
            )
        }
    }
}