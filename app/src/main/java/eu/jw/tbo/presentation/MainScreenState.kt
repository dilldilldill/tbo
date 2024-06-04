package eu.jw.tbo.presentation

import android.os.Parcelable
import eu.jw.tbo.domain.models.CoinPrice
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainScreenState(
    val currentPrice: CoinPrice? = null,
    val currentPriceLoading: Boolean = false,
    val currentPriceError: String? = null,

    val priceHistory: List<CoinPrice> = emptyList(),
    val priceHistoryLoading: Boolean = false,
    val priceHistoryError: String? = null,

    val selectedCurrency: String = "eur",
    val supportedCurrencies: List<String> = emptyList()
): Parcelable