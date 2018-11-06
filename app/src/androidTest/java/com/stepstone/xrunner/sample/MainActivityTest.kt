package com.stepstone.xrunner.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun dummy_text_should_be_hidden_by_default() {
        onView(withId(R.id.dummy_text_view)).check(matches(not(isDisplayed())))
    }

    @Test
    @Ignore
    fun fab_should_be_visible() {
        onView(withId(R.id.fab)).check(matches(isDisplayed()))
    }

    @Test
    fun clicking_fab_should_show_dummy_text() {
        // when
        onView(withId(R.id.fab)).perform(click())

        // then
        onView(withId(R.id.dummy_text_view)).check(matches(withText("XRunner")))
    }

    @Test
    fun failing_test() {
        assertTrue(false)
    }
}
