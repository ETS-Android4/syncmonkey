package com.chesapeaketechnology.syncmonkey;

import androidx.test.espresso.ViewInteraction;

import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeyHomeScreen;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static org.hamcrest.Matchers.allOf;

public class TestSuite extends TestBase {

    @Test
    public void clickSettingsButton() {
        SyncMonkeyHomeScreen.clickSettingsGear();
    }

    @Test
    public void clickSyncNow(){
        SyncMonkeyHomeScreen.clickSyncNow();
    }

    @Test
    public void assertHomePageContent() {
        onView(
                allOf(withId(R.id.sync_button_description),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()))
        .check(matches(withText("All files will be automatically synced at a scheduled interval. Use the Sync Button below to force a sync.")));
    }
}
