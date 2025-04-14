package com.example.projdraft_autovitals

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.projdraft_autovitals.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testLoginCancelClearsFieldsAndNavigates() {
        // Navigate to Login screen from Welcome screen
        composeTestRule.onNodeWithText("Login").performClick()

        // Fill in username and password
        composeTestRule.onNodeWithText("Username").performTextInput("test1")
        composeTestRule.onNodeWithText("Password").performTextInput("pass1")

        // Press Cancel
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Verify we're back on Welcome screen
        composeTestRule.onNodeWithText("Welcome to AutoVitals!").assertExists()

        // Navigate to Login again
        composeTestRule.onNodeWithText("Login").performClick()

        // Check fields are cleared
        composeTestRule.onNodeWithText("Username").assertTextEquals("")
        composeTestRule.onNodeWithText("Password").assertTextEquals("")
    }
}
