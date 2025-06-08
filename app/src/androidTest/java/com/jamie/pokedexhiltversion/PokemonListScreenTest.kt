package com.jamie.pokedexhiltversion

import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class PokemonListScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            device.executeShellCommand("settings put global window_animation_scale 0")
            device.executeShellCommand("settings put global transition_animation_scale 0")
            device.executeShellCommand("settings put global animator_duration_scale 0")
        }
    }

    private fun waitForPokemonListToLoad() {
        composeTestRule.waitUntil(timeoutMillis = 20_000) {
            composeTestRule
                .onAllNodesWithText("Bulbasaur", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun pokemonList_clickOnFirstItem_navigatesToDetailScreen() {
        waitForPokemonListToLoad()
        composeTestRule.onNodeWithText("Bulbasaur", ignoreCase = true).performClick()
        composeTestRule.onNodeWithText("About").assertIsDisplayed()
        composeTestRule.onNodeWithText("#1 Bulbasaur", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun searchBar_whenTypingPikachu_onlyPikachuIsDisplayed() {
        waitForPokemonListToLoad()
        composeTestRule.onNodeWithText("Search...").performTextInput("Pikachu")
        composeTestRule.onNodeWithText("Pikachu", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Bulbasaur", ignoreCase = true).assertDoesNotExist()
    }

    @Test
    fun favorites_toggleFavoriteAndFilter_showsInFavoriteList() {
        waitForPokemonListToLoad()
        composeTestRule.onNodeWithText("Charmander", ignoreCase = true).performClick()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule
                .onAllNodesWithContentDescription("Favorite")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithContentDescription("Favorite").performClick()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.onNodeWithText("Charmander", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Bulbasaur", ignoreCase = true).assertDoesNotExist()
    }
}