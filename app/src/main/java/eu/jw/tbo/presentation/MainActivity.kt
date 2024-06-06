package eu.jw.tbo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import eu.jw.tbo.domain.models.CoinPrice
import eu.jw.tbo.presentation.main_screen.MainScreen
import eu.jw.tbo.presentation.main_screen.MainScreenState
import eu.jw.tbo.presentation.main_screen.MainScreenViewModel
import eu.jw.tbo.presentation.ui.theme.TboTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TboTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = hiltViewModel<MainScreenViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit) {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                            withContext(Dispatchers.Default) {
                                while (true) {
                                    // Get current price automatically every 5
                                    // minutes or when activity is resumed
                                    viewModel.getCurrentPrice()
                                    delay(5 * 60_000)
                                }
                            }
                        }
                    }

                    val refreshState = rememberPullRefreshState(
                        refreshing = state.priceHistoryLoading || state.currentPriceLoading,
                        onRefresh = {
                            viewModel.getCurrentPrice()
                            viewModel.getPriceHistory()
                        }
                    )

                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(refreshState)
                            .padding(top = 32.dp)
                    ) {
                        MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            state = state,
                            onEvent = viewModel::onEvent
                        )

                        PullRefreshIndicator(
                            refreshing = state.priceHistoryLoading || state.currentPriceLoading,
                            state = refreshState
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TboTheme {
        MainScreen(
            state = MainScreenState(
                currentPrice = CoinPrice(63_540.0, LocalDateTime.now()),
                currentPriceLoading = false,

                priceHistory = listOf(
                    CoinPrice(62_001.0, LocalDateTime.now()),
                    CoinPrice(61_200.0, LocalDateTime.now())
                ),
                priceHistoryLoading = false
            ),
            onEvent = {}
        )
    }
}