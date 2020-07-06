package com.chesapeaketechnology.syncmonkey.settings;

import android.content.Context;
import android.content.RestrictionsManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.chesapeaketechnology.syncmonkey.R;
import com.chesapeaketechnology.syncmonkey.SyncMonkeyConstants;
import com.chesapeaketechnology.syncmonkey.SyncMonkeyMainActivity;

import net.grandcentrix.tray.AppPreferences;
import net.grandcentrix.tray.core.ItemNotFoundException;

import java.util.HashSet;

/**
 * A Settings Fragment to inflate the Preferences XML resource so the user can interact with the App's settings.
 *
 * @since 0.0.4
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    /**
     * Preferences that we allow to be overridden by the user if already configured via MDM.
     *
     * @since 0.1.3
     */
    private static final HashSet<String> OVERRIDABLE_PREFERENCES = new HashSet<>();

    private AppPreferences appPreferences;

    static
    {
        OVERRIDABLE_PREFERENCES.add(SyncMonkeyConstants.PROPERTY_WIFI_ONLY_KEY);
        OVERRIDABLE_PREFERENCES.add(SyncMonkeyConstants.PROPERTY_VPN_ONLY_KEY);
        OVERRIDABLE_PREFERENCES.add(SyncMonkeyConstants.PROPERTY_AUTO_SYNC_KEY);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        // Inflate the preferences XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);
        appPreferences = new AppPreferences(requireContext());
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        updateUiForMdmIfNecessary();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Log.d(LOG_TAG, "Shared Preference changed for key=" + key);

        // Whenever the share preferences are updated, copy them over to the Tray Preferences
        switch (key)
        {
            case SyncMonkeyConstants.PROPERTY_MDM_OVERRIDE_KEY:
                final boolean mdmOverride = sharedPreferences.getBoolean(key, false);
                appPreferences.put(key, mdmOverride);

                Log.d(LOG_TAG, "mdmOverride Preference Changed to " + mdmOverride);

                updateOverrideState(mdmOverride);
                if (!mdmOverride)
                {
                    // Since we are removing the override, we need to read in the MDM provided preferences to the appPreferences
                    final Context context = getContext();
                    if (context != null)
                    {
                        SyncMonkeyMainActivity.readSyncMonkeyManagedConfiguration(context, appPreferences);
                    }
                    updateUiForMdmIfNecessary();
                }
                break;

            case SyncMonkeyConstants.PROPERTY_AUTO_SYNC_KEY:
            case SyncMonkeyConstants.PROPERTY_VPN_ONLY_KEY:
            case SyncMonkeyConstants.PROPERTY_WIFI_ONLY_KEY:
                appPreferences.put(key, sharedPreferences.getBoolean(key, true));
                break;

            case SyncMonkeyConstants.PROPERTY_AZURE_SAS_URL_KEY:
            case SyncMonkeyConstants.PROPERTY_CONTAINER_NAME_KEY:
            case SyncMonkeyConstants.PROPERTY_DEVICE_ID_KEY:
            case SyncMonkeyConstants.PROPERTY_LOCAL_SYNC_DIRECTORIES_KEY:
                appPreferences.put(key, sharedPreferences.getString(key, ""));
                break;

            default:
                Log.wtf(LOG_TAG, "A User Preference changed, but there was not a mapping for it to set it in the Tray Preferences, key=" + key);
        }
    }

    /**
     * @return True if this app is under MDM control (aka the SAS_URL is set via the MDM server).
     * @since 0.1.3
     */
    private boolean isUnderMdmControl()
    {
        final RestrictionsManager restrictionsManager = (RestrictionsManager) requireContext().getSystemService(Context.RESTRICTIONS_SERVICE);
        if (restrictionsManager != null)
        {
            final Bundle mdmProperties = restrictionsManager.getApplicationRestrictions();

            if (!mdmProperties.getString(SyncMonkeyConstants.PROPERTY_AZURE_SAS_URL_KEY, "").isEmpty())
            {
                Log.i(LOG_TAG, "Sync Monkey is under MDM control");
                return true;
            }
        }

        return false;
    }

    /**
     * If the app is under MDM control, update the user preferences UI to reflect those MDM provided values. If the app
     * is not under MDM control, then do nothing.
     * <p>
     * Also, we need to check if the user has turned on the MDM override option. If so, then some of the values can
     * still be changed. If not, then we should disable all settings but still update the values so that the UI reflects
     * the MDM provided values.
     *
     * @since 0.1.3
     */
    private void updateUiForMdmIfNecessary()
    {
        if (!isUnderMdmControl()) return;

        final SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

        // Update the UI so that the MDM override is visible, and that some of the settings can't be changed
        final Preference overridePreference = getPreferenceScreen().findPreference(SyncMonkeyConstants.PROPERTY_MDM_OVERRIDE_KEY);
        if (overridePreference != null) overridePreference.setVisible(true);

        final boolean mdmOverride = sharedPreferences.getBoolean(SyncMonkeyConstants.PROPERTY_MDM_OVERRIDE_KEY, false);

        final PreferenceScreen preferenceScreen = getPreferenceScreen();

        updateSwitchPreferenceForMdm(preferenceScreen, SyncMonkeyConstants.PROPERTY_AUTO_SYNC_KEY, mdmOverride);
        updateSwitchPreferenceForMdm(preferenceScreen, SyncMonkeyConstants.PROPERTY_VPN_ONLY_KEY, mdmOverride);
        updateSwitchPreferenceForMdm(preferenceScreen, SyncMonkeyConstants.PROPERTY_WIFI_ONLY_KEY, mdmOverride);

        updateEditTextPreferenceForMdm(preferenceScreen, SyncMonkeyConstants.PROPERTY_AZURE_SAS_URL_KEY, mdmOverride);
        updateEditTextPreferenceForMdm(preferenceScreen, SyncMonkeyConstants.PROPERTY_CONTAINER_NAME_KEY, mdmOverride);
        updateEditTextPreferenceForMdm(preferenceScreen, SyncMonkeyConstants.PROPERTY_DEVICE_ID_KEY, mdmOverride);
        updateEditTextPreferenceForMdm(preferenceScreen, SyncMonkeyConstants.PROPERTY_LOCAL_SYNC_DIRECTORIES_KEY, mdmOverride);
    }

    /**
     * Update the preferences that can be overridden by the user as either enabled or disabled based on the MDM override
     * setting.
     *
     * @param mdmOverride True if the user has overridden the MDM settings, false otherwise.
     * @since 0.1.3
     */
    private void updateOverrideState(boolean mdmOverride)
    {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        //noinspection ConstantConditions
        OVERRIDABLE_PREFERENCES.forEach(key -> preferenceScreen.findPreference(key).setEnabled(mdmOverride));
    }

    /**
     * Updates the UI preference to reflect MDM control by disabling the UI preference component and pulling the
     * specified preference value from the app preferences.
     * <p>
     * However, this only occurs if the preference is not being overridden via the mdmOverride user option.
     *
     * @param preferenceScreen The preference screen that contains the preference to set.
     * @param preferenceKey    The key that corresponds to the preference of interest.
     * @param mdmOverride      If true, then the preference UI component will only be disabled and updated if the
     *                         preference is not in the overridable list.
     * @since 0.1.4
     */
    private void updateSwitchPreferenceForMdm(PreferenceScreen preferenceScreen, String preferenceKey, boolean mdmOverride)
    {
        if (mdmOverride && OVERRIDABLE_PREFERENCES.contains(preferenceKey))
        {
            Log.d(LOG_TAG, "Skipping updating a preference because of the MDM override, key=" + preferenceKey);
            return;
        }

        try
        {
            final SwitchPreferenceCompat preference = preferenceScreen.findPreference(preferenceKey);
            //noinspection ConstantConditions
            preference.setEnabled(false);
            preference.setChecked(appPreferences.getBoolean(preferenceKey));
        } catch (ItemNotFoundException | NullPointerException e)
        {
            Log.wtf(LOG_TAG, "Could not find the boolean Tray preference or update the UI component for " + preferenceKey, e);
        }
    }

    /**
     * Updates the UI preference to reflect MDM control by disabling the UI preference component and pulling the
     * specified preference value from the app preferences.
     * <p>
     * However, this only occurs if the preference is not being overridden via the mdmOverride user option.
     *
     * @param preferenceScreen The preference screen that contains the preference to set.
     * @param preferenceKey    The key that corresponds to the preference of interest.
     * @param mdmOverride      If true, then the preference UI component will only be disabled and updated if the
     *                         preference is not in the overridable list.
     * @since 0.1.4
     */
    private void updateEditTextPreferenceForMdm(PreferenceScreen preferenceScreen, String preferenceKey, boolean mdmOverride)
    {
        if (mdmOverride && OVERRIDABLE_PREFERENCES.contains(preferenceKey))
        {
            Log.d(LOG_TAG, "Skipping updating a preference because of the MDM override, key=" + preferenceKey);
            return;
        }

        try
        {
            final EditTextPreference preference = preferenceScreen.findPreference(preferenceKey);
            //noinspection ConstantConditions
            preference.setEnabled(false);
            preference.setText(appPreferences.getString(preferenceKey));
        } catch (ItemNotFoundException | NullPointerException e)
        {
            Log.wtf(LOG_TAG, "Could not find the String Tray preference or update the UI component for " + preferenceKey, e);
        }
    }
}
