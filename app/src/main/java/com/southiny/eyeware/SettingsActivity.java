package com.southiny.eyeware;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.tool.Logger;

public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView resetToDefaultTextView = findViewById(R.id.reset_to_default_text);
        resetToDefaultTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.log(TAG, "onClick() reset to default");
                dialogResetDefaultConfirmation();
            }
        });
    }

    private void dialogResetDefaultConfirmation() {
        Logger.log(TAG, "dialogResetDefaultConfirmation()");
        new AlertDialog.Builder(this)
                .setTitle(getApplicationContext().getString(R.string.reset_to_default_title))
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SQLRequest.deleteAllData();
                        SQLRequest.whatInDB();
                        SQLRequest.getRun();
                        Toast.makeText(SettingsActivity.this, "Reset to default complete", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

    }
}
