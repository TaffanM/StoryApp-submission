package com.taffan.storyapp.ui.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.taffan.storyapp.idling.LoginIdlingResource
import com.taffan.storyapp.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    private val validEmail = "taffanm@gmail.com"
    private val validPassword = "12345678"

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
        IdlingRegistry.getInstance().register(LoginIdlingResource.countingIdlingResource)
    }

    @After
    fun teardown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(LoginIdlingResource.countingIdlingResource)
    }

    @Test
    fun shouldLoginAndLogoutSuccessfully() {
        onView(withId(R.id.ed_login_email)).perform(typeText(validEmail), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password)).perform(typeText(validPassword), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        intended(hasComponent(MainActivity::class.java.name))

        onView(withId(R.id.settings)).perform(click())

        onView(withText(R.string.logout)).perform(click())

        intended(hasComponent(LoginActivity::class.java.name))
    }
}



