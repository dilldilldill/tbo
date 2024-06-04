package eu.jw.tbo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.jw.swapi.util.Resource
import eu.jw.tbo.domain.repository.CoinRepository
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
    var state = savedStateHandle.getStateFlow(stateKey, MainScreenState())
        private set

    init {
        getPriceHistory()
    }

    fun getCurrentPrice() {
        savedStateHandle.updateState {
            it.copy(
                currentPrice = null,
                currentPriceLoading = true,
                currentPriceError = null
            )
        }

        viewModelScope.launch {
            val response = repository.getCurrentPrice()
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
        }
    }

    private fun getPriceHistory() {
        savedStateHandle.updateState {
            it.copy(
                priceHistory = emptyList(),
                priceHistoryLoading = true,
                priceHistoryError = null
            )
        }

        viewModelScope.launch {
            val response = repository.getPriceHistory()
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
        }
    }

    private fun SavedStateHandle.updateState(update: (MainScreenState) -> MainScreenState) {
        val updatedState = update(get<MainScreenState>(stateKey)!!)
        set(stateKey, updatedState)
    }
}