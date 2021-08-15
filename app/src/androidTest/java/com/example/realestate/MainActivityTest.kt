package com.example.realestate

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.realestate.ui.mainActivity.MainActivity
import com.example.realestate.utils.Utils
import junit.framework.AssertionFailedError
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.jetbrains.annotations.NotNull
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var mActivityRule:ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)
    private lateinit var mActivity: MainActivity

    @Before
    fun setUp() {
        mActivity = mActivityRule.activity
        assertThat(mActivity, notNullValue())
    }

    @Test
    fun getConnection() {
        Assert.assertEquals(true, Utils.isInternetAvailable(InstrumentationRegistry.getContext()))
        onView(withId(R.id.btnWifi)).perform(ViewActions.click())
        if (Utils.isInternetAvailable(InstrumentationRegistry.getContext())) {
            onView(withText("Connexion disponible")).inRoot(withDecorView(not(mActivity.window.decorView)))
                .check(matches(isDisplayed()))
        } else {
            onView(withText("Connexion indisponible")).inRoot(withDecorView(not(mActivity.window.decorView)))
                .check(matches(isDisplayed()))
        }
    }

}







