package com.chesapeaketechnology.syncmonkey.tests.homepage;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.chesapeaketechnology.syncmonkey.TestBase;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeyHomeScreen;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeySettingsScreen;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.*;

@RunWith(AndroidJUnit4.class)
public class HomePageTests extends TestBase
{

    @Test
    public void assertHomePageContent()
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        assertWithMessage("AutoSync is enabled by default")
                .that(SyncMonkeySettingsScreen.isAutoSyncEnabled())
                .isTrue();
        SyncMonkeySettingsScreen.clickBackArrow();
        assertWithMessage("The default sync message on the home screen matches the expected value.")
                .that(SyncMonkeyHomeScreen.assertApplicationTitle("All files will be automatically synced at a scheduled interval. Use the Sync Button below to force a sync."))
                .isTrue();
    }

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
                .that(SyncMonkeyHomeScreen.assertApplicationTitle("Auto upload is disabled. Syncing will only occur using the Sync Button below."))
                .isTrue();
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.toggleAutoSync(true);
        assertWithMessage("AutoSync is enabled by default")
                .that(SyncMonkeySettingsScreen.isAutoSyncEnabled())
                .isTrue();
    }
}
