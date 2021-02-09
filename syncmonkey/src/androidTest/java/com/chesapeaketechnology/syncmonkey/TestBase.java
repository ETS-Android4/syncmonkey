package com.chesapeaketechnology.syncmonkey;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import com.chesapeaketechnology.syncmonkey.helpers.SettingsPagePositions;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeyHomeScreen;
import com.chesapeaketechnology.syncmonkey.screens.SyncMonkeySettingsScreen;
import net.grandcentrix.tray.AppPreferences;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestBase
{
    private static final String LOG_TAG = TestBase.class.getSimpleName();
    private static AppPreferences appPreferences;

    @Rule
    public ActivityScenarioRule<SyncMonkeyMainActivity> activityScenarioRule = new ActivityScenarioRule<>(SyncMonkeyMainActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE");

    public static AppPreferences getAppPreferences()
    {
        return appPreferences;
    }

    @Before
    public void init()
    {
        appPreferences = new AppPreferences(ApplicationProvider.getApplicationContext());
    }

    /*
        The argument testInstrumentationRunnerArguments clearPackageData: 'true' does not seem to
        actually clear the package data. As a result, the previous run's settings are persisted. The
        intention for this method is to set the toggles back to the known default settings.
     */
    @Before
    public void verifyAppState()
    {
        SyncMonkeyHomeScreen.clickSettingsGear();
        SyncMonkeySettingsScreen.toggleWifiOnly(true);
        SyncMonkeySettingsScreen.setSwitchToggle(SettingsPagePositions.VPN_ONLY_POSITION.getValue(), false);
        SyncMonkeySettingsScreen.setSwitchToggle(SettingsPagePositions.AUTO_SYNC_ENABLED_POSITION.getValue(), true);
        SyncMonkeySettingsScreen.clickBackArrow();
    }
}
