package eu.jw.tbo.presentation.main_screen

sealed class MainScreenEvent {
    data class ChangedCurrency(val currency: String) : MainScreenEvent()
}