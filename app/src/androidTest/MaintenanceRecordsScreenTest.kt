package com.example.projdraft_autovitals.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.projdraft_autovitals.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaintenanceRecordsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addRecordButton_opensDialog() {
        // Click on "Add New Record"
        composeTestRule.onNodeWithTag("addRecordButton").performClick()

        // Check that the dialog appears
        composeTestRule.onNodeWithText("Add Record").assertIsDisplayed()
    }

    @Test
    fun searchRecord_displaysCorrectItem() {
        // Type into the search field
        composeTestRule.onNodeWithTag("searchField")
            .performTextInput("Oil Change")

        // Trigger the search
        composeTestRule.onNodeWithContentDescription("Search").performClick()

        // Check that matching item is shown
        composeTestRule.onNodeWithText("Oil Change").assertIsDisplayed()
    }

    @Test
    fun editButton_opensEditDialog() {
        // Assume record "Brake Check" already exists and has test tag
        composeTestRule.onNodeWithTag("editButton_Brake Check").performClick()

        // Dialog for editing should appear
        composeTestRule.onNodeWithText("Edit Record").assertIsDisplayed()
    }

    @Test
    fun deleteButton_removesItemFromList() {
        // Click delete
        composeTestRule.onNodeWithTag("deleteButton_Tire Rotation").performClick()

        // Item should no longer be displayed
        composeTestRule.onNodeWithText("Tire Rotation").assertDoesNotExist()
    }
}
