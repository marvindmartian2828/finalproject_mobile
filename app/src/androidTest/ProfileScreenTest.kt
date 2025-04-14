package com.example.projdraft_autovitals.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.projdraft_autovitals.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun editProfile_updatesFieldsAndSaves() {
        // Click on Edit Profile
        composeTestRule.onNodeWithTag("editProfileButton").performClick()

        // Type a new name
        composeTestRule.onNodeWithTag("nameField").performTextReplacement("Jane Smith")

        // Click Save
        composeTestRule.onNodeWithTag("saveProfileButton").performClick()

        // Check that updated name is shown
        composeTestRule.onNodeWithText("Jane Smith").assertIsDisplayed()
    }
}
