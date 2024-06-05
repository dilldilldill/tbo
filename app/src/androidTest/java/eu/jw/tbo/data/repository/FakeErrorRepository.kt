package eu.jw.tbo.data.repository

import eu.jw.tbo.domain.models.CoinPrice
import eu.jw.tbo.domain.repository.CoinRepository
import eu.jw.tbo.util.Resource

class FakeErrorRepository: CoinRepository {
    override suspend fun getPriceHistory(currency: String): Resource<List<CoinPrice>> {
        return Resource.Error(message = "404 Resource not found")
    }

    override suspend fun getCurrentPrice(currency: String): Resource<CoinPrice> {
        return Resource.Error(message = "404 Resource not found")
    }

    override suspend fun getSupportedCurrencies(): Resource<List<String>> {
        return Resource.Error(message = "404 Resource not found")
    }
}