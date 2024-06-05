package eu.jw.tbo.presentation.main_screen

import android.os.Parcelable
import eu.jw.tbo.domain.models.CoinPrice
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainScreenState(
    val currentPrice: CoinPrice? = null,
    val currentPriceLoading: Boolean = true,
    val currentPriceError: String? = null,

    val priceHistory: List<CoinPrice> = emptyList(),
    val priceHistoryLoading: Boolean = true,
    val priceHistoryError: String? = null,

    val selectedCurrency: String = "eur",
    val supportedCurrencies: List<String> = emptyList()
) : Parcelable