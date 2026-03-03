package com.example.thredsnp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thredsnp.view.LoginContent
import com.example.thredsnp.view.ui.theme.THREDSNPTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_InitialState_IsCorrect() {
        composeTestRule.setContent {
            THREDSNPTheme {
                LoginContent()
            }
        }

        // Verify Title and Subtitle
        composeTestRule.onNodeWithText("Welcome Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in to continue shopping").assertIsDisplayed()

        // Verify Input Fields via test tags
        composeTestRule.onNodeWithTag("emailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("passwordField").assertIsDisplayed()

        // Verify Buttons
        composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("supplierLoginButton").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").assertIsDisplayed()
    }

    @Test
    fun loginScreen_InputFields_AcceptText() {
        composeTestRule.setContent {
            THREDSNPTheme {
                LoginContent()
            }
        }

        val email = "test@user.com"
        val password = "password123"

        // Type email using tag
        composeTestRule.onNodeWithTag("emailField").performTextInput(email)
        // verify text is in the field
        composeTestRule.onNodeWithText(email).assertExists()

        // Type password using tag
        composeTestRule.onNodeWithTag("passwordField").performTextInput(password)
    }}


