package eu.jw.tbo.data.remote.dto

import eu.jw.tbo.domain.models.CoinPrice
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

data class CurrentPriceDto(
    val bitcoin: Bitcoin
) {
    fun toCoinPrice(): CoinPrice {
        return CoinPrice(
            price = bitcoin.eur,
            time = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(bitcoin.last_updated_at),
                TimeZone.getDefault().toZoneId()
            )
        )
    }
}