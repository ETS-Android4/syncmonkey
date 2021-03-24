package com.chesapeaketechnology.syncmonkey.screens;

import android.util.Log;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;

import com.chesapeaketechnology.syncmonkey.R;
import com.chesapeaketechnology.syncmonkey.helpers.ChildAtPosition;
import com.chesapeaketechnology.syncmonkey.helpers.SettingsPagePositions;
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions;

import org.hamcrest.core.IsInstanceOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.chesapeaketechnology.syncmonkey.helpers.SetSwitchToggleChecked.setChecked;
import static com.chesapeaketechnology.syncmonkey.matchers.ListViewMatcher.withIndex;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.clearText;
import static com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo;
import static org.hamcrest.Matchers.allOf;

public class SyncMonkeySettingsScreen
{
    private static final String LOG_TAG = SyncMonkeySettingsScreen.class.getSimpleName();

    public static ViewInteraction getRecyclerView()
    {
        return onView(
                allOf(withId(R.id.recycler_view),
                        ChildAtPosition.childAtPosition(
                                withId(android.R.id.list_container),
                                0)));
    }

    private static void clickIntoRecyclerPositionedChild(Integer position)
    {
        try
        {
            onView(
                    allOf(withId(R.id.recycler_view),
                            ChildAtPosition.childAtPosition(
                                    withId(android.R.id.list_container),
                                    0)))
                    .perform(actionOnItemAtPosition(position, click()));
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "No recycler view child at position " + position + " found.");
            throw e;
        }
    }

    public static void setSwitchToggle(Integer position, Boolean booleanValue)
    {
        try
        {
            onView(withIndex(withId(R.id.switchWidget), position)).perform(setChecked(booleanValue));
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Unable to find a switch at position " + position + ".");
            throw e;
        }
    }

    public static Boolean isWifiEnabled()
    {
        try
        {
            onView(withIndex(withId(R.id.switchWidget), SettingsPagePositions.WIFI_ONLY_TOGGLE_POSITION.getValue()))
                    .check(matches(isChecked()));
            return true;
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "WiFi switch widget was not checked.");
            return false;
        }
    }

    public static Boolean isWifiDisabled()
    {
        try
        {
            onView(withIndex(withId(R.id.switchWidget), SettingsPagePositions.WIFI_ONLY_TOGGLE_POSITION.getValue()))
                    .check(matches(isNotChecked()));
            return true;
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "WiFi switch widget was not able to be found or was checked.");
            return false;
        }
    }

    public static Boolean isVpnEnabled()
    {
        try
        {
            onView(withIndex(withId(R.id.switchWidget), SettingsPagePositions.VPN_ONLY_POSITION.getValue()))
                    .check(matches(isChecked()));
            return true;
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "VPN switch widget was not able to be found or was not checked.");
            return false;
        }
    }

    public static Boolean isVpnDisabled()
    {
        try
        {
            onView(withIndex(withId(R.id.switchWidget), SettingsPagePositions.VPN_ONLY_POSITION.getValue()))
                    .check(matches(isNotChecked()));
            return true;
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "VPN switch widget was not able to be found or was checked.");
            return false;
        }
    }

    public static void toggleWifiOnly(Boolean booleanValue)
    {
        try
        {
            setSwitchToggle(SettingsPagePositions.WIFI_ONLY_TOGGLE_POSITION.getValue(), booleanValue);
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "WiFi switch widget was not able to be found.");
            throw e;
        }
    }

    public static void toggleVpnOnly(Boolean booleanValue)
    {
        try
        {
            setSwitchToggle(SettingsPagePositions.VPN_ONLY_POSITION.getValue(), booleanValue);
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "VPN Only switch widget was not able to be found.");
            throw e;
        }
    }

    public static Boolean isAutoSyncEnabled()
    {
        try
        {
            onView(withIndex(withId(R.id.switchWidget), SettingsPagePositions.AUTO_SYNC_ENABLED_POSITION.getValue()))
                    .check(matches(isChecked()));
            return true;
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Auto Sync switch widget was not able to be found or was not checked.");
            return false;
        }
    }

    public static Boolean isAutoSyncDisabled()
    {
        try
        {
            onView(withIndex(withId(R.id.switchWidget), SettingsPagePositions.AUTO_SYNC_ENABLED_POSITION.getValue()))
                    .check(matches(isNotChecked()));
            return true;
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Auto Sync switch widget was not able to be found or was checked.");
            return false;
        }
    }

    public static void toggleAutoSync(Boolean booleanValue)
    {
        try
        {
            setSwitchToggle(SettingsPagePositions.AUTO_SYNC_ENABLED_POSITION.getValue(), booleanValue);
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Auto Sync switch widget was not able to be found.");
            throw e;
        }
    }

    public static void clickBackArrow()
    {
        try
        {
            onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Navigate back arrow was not able to be found.");
            throw e;
        }
    }

    public static Boolean assertSummaryText(String message)
    {
        try
        {
            onView(
                    allOf(withId(android.R.id.summary), withText(message),
                            withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout.class))),
                            isDisplayed()));
            return true;
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Summary text with the message " + message + " was not able to be found.");
            return false;
        }
    }

    public static void expandServerConfig()
    {
        try
        {
            onView(
                    allOf(withId(R.id.recycler_view),
                            ChildAtPosition.childAtPosition(
                                    withId(android.R.id.list_container),
                                    0)))
                    .perform(actionOnItemAtPosition(4, click()))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "The expand server configuration button was not found.");
            throw e;
        }
    }

    public static void setDeviceId(String deviceId)
    {
        try
        {
            clickIntoRecyclerPositionedChild(SettingsPagePositions.DEVICE_ID_POSITION.getValue());
            enterTextIntoEditField(deviceId);
            clickOkConfirmationButton();
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Error setting the deviceId to " + deviceId);
            throw e;
        }
    }

    private static void enterTextIntoEditField(String message)
    {
        try
        {
            clearText(android.R.id.edit);
            writeTo(android.R.id.edit, message);
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Error entering the text " + message + " into the edit field.");
            throw e;
        }
    }

    private static void clickOkConfirmationButton()
    {
        clickOn("OK");
    }

    public static void setContainerName(String containerName)
    {
        try
        {
            clickIntoRecyclerPositionedChild(SettingsPagePositions.CONTAINER_NAME_POSITION.getValue());
            enterTextIntoEditField(containerName);
            clickOkConfirmationButton();
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Error setting the container name to " + containerName + ".");
            throw e;
        }
    }

    public static void setSasUrl(String sasUrl)
    {
        try
        {
            clickIntoRecyclerPositionedChild(SettingsPagePositions.SAS_URL_POSITION.getValue());
            enterTextIntoEditField(sasUrl);
            clickOkConfirmationButton();
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Error setting the SAS URL to " + sasUrl + ".");
            throw e;
        }
    }

    public static void setSyncDirectories(String syncDirectories)
    {
        try
        {
            clickIntoRecyclerPositionedChild(SettingsPagePositions.SYNC_DIRECTORIES_POSITION.getValue());
            enterTextIntoEditField(syncDirectories);
            BaristaSleepInteractions.sleep(50); // For whatever reason this delay prevented this test from failing on a Samsung S20
            clickOkConfirmationButton();
        } catch (NoMatchingViewException e)
        {
            Log.e(LOG_TAG, "Error setting the sync directories to " + syncDirectories + ".");
            throw e;
        }
    }
}
