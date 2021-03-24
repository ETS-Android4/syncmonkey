package com.chesapeaketechnology.syncmonkey.tests.settingspage;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.chesapeaketechnology.syncmonkey.SyncMonkeyConstants;
import com.chesapeaketechnology.syncmonkey.TestBase;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeyHomeScreen;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeySettingsScreen;

import net.grandcentrix.tray.core.ItemNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertWithMessage;

@RunWith(AndroidJUnit4.class)
public class ToggleButtonsTests extends TestBase
{
    /*
        Test Case: MONKEY-T48
     */
    @Test
    public void toggleWifiOnlyUploadButtonUpdatesSettings() throws ItemNotFoundException
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        assertWithMessage("WiFi is enabled by default")
                .that(SyncMonkeySettingsScreen.isWifiEnabled())
                .isTrue();
        assertWithMessage("Application Preferences for WiFi Only are set to true.")
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_WIFI_ONLY_KEY))
                .isEqualTo("true");
        SyncMonkeySettingsScreen.toggleWifiOnly(false);
        assertWithMessage("WiFi is disabled after toggling the switch.")
                .that(SyncMonkeySettingsScreen.isWifiDisabled())
                .isTrue();
        assertWithMessage("Application Preferences for WiFi Only are set to false.")
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_WIFI_ONLY_KEY))
                .isEqualTo("false");
    }

    /*
        Test Case: MONKEY-T49
    */
    @Test
    public void toggleWifiOnlyUploadButtonUpdatesText()
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        assertWithMessage("WiFi is enabled by default")
                .that(SyncMonkeySettingsScreen.isWifiEnabled())
                .isTrue();
        assertWithMessage("Expected message is displayed when WiFi Only is enabled.")
                .that(SyncMonkeySettingsScreen.assertSummaryText("Upload files only over Wi-Fi"))
                .isTrue();
        SyncMonkeySettingsScreen.toggleWifiOnly(false);
        assertWithMessage("WiFi is disabled after toggling the switch.")
                .that(SyncMonkeySettingsScreen.isWifiDisabled()).isTrue();
        assertWithMessage("Expected message is displayed when WiFi Only is disabled.")
                .that(SyncMonkeySettingsScreen.assertSummaryText("Upload files over Wi-Fi and Cellular"))
                .isTrue();
    }

    /*
        Test Case: MONKEY-T50
    */
    @Test
    public void toggleVpnOnlyUploadButtonUpdatesSettings() throws ItemNotFoundException
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        assertWithMessage("VPN Only Sync is disabled by default.")
                .that(SyncMonkeySettingsScreen.isVpnDisabled())
                .isTrue();
        assertWithMessage("Application Preferences for VPN Only are set to false.")
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_VPN_ONLY_KEY))
                .isEqualTo("false");
        SyncMonkeySettingsScreen.toggleVpnOnly(true);
        assertWithMessage("VPN Only Sync is enabled after being toggled.")
                .that(SyncMonkeySettingsScreen.isVpnEnabled())
                .isTrue();
        assertWithMessage("Application Preferences for VPN Only are set to true")
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_VPN_ONLY_KEY))
                .isEqualTo("true");
    }

    /*
        Test Case: MONKEY-T51
    */
    @Test
    public void toggleVpnOnlyUploadButtonUpdatesText()
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        assertWithMessage("VPN Only Sync is disabled by default.")
                .that(SyncMonkeySettingsScreen.isVpnDisabled())
                .isTrue();
        assertWithMessage("Expected message is displayed when VPN Only is disabled.")
                .that(SyncMonkeySettingsScreen.assertSummaryText("Upload files regardless of a active VPN connection"))
                .isTrue();
        SyncMonkeySettingsScreen.toggleVpnOnly(true);
        assertWithMessage("VPN Only Sync is enabled after being toggled.")
                .that(SyncMonkeySettingsScreen.isVpnEnabled())
                .isTrue();
        assertWithMessage("Expected message is displayed when VPN Only is enabled.")
                .that(SyncMonkeySettingsScreen.assertSummaryText("Upload files only when a VPN connection is active"))
                .isTrue();
    }

    /*
        Test Case: MONKEY-T52
    */
    @Test
    public void toggleAutoSyncButtonUpdatesSettings() throws ItemNotFoundException
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        assertWithMessage("Auto Sync is enabled by default.")
                .that(SyncMonkeySettingsScreen.isAutoSyncEnabled())
                .isTrue();
        assertWithMessage("Application Preferences for Auto Sync are set to true.")
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_AUTO_SYNC_KEY))
                .isEqualTo("true");
        SyncMonkeySettingsScreen.toggleAutoSync(false);
        assertWithMessage("Auto Sync is disabled after being toggled.")
                .that(SyncMonkeySettingsScreen.isAutoSyncDisabled())
                .isTrue();
        assertWithMessage("Application Preferences for Auto Sync are set to false.")
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_AUTO_SYNC_KEY))
                .isEqualTo("false");
    }

    /*
        Test Case: MONKEY-T53
    */
    @Test
    public void toggleAutoSyncButtonUpdatesText()
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        assertWithMessage("Auto Sync is enabled by default.")
                .that(SyncMonkeySettingsScreen.isAutoSyncEnabled())
                .isTrue();
        assertWithMessage("Expected message is displayed when Auto Sync is enabled.")
                .that(SyncMonkeySettingsScreen.assertSummaryText("Files will be synced automatically"))
                .isTrue();
        SyncMonkeySettingsScreen.toggleAutoSync(false);
        assertWithMessage("Auto Sync is disabled after being toggled.")
                .that(SyncMonkeySettingsScreen.isAutoSyncDisabled())
                .isTrue();
        assertWithMessage("Expected message is displayed when Auto Sync is disabled.")
                .that(SyncMonkeySettingsScreen.assertSummaryText("Manual upload is required to sync files"))
                .isTrue();
    }
}
