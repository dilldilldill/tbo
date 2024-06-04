package eu.jw.tbo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.jw.swapi.util.Resource
import eu.jw.tbo.domain.repository.CoinRepository
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
        getCurrentPrice()
        getPriceHistory()
    }

    private fun getCurrentPrice() {
        savedStateHandle.updateState {
            it.copy(
                currentPrice = null,
                loading = true,
                error = null
            )
        }

        viewModelScope.launch {
            val response = repository.getCurrentPrice()
            println("HELLO1 ${response.data}")

            if (response is Resource.Error) {
                savedStateHandle.updateState {
                    it.copy(
                        currentPrice = null,
                        loading = false,
                        error = response.message
                    )
                }
                return@launch
            }

            savedStateHandle.updateState {
                it.copy(
                    currentPrice = response.data,
                    loading = false,
                    error = null
                )
            }
        }
    }

    private fun getPriceHistory() {
        savedStateHandle.updateState {
            it.copy(
                priceHistory = emptyList(),
                loading = true,
                error = null
            )
        }

        viewModelScope.launch {
            val response = repository.getPriceHistory()
            println("HELLO2 ${response.message}")
            if (response is Resource.Error) {
                savedStateHandle.updateState {
                    it.copy(
                        priceHistory = emptyList(),
                        loading = false,
                        error = response.message
                    )
                }
                return@launch
            }

            savedStateHandle.updateState {
                it.copy(
                    priceHistory = response.data!!,
                    loading = false,
                    error = null
                )
            }
        }
    }

    private fun SavedStateHandle.updateState(update: (MainScreenState) -> MainScreenState) {
        val updatedState = update(get<MainScreenState>(stateKey)!!)
        set(stateKey, updatedState)
    }
}