package eu.jw.tbo.domain.repository

import eu.jw.swapi.util.Resource
import eu.jw.tbo.domain.models.CoinPrice

interface CoinRepository {
    suspend fun getPriceHistory(): Resource<List<CoinPrice>>
    suspend fun getCurrentPrice(): Resource<CoinPrice>
}