package eu.jw.tbo.presentation

sealed class MainScreenEvent {
    data class ChangedCurrency(val currency: String) : MainScreenEvent()
}