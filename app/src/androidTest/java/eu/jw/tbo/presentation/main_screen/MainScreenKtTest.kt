package eu.jw.tbo.presentation.main_screen

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import eu.jw.tbo.presentation.MainActivity
import okhttp3.mockwebserver.MockWebServer
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import eu.jw.tbo.di.AppModule
import eu.jw.tbo.util.SharedPreferencesManager
import eu.jw.tbo.util.TestTags

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppModule::class)
class MainScreenKtTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var mockServer: MockWebServer

    private val numberFormat = NumberFormat.getInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    @Before
    fun setUp() {
        hiltRule.inject()
        SharedPreferencesManager.clear()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun initialData_success_displayingEurData() {
        composeRule
            .onAllNodesWithTag(TestTags.PRICE_TABLE_DATA_ROW)
            .assertCountEquals(14)

        // Check top row price cell for EUR value
        composeRule
            .onAllNodesWithTag(TestTags.PRICE_TABLE_DATA_ROW_PRICE_VALUE)[0]
                .assertTextEquals(numberFormat.format(64881.83))

        // Check current price for EUR value
        composeRule
            .onNodeWithTag(TestTags.CURRENT_PRICE_TEXT)
            .assertTextEquals("${numberFormat.format(65780.93)} EUR")
    }

    @Test
    fun changeCurrency_success_displayingUsdData() {
        // Change currency to USD
        val dropdownMenu = composeRule.onNodeWithTag(TestTags.CURRENCY_DROPDOWN_MENU)
        dropdownMenu.assertExists()
        dropdownMenu.performClick()
        val menuItems = composeRule.onAllNodesWithTag(TestTags.CURRENCY_DROPDOWN_MENU_ITEM)
        menuItems[2].performClick()

        // Check top row price cell for USD value
        composeRule
            .onAllNodesWithTag(TestTags.PRICE_TABLE_DATA_ROW_PRICE_VALUE)[0]
            .assertTextEquals(numberFormat.format(70600.01))

        // Check current price for USD value
        composeRule
            .onNodeWithTag(TestTags.CURRENT_PRICE_TEXT)
            .assertTextEquals("${numberFormat.format(71518.52)} AUD")
    }
}