package eu.jw.tbo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import eu.jw.tbo.domain.models.CoinPrice
import eu.jw.tbo.presentation.ui.theme.TboTheme


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
    Column {
        if (state.loading) {
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (!state.error.isNullOrBlank()) {
            Text(text = "Error: ${state.error}", color = Color.Red)
        }
    }
    PriceTable(state.priceHistory)
}

@Composable
fun PriceTable(prices: List<CoinPrice>) {
    val column1Weight = .3f // 30%
    val column2Weight = .7f // 70%

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        // Here is the header
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(text = "Column 1", weight = column1Weight)
                TableCell(text = "Column 2", weight = column2Weight)
            }
        }
        items(prices) {
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = it.time.toString(), weight = column1Weight)
                TableCell(text = it.price.toString(), weight = column2Weight)
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
//            .weight(weight)
            .padding(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TboTheme {
//        MainScreen()
    }
}