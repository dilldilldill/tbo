package eu.jw.tbo.presentation.main_screen

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.jw.tbo.R
import eu.jw.tbo.domain.models.CoinPrice
import eu.jw.tbo.util.TestTags
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit
) {
    val numberFormat = NumberFormat.getInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 2;
        minimumFractionDigits = 2;
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CurrencySelector(
            label = stringResource(R.string.main_screen_dropdown_title_selected_currency),
            state = state,
            onEvent = onEvent,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start)
        )

        if (!state.currentPriceError.isNullOrBlank()) {
            Text(
                text = "Error: ${state.priceHistoryError}", color = Color.Red
            )
        }

        state.currentPrice?.let {
            Text(
                text = "${numberFormat.format(it.price)} ${state.selectedCurrency.uppercase()}",
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
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (!state.priceHistoryError.isNullOrBlank()) {
            Text(
                text = "Error: ${state.priceHistoryError}", color = Color.Red
            )
        }

        if (state.priceHistory.isNotEmpty()) {
            PriceTable(state.priceHistory, state, numberFormat)
        }
    }

}

@Composable
fun PriceTable(prices: List<CoinPrice>, state: MainScreenState, numberFormat: NumberFormat) {
    val dateColumnWeight = .3f
    val priceColumnWeight = .7f

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(
                    text = stringResource(R.string.main_screen_price_table_column_title_date),
                    weight = dateColumnWeight
                )
                TableCell(
                    text = "${stringResource(R.string.main_screen_price_table_column_title_price)} " +
                            "(${state.selectedCurrency.uppercase()})",
                    weight = priceColumnWeight
                )
            }
        }
        items(prices) {
            Row(Modifier.fillMaxWidth().testTag(TestTags.PRICE_TABLE_DATA_ROW)) {
                TableCell(
                    text = dateFormatter.format(it.time), weight = dateColumnWeight
                )
                TableCell(
                    text = numberFormat.format(it.price), weight = priceColumnWeight
                )
            }
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelector(
    label: String,
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // Providing LocalTextInputService with null so keyboard does not pop up
        // when clicking on the dropdown menu
        CompositionLocalProvider(LocalTextInputService provides null) {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = state.selectedCurrency.uppercase(),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(label) }
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            state.supportedCurrencies.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.uppercase(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        expanded = false
                        onEvent(MainScreenEvent.ChangedCurrency(it))
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}