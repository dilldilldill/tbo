package eu.jw.tbo.data.remote.dto

import eu.jw.tbo.domain.models.CoinPrice
import java.time.Instant
import java.time.ZoneId

data class CurrentPriceDto(
    val bitcoin: CoinDto
) {
    fun toCoinPrice(): CoinPrice {
        return CoinPrice(
            price = bitcoin.currency,
            time = Instant.ofEpochSecond(bitcoin.lastUpdatedAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        )
    }
}