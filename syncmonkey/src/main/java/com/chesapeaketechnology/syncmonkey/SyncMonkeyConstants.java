package com.chesapeaketechnology.syncmonkey;

import android.content.Intent;

/**
 * Some constants used in the App.
 *
 * @since 0.0.1
 */
@SuppressWarnings("WeakerAccess")
public class SyncMonkeyConstants
{
    private SyncMonkeyConstants()
    {
    }

    public static final String PRIVATE_SHARED_SYNC_DIRECTORY = "sharedfiles";
    public static final String DEFAULT_SHARED_TEXT_FILE_NAME = "Text_To_Share.txt";
    public static final String SYNC_MONKEY_PROPERTIES_FILE = "syncmonkey.properties";
    public static final String ANDROID_AD_ID_FILE = "android_advertisement_id.txt";
    public static final String NO_SAS_URL_WARNING = "No SAS URL set";

    /**
     * A custom action that other apps can use to trigger a sync in the Sync Monkey App.  Sending an intent with this
     * action will kick off an upload of the content of the local directories to the remote system.
     *
     * @since 0.1.1
     */
    public static final String ACTION_SYNC_NOW = "com.chesapeaketechnology.sycnmonkey.action.SYNC_NOW";

    /**
     * A custom action that other apps can use to bypass the sharing screen and send a single file in the form of a
     * parcelable extra {@link Intent#EXTRA_STREAM}.
     *
     * @since 0.1.0
     */
    public static final String ACTION_SEND_FILE_NO_UI = "com.chesapeaketechnology.sycnmonkey.action.SEND_FILE_NO_UI";

    /**
     * A custom action that other apps can use to bypass the sharing screen and send multiple files in the form of a
     * parcelable array list extra {@link Intent#EXTRA_STREAM}.
     *
     * @since 0.1.0
     */
    public static final String ACTION_SEND_MULTIPLE_FILE_NO_UI = "com.chesapeaketechnology.sycnmonkey.action.SEND_MULTIPLE_FILE_NO_UI";

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.chesapeaketechnology.sycnmonkey.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.rfmonkey";
    // The account name
    public static final String ACCOUNT = "dummyaccount";

    public static final int SECONDS_IN_HOUR = 3600;
    public static final String COLON_SEPARATOR = ":";

    public static final String AZURE_CONFIG_NAME = "azureconfig";
    public static final String AZURE_REMOTE_TYPE = "azureblob";

    // User Preference keys and MDM managed configuration keys
    public static final String PROPERTY_MDM_OVERRIDE_KEY = "mdmOverride";
    public static final String PROPERTY_CONTAINER_NAME_KEY = "containerName";
    public static final String PROPERTY_AZURE_SAS_URL_KEY = "sas_url";
    public static final String PROPERTY_LOCAL_SYNC_DIRECTORIES_KEY = "localSyncDirectories";
    public static final String PROPERTY_DEVICE_ID_KEY = "deviceId";
    public static final String PROPERTY_AUTO_SYNC_KEY = "autoSync";
    public static final String PROPERTY_VPN_ONLY_KEY = "vpnOnly";
    public static final String PROPERTY_WIFI_ONLY_KEY = "wifiOnly";

    public static final String DEFAULT_DEVICE_ID = "UnknownDeviceId";

    // Tray Preferences used for the syncing status
    public static final String TRAY_STATUS_MODULE = "sync_monkey_sync_status_module";
    public static final String STATUS_PROPERTY_LAST_SUCCESSFUL_TIME_KEY = "status_last_success";
    public static final String STATUS_PROPERTY_LAST_SYNC_STATUS_KEY = "status_last_sync_status";
}
