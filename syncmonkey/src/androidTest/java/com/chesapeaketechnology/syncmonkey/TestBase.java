package com.chesapeaketechnology.syncmonkey;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

public class TestBase {
    @Rule
    public ActivityTestRule<SyncMonkeyMainActivity> activityActivityTestRule = new ActivityTestRule<>(SyncMonkeyMainActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE");
}
