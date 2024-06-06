package eu.jw.tbo.data.repository

import eu.jw.tbo.domain.models.CoinPrice
import eu.jw.tbo.domain.repository.CoinRepository
import eu.jw.tbo.util.Resource
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FakeSuccessRepositoryImpl: CoinRepository {
    override suspend fun getPriceHistory(currency: String): Resource<List<CoinPrice>> {
        val millis = System.currentTimeMillis()

        return Resource.Success(
            listOf(
                CoinPrice(
                    price = 61000.0,
                    Instant.ofEpochSecond(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                ),
                CoinPrice(
                    price = 62000.0,
                    Instant.ofEpochSecond(millis-1000)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                ),
                CoinPrice(
                    price = 63000.0,
                    Instant.ofEpochSecond(millis-2000)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                ),
                CoinPrice(
                    price = 64000.0,
                    Instant.ofEpochSecond(millis-3000)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                ),
                CoinPrice(
                    price = 65000.0,
                    Instant.ofEpochSecond(millis-4000)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                )
            )
        )
    }

    override suspend fun getCurrentPrice(currency: String): Resource<CoinPrice> {
        return Resource.Success(CoinPrice(price = 66000.0, time = LocalDateTime.now()))
    }

    override suspend fun getSupportedCurrencies(): Resource<List<String>> {
        return Resource.Success(listOf("eur", "usd", "gbp", "eth", "btc"))
    }
}