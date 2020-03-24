package com.southiny.eyeware;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.southiny.eyeware.service.LockAndUnlockScreenService;
import com.southiny.eyeware.tool.AdminReceiver;
import com.southiny.eyeware.tool.Logger;

public class LockScreenActivity extends AppCompatActivity {

    public final static String TAG = LockScreenActivity.class.getSimpleName();

    DevicePolicyManager deviceManger;
    ComponentName compName;
    static final int RESULT_ENABLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        Logger.log(TAG, "onCreate()");

        ImageView unlockImageView = findViewById(R.id.unlock_icon);
        unlockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LockScreenActivity.this, LockAndUnlockScreenService.class);
                intent.putExtra(LockAndUnlockScreenService.INTENT_EXTRA_LOCK_UNLOCK_CODE,
                        LockAndUnlockScreenService.LOCK_CODE);
                startService(intent);
            }
        });

        deviceManger = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, AdminReceiver.class);

        if (deviceManger.isAdminActive(compName)) {
            Logger.log(TAG, "device is admin");
            deviceManger.lockNow();
        } else {
            Logger.log(TAG, "device is not admin");
            Intent intent = new Intent(DevicePolicyManager
                    .ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Additional  southiny text explaining why this needs to be added.");
            startActivityForResult(intent, RESULT_ENABLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.log(TAG, "onActivityResult()");
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Logger.log(TAG, "Admin enabled!");
                } else {
                    Logger.log(TAG, "Admin enable FAILED!");
                }

                if (deviceManger.isAdminActive(compName)) {
                    Logger.log(TAG, "re-attempted, device is admin");
                    deviceManger.lockNow();
                } else {
                    Logger.log(TAG, "re-attempted, device is not admin");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
