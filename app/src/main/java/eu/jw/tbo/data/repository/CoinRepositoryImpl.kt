package eu.jw.tbo.data.repository

import eu.jw.swapi.util.Resource
import eu.jw.tbo.data.remote.CoinGeckoApi
import eu.jw.tbo.domain.models.CoinPrice
import eu.jw.tbo.domain.repository.CoinRepository
import okhttp3.Response
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi
): CoinRepository {
    override suspend fun getCurrentPrice(): Resource<CoinPrice> {
        val response = api.getCurrentPrice("bitcoin", "eur")
        if (response.isSuccessful) {
            val priceDto = response.body() ?: return Resource.Error(message = "Received empty body for current price")
            return Resource.Success(priceDto.toCoinPrice())
        }
        return Resource.Error(message = parseError(response.raw()))
    }

    override suspend fun getPriceHistory(): Resource<List<CoinPrice>> {
        val response = api.getPriceHistory("bitcoin", "eur", 14)
        if (response.isSuccessful) {
            val historyDto = response.body() ?: return Resource.Error(message = "Received empty body for price history")
            return Resource.Success(historyDto.toCoinPriceList())
        }
        return Resource.Error(message = parseError(response.raw()))
    }

    private fun parseError(error: Response): String {
        return "${error.code}: ${error.message.ifBlank { "No error message" }}"
    }
}