package ua.study.smartgrid
import ua.study.smartgrid.*
import ua.study.smartgrid.domain.*
import ua.study.smartgrid.data.*
import ua.study.smartgrid.viewmodel.*
import ua.study.smartgrid.device.*
import ua.study.smartgrid.ui.*


import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.Assume.assumeTrue

/** Checks missing navigation and role boundaries found during the requirements audit. */
class AuditUiTest {
    @get:Rule val rule=createAndroidComposeRule<MainActivity>()
    private fun login() {
        rule.onNodeWithText("Увійти").performClick()
        rule.waitUntil(10000) { rule.onAllNodesWithText("Огляд мережі").fetchSemanticsNodes().isNotEmpty() }
    }
    @Test fun detailsRouteCarriesIdAndMapReturnsToSelectedCard() {
        assumeTrue(BuildConfig.PRACTICAL>=2)
        login()
        rule.onNodeWithText("Додати демонстраційні вузли").performScrollTo().performSemanticsAction(SemanticsActions.OnClick) { it() }
        rule.onNodeWithText("Журнал",useUnmergedTree=true).performClick()
        rule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Деталі Вузол A"))
        rule.onNodeWithText("Деталі Вузол A").performScrollTo().performClick()
        rule.onNodeWithText("Деталі вузла").assertExists()
        rule.onNodeWithText("Вузол A").assertExists()
        if(BuildConfig.PRACTICAL>=4) {
            rule.onNodeWithText("Показати вузол на мапі").performScrollTo().performClick()
            rule.onNodeWithText("Відкрити вибрану картку").performClick()
            rule.onNodeWithText("Журнал вузлів").assertExists()
        }
    }
    @Test fun viewerCannotCreateRecords() {
        assumeTrue(BuildConfig.PRACTICAL>=2)
        rule.onNodeWithText("Email").performTextReplacement("viewer@demo.ua")
        login()
        rule.onNodeWithText("Журнал",useUnmergedTree=true).performClick()
        rule.onNodeWithText("Новий запис").assertDoesNotExist()
    }
}
