package eu.jw.tbo.presentation.main_screen

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dagger.hilt.android.testing.HiltAndroidRule
import eu.jw.tbo.presentation.MainActivity
import okhttp3.mockwebserver.MockWebServer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import eu.jw.tbo.di.AppModule
import eu.jw.tbo.presentation.main_screen.mock_responses.currenciesJson
import eu.jw.tbo.presentation.main_screen.mock_responses.currentPriceEur
import eu.jw.tbo.presentation.main_screen.mock_responses.priceHistoryEur
import eu.jw.tbo.util.SharedPreferencesManager
import eu.jw.tbo.util.TestTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppModule::class)
class MainScreenKtTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var  mockServer: MockWebServer

    @Before
    fun setUp() {
        hiltRule.inject()

        // Provide initial eur price history
        mockServer.enqueue(
            MockResponse().apply {
                setBody(currenciesJson)
            }
        )
        mockServer.enqueue(
            MockResponse().apply {
                setBody(priceHistoryEur)
            }
        )
        mockServer.enqueue(
            MockResponse().apply {
                setBody(currentPriceEur)
            }
        )
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
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.clearAndSetContent(content: @Composable () -> Unit) {
        (this.activity.findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0) as? ComposeView)?.setContent(content)
            ?: this.setContent(content)
    }
}