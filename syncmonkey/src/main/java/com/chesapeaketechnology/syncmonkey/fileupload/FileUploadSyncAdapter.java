package com.chesapeaketechnology.syncmonkey.fileupload;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.chesapeaketechnology.syncmonkey.SyncMonkeyConstants;
import com.chesapeaketechnology.syncmonkey.SyncMonkeyMainActivity;
import com.chesapeaketechnology.syncmonkey.SyncMonkeyUtils;

import net.grandcentrix.tray.AppPreferences;
import net.grandcentrix.tray.TrayPreferences;
import net.grandcentrix.tray.core.TrayStorage;

import java.io.File;
import java.time.LocalDateTime;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class FileUploadSyncAdapter extends AbstractThreadedSyncAdapter
{
    private static final String LOG_TAG = FileUploadSyncAdapter.class.getSimpleName();

    private final AzureBlob azureBlob;
    private final String dataDirectoryPath;
    private final AppPreferences appPreferences;
    private final TrayPreferences statusInformation;

    /**
     * Set up the sync adapter
     */
    FileUploadSyncAdapter(Context context, boolean autoInitialize)
    {
        this(context, autoInitialize, false);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    private FileUploadSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs)
    {
        super(context, autoInitialize, allowParallelSyncs);

        appPreferences = new AppPreferences(context);
        statusInformation = new TrayPreferences(context, SyncMonkeyConstants.TRAY_STATUS_MODULE, 1, TrayStorage.Type.DEVICE);
        dataDirectoryPath = Environment.getExternalStorageDirectory().getPath() + "/";
        final String sasUrl = appPreferences.getString(SyncMonkeyConstants.PROPERTY_AZURE_SAS_URL_KEY, "");
        if (sasUrl != null && !sasUrl.isEmpty())
        {
            azureBlob = new AzureBlob(sasUrl);
        } else
        {
            Log.w(LOG_TAG, "Did not find the Azure SAS URL Property, syncs will not succeed.");
            azureBlob = null;
        }
    }

    /**
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
    {
        try
        {
            updateSyncStatus("Checking Sync Preferences ...");

            final boolean autoSync = appPreferences.getBoolean(SyncMonkeyConstants.PROPERTY_AUTO_SYNC_KEY, true);
            final boolean expedited = extras.getBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false);
            if (!autoSync && !expedited) return;

            Log.i(LOG_TAG, "Running the SyncMonkey Sync Adapter");

            final boolean transmitOnlyOnVpn = appPreferences.getBoolean(SyncMonkeyConstants.PROPERTY_VPN_ONLY_KEY, true);
            final boolean transmitOnlyOnWiFi = appPreferences.getBoolean(SyncMonkeyConstants.PROPERTY_WIFI_ONLY_KEY, true);

            if (Log.isLoggable(LOG_TAG, Log.INFO))
            {
                Log.i(LOG_TAG, "Wi-Fi Only Upload Preference: " + transmitOnlyOnWiFi);
                Log.i(LOG_TAG, "VPN Only Upload Preference: " + transmitOnlyOnVpn);
            }

            if (transmitOnlyOnWiFi && !isWiFiConnected())
            {
                updateSyncStatus("Skipping upload because Wi-Fi is not connected and the Wi-Fi Only setting is enabled");
                return;
            }

            if (transmitOnlyOnVpn)
            {
                if (isVpnEnabled())
                {
                    uploadFiles();
                } else
                {
                    updateSyncStatus("Skipping upload because the VPN is not connected and the VPN Only setting is enabled");
                }
            } else
            {
                uploadFiles();
            }
        } catch (Exception e)
        {
            updateSyncStatus("Sync failed because of an unexpected error");
            Log.e(LOG_TAG, "Caught an exception when trying to perform a sync", e);
        }
    }

    /**
     * Check if the Android device is currently connected to a Wi-Fi network.
     *
     * @return If the device is connected to a Wi-Fi Network return true.
     */
    private boolean isWiFiConnected()
    {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        boolean wifiConnected = false;

        for (Network network : connectivityManager.getAllNetworks())
        {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo == null) continue;

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                wifiConnected = networkInfo.isConnected();
                break;
            }
        }
        if (Log.isLoggable(LOG_TAG, Log.INFO)) Log.i(LOG_TAG, "Wifi connected: " + wifiConnected);

        return wifiConnected;
    }

    /**
     * Check if the Android device is currently attached to a VPN.
     *
     * @return If the device is connected to a VPN return true.
     */
    private boolean isVpnEnabled()
    {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        final Network[] networks = connectivityManager.getAllNetworks();

        boolean vpnEnabled = false;

        if (Log.isLoggable(LOG_TAG, Log.INFO)) Log.i(LOG_TAG, "Network count: " + networks.length);
        for (int i = 0; i < networks.length; i++)
        {
            final NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(networks[i]);
            if (caps == null) continue;

            if (Log.isLoggable(LOG_TAG, Log.INFO))
            {
                Log.i(LOG_TAG, "Network " + i + ": " + networks[i]);
            }

            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    && !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN))
            {
                vpnEnabled = true;
            }

            if (Log.isLoggable(LOG_TAG, Log.INFO)) Log.i(LOG_TAG, "VPN transport?: " + vpnEnabled);
        }

        return vpnEnabled;
    }

    /**
     * Generates and submits a {@link SyncRequest} that can be used to schedule periodic sync updates.
     *
     * @param context The context to use when creating the Sync {@link Account}.
     */
    public static void addPeriodicSync(Context context)
    {
        Log.i(LOG_TAG, "Adding the periodic sync adapter for Sync Monkey");

        final Account dummyAccount = getSyncAccount(context);

        final SyncRequest syncRequest = new SyncRequest.Builder()
                .syncPeriodic(SyncMonkeyConstants.SECONDS_IN_HOUR, (SyncMonkeyConstants.SECONDS_IN_HOUR) / 2)
                .setSyncAdapter(dummyAccount, SyncMonkeyConstants.AUTHORITY)
                .setExtras(new Bundle()) // I think there is a bug in Android that makes setting this empty Bundle a requirement
                .build();

        ContentResolver.requestSync(syncRequest);
        ContentResolver.setSyncAutomatically(dummyAccount, SyncMonkeyConstants.AUTHORITY, true);
    }

    /**
     * Run the sync adapter immediately.
     *
     * @param context The context to use when creating the Sync {@link Account}.
     */
    public static void runSyncAdapterNow(Context context)
    {
        Log.i(LOG_TAG, "Running the sync adapter for Sync Monkey immediately");

        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(getSyncAccount(context), SyncMonkeyConstants.AUTHORITY, settingsBundle);
    }

    /**
     * Create a new dummy account for the sync adapter.
     *
     * @param context The application context
     */
    private static Account getSyncAccount(Context context)
    {
        Log.i(LOG_TAG, "Creating a new Sync Account");
        final AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        final Account dummyAccount = new Account(SyncMonkeyConstants.ACCOUNT, SyncMonkeyConstants.ACCOUNT_TYPE);

        if (accountManager == null)
        {
            Log.wtf(LOG_TAG, "Somehow the account manager is null.  Can't create the account needed for the sync adapter");
            return null;
        }

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(dummyAccount) == null)
        {
            if (!accountManager.addAccountExplicitly(dummyAccount, "", null))
            {
                Log.e(LOG_TAG, "getSyncAccount Failed to create a new account");
                return null;
            }
        }

        return dummyAccount;
    }

    /**
     * Pull the upload preferences from the PreferenceManager, and then upload any files that are not already present on the remote server.
     */
    private void uploadFiles()
    {
        synchronized (SyncMonkeyMainActivity.class)
        {
            final String containerName = appPreferences.getString(SyncMonkeyConstants.PROPERTY_CONTAINER_NAME_KEY, null);
            final String localSyncDirectories = appPreferences.getString(SyncMonkeyConstants.PROPERTY_LOCAL_SYNC_DIRECTORIES_KEY, "");
            final String deviceId = appPreferences.getString(SyncMonkeyConstants.PROPERTY_DEVICE_ID_KEY, SyncMonkeyConstants.DEFAULT_DEVICE_ID);

            if (containerName == null)
            {
                final String msg = "Could not upload any files because the containerName was null";
                Log.e(LOG_TAG, msg);
                updateSyncStatus(msg);
                return;
            }

            updateSyncStatus("Sync preference checks passed, starting upload ...");

            // First, sync any files in the private shared directory
            final String privateAppFilesSyncDirectory = new File(getContext().getFilesDir(), SyncMonkeyConstants.PRIVATE_SHARED_SYNC_DIRECTORY).getPath();
            boolean anyDirectorySuccess = processDirectoryForUpload(privateAppFilesSyncDirectory, deviceId);

            for (String relativeSyncDirectory : localSyncDirectories.split(SyncMonkeyConstants.COLON_SEPARATOR))
            {
                if (relativeSyncDirectory.isEmpty()) continue;

                final boolean success = processDirectoryForUpload(dataDirectoryPath + relativeSyncDirectory, deviceId);
                if (success) anyDirectorySuccess = success;
            }

            updateSyncStatus(anyDirectorySuccess ? "Upload successful" : "Uploaded failed");
            if (anyDirectorySuccess) updateLastSuccessfulSyncTime();
        }
    }

    /**
     * Given a directory path, sync all the files in the directory with the provided remote server.
     *
     * @param syncDirectoryPath The directory to sync.
     * @param deviceId          The device ID which will be used as the folder name on the remote server.
     */
    private boolean processDirectoryForUpload(String syncDirectoryPath, String deviceId)
    {
        if (Log.isLoggable(LOG_TAG, Log.INFO))
        {
            Log.i(LOG_TAG, "Syncing the directory: " + syncDirectoryPath);
        }

        // The destination path ends up being /deviceId/directParentDir (for example: "Eliza-Android-S7/NetworkSurveyData").
        final String[] parentDirectories = syncDirectoryPath.split("/");
        final String destinationPath = "/" + deviceId + "/" + parentDirectories[parentDirectories.length - 1] +"/";

        if (azureBlob != null)
        {
            azureBlob.uploadFile(syncDirectoryPath, destinationPath);
        }

        return true;
    }

    /**
     * Updates the status information in the Tray Preferences {@link #statusInformation} so that any UIs can display
     * the latest status.
     *
     * @param newStatus The new status information to display to the user.
     * @since 1.0.0
     */
    private void updateSyncStatus(String newStatus)
    {
        Log.i(LOG_TAG, newStatus);
        statusInformation.put(SyncMonkeyConstants.STATUS_PROPERTY_LAST_SYNC_STATUS_KEY, newStatus);
    }

    /**
     * Updates the status information in the Tray Preferences {@link #statusInformation} so that any UIs can display
     * the latest status.
     *
     * @since 1.0.0
     */
    private void updateLastSuccessfulSyncTime()
    {
        Log.d(LOG_TAG, "Updating the last successful sync time");
        statusInformation.put(SyncMonkeyConstants.STATUS_PROPERTY_LAST_SUCCESSFUL_TIME_KEY, SyncMonkeyUtils.getDateTimeString(LocalDateTime.now()));
    }
}