package com.chesapeaketechnology.syncmonkey.screens;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;

import com.chesapeaketechnology.syncmonkey.R;
import com.chesapeaketechnology.syncmonkey.helpers.ChildAtPosition;

import org.hamcrest.core.IsInstanceOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static org.hamcrest.Matchers.allOf;

public class SyncMonkeyHomeScreen
{
    public static final Integer settingsGear = R.id.action_settings;
    public static final Integer homePageDescription = R.id.sync_button_description;

    public static ViewInteraction getSettingsGear()
    {
        return onView(
                withId(R.id.action_settings));
    }

    public static ViewInteraction getSyncNowButton()
    {
        return onView(
                allOf(withId(R.id.button), withText("Sync Now"),
                        ChildAtPosition.childAtPosition(
                                ChildAtPosition.childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
    }

    public static ViewInteraction getHomePageDescription()
    {
        return onView(
                allOf(withId(R.id.sync_button_description),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
    }

    public static ViewInteraction getExpirationMessage()
    {
        return onView(
                allOf(withId(R.id.expiration_message),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
    }

    public static Boolean assertApplicationTitle(String message)
    {
        try
        {
            onView(
                    allOf(withId(R.id.sync_button_description),
                            isDisplayed()))
                    .check(matches(withText(message)));
            return true;
        } catch (NoMatchingViewException e)
        {
            return false;
        }
    }

    public static void clickSettingsGear()
    {
        clickOn(R.id.action_settings);
    }

    public static void clickSyncNow()
    {
        clickOn("Sync Now");
    }
}
