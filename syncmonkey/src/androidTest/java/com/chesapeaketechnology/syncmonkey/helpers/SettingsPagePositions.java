package com.chesapeaketechnology.syncmonkey.helpers;

public enum SettingsPagePositions
{

    WIFI_ONLY_TOGGLE_POSITION(0),
    VPN_ONLY_POSITION(1),
    AUTO_SYNC_ENABLED_POSITION(2),
    DEVICE_ID_POSITION(4),
    CONTAINER_NAME_POSITION(5),
    SAS_URL_POSITION(6),
    SYNC_DIRECTORIES_POSITION(7);

    private final int value;

    SettingsPagePositions(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}
