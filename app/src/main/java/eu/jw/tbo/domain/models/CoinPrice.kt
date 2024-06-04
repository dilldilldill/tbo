package eu.jw.tbo.domain.models

import java.time.LocalDateTime

data class CoinPrice(
    val price: Double,
    val time: LocalDateTime
)