package eu.jw.tbo.presentation

import androidx.lifecycle.SavedStateHandle
import eu.jw.tbo.data.repository.FakeErrorRepository
import eu.jw.tbo.data.repository.FakeSuccessRepository
import eu.jw.tbo.presentation.main_screen.MainScreenEvent
import eu.jw.tbo.presentation.main_screen.MainScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class MainScreenViewModelTest {
    private lateinit var successViewModel: MainScreenViewModel
    private lateinit var errorViewModel: MainScreenViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        successViewModel = MainScreenViewModel(
            FakeSuccessRepository(),
            SavedStateHandle()
        )
        // This is usually called from the MainActivity, so we have to call it manually here
        successViewModel.getCurrentPrice()

        errorViewModel = MainScreenViewModel(
            FakeErrorRepository(),
            SavedStateHandle()
        )
        // This is usually called from the MainActivity, so we have to call it manually here
        errorViewModel.getCurrentPrice()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialData_success_correctData() {
        val state = successViewModel.state.value
        assert(
            state.priceHistory.size == 5
                    && !state.priceHistoryLoading
                    && state.priceHistoryError == null
                    && state.currentPrice != null
                    && state.currentPrice!!.price == 66000.0
                    && !state.currentPriceLoading
                    && state.currentPriceError == null
                    && state.selectedCurrency == "eur"
                    && state.supportedCurrencies.size == 5
        )
    }

    @Test
    fun initialData_error_emptyAndErrorMessage() {
        val state = errorViewModel.state.value
        assert(
            state.priceHistory.isEmpty()
                    && !state.priceHistoryLoading
                    && state.priceHistoryError != null
                    && state.currentPrice == null
                    && !state.currentPriceLoading
                    && state.currentPriceError != null
        )
    }

    @Test
    fun changedCurrency_success_notEmptyAndUsd() {
        val state = successViewModel.state
        successViewModel.onEvent(MainScreenEvent.ChangedCurrency("usd"))

        assert(
            state.value.priceHistory.size == 5
                    && !state.value.priceHistoryLoading
                    && state.value.priceHistoryError == null
                    && state.value.currentPrice != null
                    && !state.value.currentPriceLoading
                    && state.value.currentPriceError == null
                    && state.value.selectedCurrency == "usd"
        )
    }
}