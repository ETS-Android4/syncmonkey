package com.chesapeaketechnology.syncmonkey.tests.homepage;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.chesapeaketechnology.syncmonkey.R;
import com.chesapeaketechnology.syncmonkey.TestBase;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeyHomeScreen;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeySettingsScreen;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertWithMessage;

@RunWith(AndroidJUnit4.class)
public class HomePageTests extends TestBase
{
    /*
        Test Case: MONKEY-T47
     */
    @Test
    public void assertHomePageContent()
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        assertWithMessage("AutoSync is enabled by default")
                .that(SyncMonkeySettingsScreen.isAutoSyncEnabled())
                .isTrue();
        SyncMonkeySettingsScreen.clickBackArrow();
        assertWithMessage("The default sync message on the home screen matches the expected value.")
                .that(SyncMonkeyHomeScreen.assertApplicationTitle(getString(R.string.sync_button_auto_upload_description)))
                .isTrue();
    }

    /*
        Test Case: MONKEY-T46
     */
    @Test
    public void homePageContentIsUpdatedWhenAutoSyncDisabled()
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        assertWithMessage("AutoSync is enabled by default")
                .that(SyncMonkeySettingsScreen.isAutoSyncEnabled())
                .isTrue();
        SyncMonkeySettingsScreen.toggleAutoSync(false);
        assertWithMessage("AutoSync is disabled after toggling the switch.")
                .that(SyncMonkeySettingsScreen.isAutoSyncDisabled())
                .isTrue();
        SyncMonkeySettingsScreen.clickBackArrow();
        assertWithMessage("Home screen message is updated when AutoSync is disabled,")
                .that(SyncMonkeyHomeScreen.assertApplicationTitle(getString(R.string.sync_button_manual_upload_description)))
                .isTrue();
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.toggleAutoSync(true);
        assertWithMessage("AutoSync is enabled by default")
                .that(SyncMonkeySettingsScreen.isAutoSyncEnabled())
                .isTrue();
    }
}
