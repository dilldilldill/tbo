package eu.jw.tbo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import eu.jw.tbo.domain.models.CoinPrice
import eu.jw.tbo.presentation.ui.theme.TboTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.y")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                                    viewModel.getCurrentPrice()
                                    delay(60_000)
                                }
                            }
                        }
                    }

                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        state = state
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, state: MainScreenState) {
    Column(modifier = modifier) {
        if (state.currentPriceLoading) {
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (!state.currentPriceError.isNullOrBlank()) {
            Text(text = "Error: ${state.priceHistoryError}", color = Color.Red)
        }

        state.currentPrice?.let {
            Text(
                text = "${it.price} €",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            )

            Text(
                text = "(${timeFormatter.format(it.time)})",
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }

        if (state.priceHistoryLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (!state.priceHistoryError.isNullOrBlank()) {
            Text(text = "Error: ${state.priceHistoryError}", color = Color.Red)
        }

        if (state.priceHistory.isNotEmpty()) {
            PriceTable(state.priceHistory)
        }
    }

}

@Composable
fun PriceTable(prices: List<CoinPrice>) {
    val dateColumnWeight = .3f
    val priceColumnWeight = .7f

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(text = "Date", weight = dateColumnWeight)
                TableCell(text = "Price (€)", weight = priceColumnWeight)
            }
        }
        items(prices) {
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = dateFormatter.format(it.time), weight = dateColumnWeight)
                TableCell(text = it.price.toString(), weight = priceColumnWeight)
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TboTheme {
        MainScreen(
            state = MainScreenState(
                currentPrice = CoinPrice(1.0, LocalDateTime.now()),
                currentPriceLoading = false,
                currentPriceError =  null,

                priceHistory = listOf(
                    CoinPrice(1.0, LocalDateTime.now()),
                    CoinPrice(1.0, LocalDateTime.now())
                ),
                priceHistoryLoading = false,
                priceHistoryError = null
            )
        )
    }
}