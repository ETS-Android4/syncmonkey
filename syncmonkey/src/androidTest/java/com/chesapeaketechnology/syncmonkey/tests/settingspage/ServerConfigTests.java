package com.chesapeaketechnology.syncmonkey.tests.settingspage;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.chesapeaketechnology.syncmonkey.SyncMonkeyConstants;
import com.chesapeaketechnology.syncmonkey.TestBase;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeyHomeScreen;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeySettingsScreen;

import net.grandcentrix.tray.core.ItemNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.chesapeaketechnology.syncmonkey.helpers.TestUtils.Generate.getRandomNumberUsingInts;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;

@RunWith(AndroidJUnit4.class)
public class ServerConfigTests extends TestBase
{

    /*
        Test Case: MONKEY-T54
    */
    @Test
    public void updateDeviceIdField() throws ItemNotFoundException
    {
        final String deviceId = "test-automation-device-id-" + getRandomNumberUsingInts(1, 99);

        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.expandServerConfig();
        SyncMonkeySettingsScreen.setDeviceId(deviceId);
        SyncMonkeySettingsScreen.clickBackArrow();
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.expandServerConfig();
        assertContains(deviceId);
        assertWithMessage("T-48: Application Preferences for DeviceId are set to " + deviceId)
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_DEVICE_ID_KEY))
                .isEqualTo(deviceId);
    }

    /*
        Test Case: MONKEY-T55
    */
    @Test
    public void updateContainerNameField() throws ItemNotFoundException
    {
        final String containerName = "test-automation-blob-" + getRandomNumberUsingInts(1, 99);
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.expandServerConfig();
        SyncMonkeySettingsScreen.setContainerName(containerName);
        SyncMonkeySettingsScreen.clickBackArrow();
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.expandServerConfig();
        assertContains(containerName);
        assertWithMessage("Application Preferences for Container Name are set to " + containerName)
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_CONTAINER_NAME_KEY))
                .isEqualTo(containerName);
    }

    /*
        Test Case: MONKEY-T56
    */
    @Test
    public void updateSasUrlField() throws ItemNotFoundException
    {
        final String url = "https://localhost/" + getRandomNumberUsingInts(1, 99);
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.expandServerConfig();
        SyncMonkeySettingsScreen.setSasUrl(url);
        SyncMonkeySettingsScreen.clickBackArrow();
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.expandServerConfig();
        assertContains(url);
        assertWithMessage("Application Preferences for Azure SAS URL are set to " + url)
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_AZURE_SAS_URL_KEY))
                .isEqualTo(url);
    }

    /*
        Test Case: MONKEY-T57
    */
    @Test
    public void updateSyncDirectoriesField() throws ItemNotFoundException, InterruptedException
    {
        final String directories = "Download/TestAutomationData:Download/TestAutomationData" + getRandomNumberUsingInts(1, 99);
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.expandServerConfig();
        SyncMonkeySettingsScreen.setSyncDirectories(directories);
        SyncMonkeySettingsScreen.clickBackArrow();
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.expandServerConfig();
        assertContains(directories);
        assertWithMessage("Application Preferences for Sync Directories are set to " + directories)
                .that(getAppPreferences().getString(SyncMonkeyConstants.PROPERTY_LOCAL_SYNC_DIRECTORIES_KEY))
                .isEqualTo(directories);
    }
}
