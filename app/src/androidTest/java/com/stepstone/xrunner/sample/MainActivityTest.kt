package com.stepstone.xrunner.sample

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
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
