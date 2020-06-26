package com.chesapeaketechnology.syncmonkey.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.chesapeaketechnology.syncmonkey.R;
import com.chesapeaketechnology.syncmonkey.SyncMonkeyConstants;

import net.grandcentrix.tray.AppPreferences;

import java.util.Arrays;
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
     */
    private static final String[] overrideablePreferences = new String[] {
        SyncMonkeyConstants.PROPERTY_AUTO_SYNC_KEY,
        SyncMonkeyConstants.PROPERTY_VPN_ONLY_KEY,
        SyncMonkeyConstants.PROPERTY_WIFI_ONLY_KEY,
    };

    private static HashSet<String> preferencesSetByMdm = new HashSet<>();

    private AppPreferences appPreferences;

    /**
     * Marks preference as having been set by MDM configuration.
     *
     * @param name Name of preference
     */
    public static void markPreferenceAsSetByMdm(String name)
    {
        preferencesSetByMdm.add(name);
    }

    /**
     * SummaryProvider for the preferences overridden by the MDM.
     *
     * @since 0.1.2
     */
    private class PreferenceSummaryProvider implements Preference.SummaryProvider<Preference>
    {
        @Override
        public CharSequence provideSummary(Preference preference)
        {
            String key = preference.getKey();
            String defaultSummary = appPreferences.getString(key, "");
            return "Set by MDM: " + defaultSummary;
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        // Inflate the preferences XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);
        appPreferences = new AppPreferences(requireContext());
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        for (String prefName : preferencesSetByMdm)
        {
            Preference preference = getPreferenceScreen().findPreference(prefName);
            boolean nonOverrideable = Arrays.stream(overrideablePreferences).noneMatch(prefName::equals);

            if (preference != null && nonOverrideable)
            {
                preference.setSummaryProvider(new PreferenceSummaryProvider());
                preference.setEnabled(false);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // Whenever the share preferences are updated, copy them over to the Tray Preferences
        switch (key)
        {
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
}
