package com.example.thredsnp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thredsnp.view.RegisterContent
import com.example.thredsnp.view.ui.theme.THREDSNPTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registerScreen_InitialState_IsCorrect() {
        composeTestRule.setContent {
            THREDSNPTheme {
                RegisterContent()
            }
        }

        // Verify Title and Subtitle
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign up to start shopping").assertIsDisplayed()

        // Verify all Input Fields via test tags
        composeTestRule.onNodeWithTag("fullNameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("phoneField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirmPasswordField").assertIsDisplayed()

        // Verify Buttons
        composeTestRule.onNodeWithTag("registerButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("loginNavigateButton").assertIsDisplayed()
    }

    @Test
    fun registerScreen_InputFields_AcceptText() {
        composeTestRule.setContent {
            THREDSNPTheme {
                RegisterContent()
            }
        }

        val name = "Swornim Chaudhary"
        val email = "swornim@test.com"
        val phone = "9800000000"
        val password = "password123"

        // Type in all fields
        composeTestRule.onNodeWithTag("fullNameField").performTextInput(name)
        composeTestRule.onNodeWithTag("fullNameField").assertTextContains(name)

        composeTestRule.onNodeWithTag("emailField").performTextInput(email)
        composeTestRule.onNodeWithTag("emailField").assertTextContains(email)

        composeTestRule.onNodeWithTag("phoneField").performTextInput(phone)
        composeTestRule.onNodeWithTag("phoneField").assertTextContains(phone)

        composeTestRule.onNodeWithTag("passwordField").performTextInput(password)
        composeTestRule.onNodeWithTag("confirmPasswordField").performTextInput(password)
        
        // Note: Password text usually isn't asserted directly due to visual transformations,
        // but we verify that the interactions are successful.
    }
}
