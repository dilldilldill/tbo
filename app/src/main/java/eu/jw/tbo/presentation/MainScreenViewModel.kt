package eu.jw.tbo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.jw.swapi.util.Resource
import eu.jw.tbo.domain.repository.CoinRepository
import eu.jw.tbo.util.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: CoinRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val stateKey = "state"
    var state = savedStateHandle.getStateFlow(stateKey, SharedPreferencesManager.getState())
        private set

    init {
        getSupportedCurrencies()
        getPriceHistory(state.value.selectedCurrency)
    }

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.ChangedCurrency -> {
                getCurrentPrice(event.currency)
                getPriceHistory(event.currency)

                savedStateHandle.updateState {
                    it.copy(
                        selectedCurrency = event.currency
                    )
                }
            }
        }
    }

    fun getCurrentPrice(currency: String = state.value.selectedCurrency) {
        savedStateHandle.updateState {
            it.copy(
                currentPrice = null,
                currentPriceLoading = true,
                currentPriceError = null
            )
        }

        viewModelScope.launch {
            val response = repository.getCurrentPrice(currency)
            if (response is Resource.Error) {
                savedStateHandle.updateState {
                    it.copy(
                        currentPrice = null,
                        currentPriceLoading = false,
                        currentPriceError = response.message
                    )
                }
                return@launch
            }

            savedStateHandle.updateState {
                it.copy(
                    currentPrice = response.data,
                    currentPriceLoading = false,
                    currentPriceError = null
                )
            }

            SharedPreferencesManager.saveState(state = state.value)
        }
    }

    fun getPriceHistory(currency: String = state.value.selectedCurrency) {
        savedStateHandle.updateState {
            it.copy(
                priceHistory = emptyList(),
                priceHistoryLoading = true,
                priceHistoryError = null
            )
        }

        viewModelScope.launch {
            val response = repository.getPriceHistory(currency)
            if (response is Resource.Error) {
                savedStateHandle.updateState {
                    it.copy(
                        priceHistory = emptyList(),
                        priceHistoryLoading = false,
                        priceHistoryError = response.message
                    )
                }
                return@launch
            }

            savedStateHandle.updateState {
                it.copy(
                    priceHistory = response.data!!,
                    priceHistoryLoading = false,
                    priceHistoryError = null
                )
            }

            SharedPreferencesManager.saveState(state = state.value)
        }
    }

    private fun getSupportedCurrencies() {
        viewModelScope.launch {
            val response = repository.getSupportedCurrencies()
            if (response is Resource.Success) {
                savedStateHandle.updateState {
                    it.copy(
                        supportedCurrencies = response.data!!
                    )
                }
            }
        }
    }

    private fun SavedStateHandle.updateState(update: (MainScreenState) -> MainScreenState) {
        val updatedState = update(get<MainScreenState>(stateKey)!!)
        set(stateKey, updatedState)
    }
}