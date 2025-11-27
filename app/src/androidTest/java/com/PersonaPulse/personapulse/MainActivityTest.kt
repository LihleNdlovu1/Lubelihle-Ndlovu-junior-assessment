package com.PersonaPulse.personapulse

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest{
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun AddTaskFlowTest(){
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val allowButton = device.wait(Until.findObject(By.text("Allow")), 2000)

        if (allowButton != null) {
            allowButton.click()
        }

        composeTestRule.onNodeWithTag("add_task_button")
            .assertExists()
            .performClick()

        composeTestRule
            .onNodeWithTag("TitleTextField")
            .performTextInput("Test Task")

        composeTestRule
            .onNodeWithText("Description (optional)")
            .performTextInput("Test Description")

        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("Test Task").assertIsDisplayed()
    }

    @Test
    fun EditTaskFlowTest(){
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val allowButton = device.wait(Until.findObject(By.text("Allow")), 2000)

        if (allowButton != null) {
            allowButton.click()
        }

        composeTestRule.onNodeWithTag("add_task_button")
            .assertExists()
            .performClick()

        composeTestRule
            .onNodeWithTag("TitleTextField")
            .performTextInput("Test Task")

        composeTestRule
            .onNodeWithText("Description (optional)")
            .performTextInput("Test Description")

        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("Test Task").assertIsDisplayed()

        composeTestRule.waitForIdle()

        composeTestRule.onNode(
            hasTestTag("MoreOptionsButton")
        ).performClick()

        composeTestRule.onNodeWithText("Edit").performClick()
        composeTestRule.waitForIdle()

        val titleField = composeTestRule.onNodeWithTag("TitleTextField")
        titleField.assertExists()
        titleField.performClick()
        titleField.performTextInput(" Some update task")

        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.onNodeWithText("Test Task Some update task").assertExists()
    }


    @Test
    fun DeleteTaskFlowTest(){
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val allowButton = device.wait(Until.findObject(By.text("Allow")), 2000)


        if (allowButton != null) {
            allowButton.click()
        }

        composeTestRule.onNodeWithTag("add_task_button")
            .assertExists()
            .performClick()

        composeTestRule
            .onNodeWithTag("TitleTextField")
            .performTextInput("Test Task")

    }

}