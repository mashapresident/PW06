package ua.study.smartgrid
import ua.study.smartgrid.*
import ua.study.smartgrid.domain.*
import ua.study.smartgrid.data.*
import ua.study.smartgrid.viewmodel.*
import ua.study.smartgrid.device.*
import ua.study.smartgrid.ui.*


import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Rule
import org.junit.Test

class EvidenceTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    private fun capture(name: String) {
        // Allow asynchronous map tiles and sensor samples to appear in evidence.
        Thread.sleep(if (name == "04-map") 30000 else if (name == "03-api-error") 0 else 3500)
        rule.waitForIdle()
        val folder =
            File(
                    InstrumentationRegistry.getInstrumentation()
                        .targetContext
                        .getExternalFilesDir(null),
                    "evidence",
                )
                .apply { mkdirs() }
        File(folder, "$name.png").outputStream().use {
            rule
                .onRoot()
                .captureToImage()
                .asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Test
    fun captureScreens() {
        capture("01-login")
        rule.onNodeWithText("Увійти").performClick()
        rule.waitUntil(10000) {
            rule.onAllNodesWithText("Огляд мережі").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText("Розрахувати").performScrollTo().performClick()
        rule.onNodeWithText("E = 0.960 МВт·год").assertExists()
        capture("02-dashboard")
        if (BuildConfig.PRACTICAL >= 2) {
            rule.onNodeWithText("Додати демонстраційні вузли").performScrollTo().performSemanticsAction(SemanticsActions.OnClick) { it() }
            rule.waitUntil(10000) {
                rule.onAllNodesWithText("Записів у журналі: 3").fetchSemanticsNodes().isNotEmpty()
            }
            rule.onNodeWithText("Журнал", useUnmergedTree = true).performClick()
            capture("03-records")
            rule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Деталі Вузол A"))
            rule.onNodeWithText("Деталі Вузол A").performClick()
            capture("03-details")
            rule.onNodeWithText("Журнал",useUnmergedTree=true).performClick()
        }
        if (BuildConfig.PRACTICAL >= 3) {
            rule.onNodeWithText("Огляд",useUnmergedTree=true).performClick()
            rule.onNodeWithText("Синхронізувати API").performScrollTo()
            capture("03-api")
            rule.onNodeWithText("Сценарій: Успіх").performClick()
            rule.onNodeWithText("HTTP 503").performClick()
            rule.onNodeWithText("Синхронізувати API").performSemanticsAction(SemanticsActions.OnClick) { it() }
            rule.waitUntil(10000) {rule.onAllNodesWithText("Сервер тимчасово недоступний").fetchSemanticsNodes().isNotEmpty()}
            capture("03-api-error")
            rule.onNodeWithText("Сценарій: HTTP 503").performClick()
            rule.onNodeWithText("Успіх").performClick()
            rule.mainClock.advanceTimeBy(6000)
            rule.waitForIdle()
        }
        if (BuildConfig.PRACTICAL >= 4) {
            rule.onNodeWithText("Мапа", useUnmergedTree = true).performClick()
            capture("04-map")
        }
        if (BuildConfig.PRACTICAL >= 5) {
            rule.onNodeWithText("Сенсори", useUnmergedTree = true).performClick()
            capture("05-sensors")
        }
        if (BuildConfig.PRACTICAL >= 6) {
            rule.onNodeWithText("Файли", useUnmergedTree = true).performScrollTo().performClick()
            capture("06-files")
        }
    }
}
