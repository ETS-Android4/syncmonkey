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
        // Whenever the share preferences are updated, copy them over to the Tray Preferences
        switch (key)
        {
            case SyncMonkeyConstants.PROPERTY_MDM_OVERRIDE_KEY:
                final boolean mdmOverride = sharedPreferences.getBoolean(key, false);
                appPreferences.put(key, mdmOverride);

                updateOverrideState(mdmOverride);
                if (!mdmOverride)
                {
                    // Since we are removing the override, we need to read in the MDM provided preferences to the appPreferences
                    SyncMonkeyMainActivity.readSyncMonkeyManagedConfiguration(requireContext(), appPreferences);
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
                Log.wtf(LOG_TAG, "A User Preference changed, but there was not a mapping for it to set it in the Tray Preferences");
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
        final Preference preference = getPreferenceScreen().findPreference(SyncMonkeyConstants.PROPERTY_MDM_OVERRIDE_KEY);
        if (preference != null) preference.setVisible(true);

        final boolean mdmOverride = sharedPreferences.getBoolean(SyncMonkeyConstants.PROPERTY_MDM_OVERRIDE_KEY, false);

        final PreferenceScreen prefScreen = getPreferenceScreen();
        int prefCount = prefScreen.getPreferenceCount();

        for (int i = 0; i < prefCount; i++)
        {
            final Preference pref = prefScreen.getPreference(i);
            final String key = pref.getKey();

            // We don't need to enable/disable or update the MDM override option, so skip past it.
            if (key.equals(SyncMonkeyConstants.PROPERTY_MDM_OVERRIDE_KEY)) continue;

            if (!mdmOverride || !OVERRIDABLE_PREFERENCES.contains(key))
            {
                pref.setEnabled(false);
                try
                {
                    switch (key)
                    {
                        case SyncMonkeyConstants.PROPERTY_AUTO_SYNC_KEY:
                        case SyncMonkeyConstants.PROPERTY_VPN_ONLY_KEY:
                        case SyncMonkeyConstants.PROPERTY_WIFI_ONLY_KEY:
                            //noinspection ConstantConditions
                            ((SwitchPreferenceCompat) prefScreen.findPreference(key)).setChecked(appPreferences.getBoolean(pref.getKey()));
                            break;

                        case SyncMonkeyConstants.PROPERTY_AZURE_SAS_URL_KEY:
                        case SyncMonkeyConstants.PROPERTY_CONTAINER_NAME_KEY:
                        case SyncMonkeyConstants.PROPERTY_DEVICE_ID_KEY:
                        case SyncMonkeyConstants.PROPERTY_LOCAL_SYNC_DIRECTORIES_KEY:
                            //noinspection ConstantConditions
                            ((EditTextPreference) prefScreen.findPreference(key)).setText(appPreferences.getString(pref.getKey()));
                            break;

                        default:
                            Log.wtf(LOG_TAG, "A User Preference changed, but there was not a mapping for it to set it in the Tray Preferences");
                    }
                } catch (ItemNotFoundException | NullPointerException e)
                {
                    Log.wtf(LOG_TAG, "Could not find the Tray preference for " + key);
                }
            }
        }
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
}
