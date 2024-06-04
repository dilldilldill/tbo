package eu.jw.tbo.data.remote.dto

import eu.jw.tbo.domain.models.CoinPrice
import java.time.Instant
import java.time.ZoneId

data class PriceHistoryDto(
    val prices: List<List<Double>>
) {
    fun toCoinPriceList(): List<CoinPrice> {
        return prices.map {
            CoinPrice(
                price = it[1],
                time = Instant.ofEpochMilli(it[0].toLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            )
        }.dropLast(1).sortedByDescending { it.time }
    }
}