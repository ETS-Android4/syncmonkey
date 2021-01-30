package com.chesapeaketechnology.syncmonkey.screens;

import android.view.View;

import androidx.test.espresso.ViewInteraction;

import com.chesapeaketechnology.syncmonkey.EspressoBase;
import com.chesapeaketechnology.syncmonkey.R;

import org.hamcrest.core.IsInstanceOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.chesapeaketechnology.syncmonkey.TestUtils.childAtPosition;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static org.hamcrest.Matchers.allOf;


public class SyncMonkeyHomeScreen extends EspressoBase {

    public static final Integer settingsGear = R.id.action_settings;
    public static final Integer homePageDescription = R.id.sync_button_description;

    public static ViewInteraction getSettingsGear() {
        return onView(
                withId(R.id.action_settings));
    }

    public static ViewInteraction getSyncNowButton() {
        return onView(
                allOf(withId(R.id.button), withText("Sync Now"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
    }

    public static ViewInteraction getHomePageDescription() {
        return onView(
                allOf(withId(R.id.sync_button_description),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
    }

    public static ViewInteraction getExpirationMessage() {
        return onView(
                allOf(withId(R.id.expiration_message),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
    }

    public static ViewInteraction getApplicationTitle() {
        return onView(
                allOf(withText("Sync Monkey"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
    }

    public static void clickSettingsGear() {
        clickOn(R.id.action_settings);
    }

    public static void clickSyncNow() {
        clickOn("Sync Now");
    }

}
