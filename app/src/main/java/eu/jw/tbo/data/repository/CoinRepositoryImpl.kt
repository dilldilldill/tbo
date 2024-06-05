package eu.jw.tbo.data.repository

import android.content.res.Resources
import eu.jw.tbo.util.Resource
import eu.jw.tbo.R
import eu.jw.tbo.data.remote.CoinGeckoApi
import eu.jw.tbo.domain.models.CoinPrice
import eu.jw.tbo.domain.repository.CoinRepository
import okhttp3.Response
import javax.inject.Inject

private const val COIN_ID = "bitcoin"

class CoinRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi
) : CoinRepository {
    private val resources: Resources = Resources.getSystem()

    override suspend fun getCurrentPrice(currency: String): Resource<CoinPrice> {
        val response = api.getCurrentPrice(COIN_ID, currency)
        if (response.isSuccessful) {
            val priceDto = response.body() ?: return Resource.Error(
                message = resources.getString(R.string.repository_error_empty_body_current_price)
            )
            return Resource.Success(priceDto.toCoinPrice())
        }
        return Resource.Error(message = parseError(response.raw()))
    }

    override suspend fun getPriceHistory(currency: String): Resource<List<CoinPrice>> {
        val response = api.getPriceHistory(COIN_ID, currency, 14)
        if (response.isSuccessful) {
            val historyDto = response.body() ?: return Resource.Error(
                message = resources.getString(R.string.repository_error_empty_body_price_history)
            )
            return Resource.Success(historyDto.toCoinPriceList())
        }
        return Resource.Error(message = parseError(response.raw()))
    }

    override suspend fun getSupportedCurrencies(): Resource<List<String>> {
        val response = api.getSupportedCurrencies()
        if (response.isSuccessful) {
            val currencies = response.body() ?: return Resource.Error(
                message = resources.getString(
                    R.string.repository_error_empty_body_supported_currencies
                )
            )
            return Resource.Success(currencies.sorted())
        }
        return Resource.Error(message = parseError(response.raw()))
    }

    private fun parseError(error: Response): String {
        return "${error.code}: ${error.message.ifBlank { 
            resources.getString(R.string.repository_error_no_error_message) 
        }}"
    }
}