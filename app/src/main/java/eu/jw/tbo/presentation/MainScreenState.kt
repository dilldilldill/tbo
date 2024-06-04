package eu.jw.tbo.presentation

import android.os.Parcelable
import eu.jw.tbo.domain.models.CoinPrice
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainScreenState(
    val currentPrice: CoinPrice? = null,
    val priceHistory: List<CoinPrice> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
): Parcelable