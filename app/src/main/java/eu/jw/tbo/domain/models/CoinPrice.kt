package eu.jw.tbo.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class CoinPrice(
    val price: Double,
    val time: LocalDateTime
): Parcelable