package ua.study.smartgrid
import ua.study.smartgrid.*
import ua.study.smartgrid.domain.*
import ua.study.smartgrid.data.*
import ua.study.smartgrid.viewmodel.*
import ua.study.smartgrid.device.*
import ua.study.smartgrid.ui.*


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

/** Перевіряє реальні переходи та введення через Compose semantics. */
class AppUiTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginAndCalculate() {
        rule.onNodeWithText("Увійти").performClick()
        rule.waitUntil(10000) {
            rule.onAllNodesWithText("Огляд мережі").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText("Розрахувати").performScrollTo().performClick()
        rule.onNodeWithText("E = 0.960 МВт·год").assertExists()
    }

    @Test
    fun rejectsInvalidCredentials() {
        rule.onNodeWithText("Email").performTextClearance()
        rule.onNodeWithText("Email").performTextInput("bad")
        rule.onNodeWithText("Увійти").performClick()
        rule.onAllNodesWithText("Введіть коректний email").onFirst().assertExists()
    }
}
