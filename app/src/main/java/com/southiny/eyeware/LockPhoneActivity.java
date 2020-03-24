package com.southiny.eyeware;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.southiny.eyeware.tool.AdminReceiver;
import com.southiny.eyeware.tool.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

public class LockPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private Button lock, disable, enable, allApps;
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;

    private static final String TAG = LockPhoneActivity.class.getCanonicalName();
    int cpt = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.log(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_phone);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, AdminReceiver.class);

        lock = (Button) findViewById(R.id.lock);
        enable = (Button) findViewById(R.id.enableBtn);
        disable = (Button) findViewById(R.id.disableBtn);
        allApps = (Button) findViewById(R.id.allAppBtn);

        lock.setOnClickListener(this);
        enable.setOnClickListener(this);
        disable.setOnClickListener(this);
        allApps.setOnClickListener(this);

        /*****/


        /*Logger.log(TAG, "post delays unlock for 20sec");
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (cpt == 20) {
                    unlockMeNow2();
                } else {
                    Logger.log(TAG, cpt + "");
                    cpt++;
                    handler.postDelayed(this, 1000);

                }
            }
        });*/
    }

    @Override
    public void onClick(View view) {
        Logger.log(TAG, "on click lalala");
        if (view == lock) {
            Logger.log(TAG, "onclick() lock");
            boolean active = devicePolicyManager.isAdminActive(compName);

            if (active) {
                Logger.log(TAG, "is active");
                devicePolicyManager.lockNow();
            } else {
                Toast.makeText(this, "You need to enable the Admin Device Features", Toast.LENGTH_SHORT).show();
            }

        } else if (view == enable) {
            Logger.log(TAG, "onclick() enable");
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
            startActivityForResult(intent, RESULT_ENABLE);

        } else if (view == disable) {
            Logger.log(TAG, "onclick() disable");
            devicePolicyManager.removeActiveAdmin(compName);
            disable.setVisibility(View.GONE);
            enable.setVisibility(View.VISIBLE);
        }

        else if (view == allApps) {
            Logger.log(TAG, "onclick() appApps");
            allInstallesApps();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Logger.log(TAG,"onResume()");
        boolean isActive = devicePolicyManager.isAdminActive(compName);
        disable.setVisibility(isActive ? View.VISIBLE : View.GONE);
        enable.setVisibility(isActive ? View.GONE : View.VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.log(TAG, "on actiivity result()");
        switch(requestCode) {
            case RESULT_ENABLE :
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(LockPhoneActivity.this, "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LockPhoneActivity.this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void allInstallesApps() {
        Logger.log(TAG, "allInstallApps()");

        final PackageManager pm = getPackageManager();
//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        int cpt = 1;
        for (ApplicationInfo packageInfo : packages) {
            Logger.log(TAG, "\n " + cpt);
            Logger.log(TAG, "Installed package :" + packageInfo.packageName);
            Logger.log(TAG, "Source dir : " + packageInfo.sourceDir);
            Logger.log(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
            cpt++;
        }
    }

    private void addApp(Drawable ic, String label) {
        ImageView icon = new ImageView(this);
        ViewGroup.LayoutParams params = icon.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

    }


    /*private void unlockMeNow2() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        km.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {
            @Override
            public void onDismissError() {
                Logger.log(TAG, "onDismissError()");
                super.onDismissError();
            }

            @Override
            public void onDismissSucceeded() {
                Logger.log(TAG, "onDismissSucceeded()");
                super.onDismissSucceeded();
            }

            @Override
            public void onDismissCancelled() {
                Logger.log(TAG, "onDismissCancelled()");
                super.onDismissCancelled();
            }
        });
    }


   /* private static final int REQUEST_PROVISION_MANAGED_PROFILE = 12;

    private void provisionManagedProfile() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);

        // Use a different intent extra below M to configure the admin component.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //noinspection deprecation
            ComponentName componentName = new ComponentName(this, AdminReceiver.class);
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
                    componentName);
        } else {
            final ComponentName component = new ComponentName(this,
                    AdminReceiver.class.getName());
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                    component);
        }

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_PROVISION_MANAGED_PROFILE);
            this.finish();
        } else {
            Toast.makeText(this, "Device provisioning is not enabled. Stopping.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void setAppEnabled(String packageName, boolean enabled) {
        PackageManager packageManager = getPackageManager();
        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        try {
            int packageFlags;
            if(Build.VERSION.SDK_INT < 24){
                //noinspection deprecation
                packageFlags = PackageManager.GET_UNINSTALLED_PACKAGES;
            }else{
                packageFlags = PackageManager.MATCH_UNINSTALLED_PACKAGES;
            }
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName,
                    packageFlags);
            // Here, we check the ApplicationInfo of the target app, and see if the flags have
            // ApplicationInfo.FLAG_INSTALLED turned on using bitwise operation.
            if (0 == (applicationInfo.flags & ApplicationInfo.FLAG_INSTALLED)) {
                // If the app is not installed in this profile, we can enable it by
                // DPM.enableSystemApp
                if (enabled) {
                    ComponentName componentName = new ComponentName(this, AdminReceiver.class);
                    devicePolicyManager.enableSystemApp(componentName, packageName);
                } else {
                    // But we cannot disable the app since it is already disabled
                    Logger.err(TAG, "Cannot disable this app: " + packageName);
                    return;
                }
            } else {
                // If the app is already installed, we can enable or disable it by
                // DPM.setApplicationHidden
                ComponentName componentName = new ComponentName(this, AdminReceiver.class);
                devicePolicyManager.setApplicationHidden(
                        componentName, packageName, !enabled);
            }
            Toast.makeText(this, enabled ? "Enabled" : "Disabled",
                    Toast.LENGTH_SHORT).show();
        } catch (PackageManager.NameNotFoundException e) {
            Logger.log(TAG, "The app cannot be found: " + packageName + "error is : " + e);
        }
    }

    private byte[] generateRandomPasswordToken() {
        try {
            return SecureRandom.getInstance("SHA1PRNG").generateSeed(32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TargetApi(26)
    private void changePasswordWithToken() {
        byte[] token = generateRandomPasswordToken();
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getApplicationContext().getSystemService(
                DEVICE_POLICY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(KEYGUARD_SERVICE);
        keyguardManager.createConfirmDeviceCredentialIntent(null, null);
        if (devicePolicyManager != null) {
            devicePolicyManager.setResetPasswordToken(AdminReceiver.getComponentName(this), token);
            devicePolicyManager.resetPasswordWithToken(AdminReceiver.getComponentName(this), "1234", token, 0);
        }
    }*/
}
