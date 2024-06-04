package eu.jw.tbo.domain.repository

import eu.jw.swapi.util.Resource
import eu.jw.tbo.domain.models.CoinPrice

interface CoinRepository {
    suspend fun getPriceHistory(currency: String): Resource<List<CoinPrice>>
    suspend fun getCurrentPrice(currency: String): Resource<CoinPrice>
    suspend fun getSupportedCurrencies(): Resource<List<String>>
}