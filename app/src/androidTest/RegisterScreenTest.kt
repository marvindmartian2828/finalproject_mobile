package com.example.projdraft_autovitals.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.projdraft_autovitals.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun register_withValidInputs_triggersRegistration() {
        composeTestRule.onNodeWithTag("nameField").performTextInput("John Doe")
        composeTestRule.onNodeWithTag("emailField").performTextInput("john@example.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("securepass")

        composeTestRule.onNodeWithTag("registerButton").performClick()

        // Add a delay or assert if you're updating UI
        // composeTestRule.waitUntil(...)
    }
}
