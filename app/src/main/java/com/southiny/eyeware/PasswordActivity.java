package com.southiny.eyeware;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.southiny.eyeware.database.SQLRequest;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.tool.Logger;
import com.southiny.eyeware.tool.Utils;

public class PasswordActivity extends AppCompatActivity {

    public static final String TAG = PasswordActivity.class.getSimpleName();

    EditText passwordInputEditText;
    TextView passwordErrorTextView;

    ParentalControl pctrl;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.log(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
    }

    @Override
    protected void onStart() {
        Logger.log(TAG, "onStart()");
        super.onStart();

        pctrl = SQLRequest.getRun().getParentalControl();
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);


        passwordInputEditText = findViewById(R.id.password_input);
        passwordErrorTextView = findViewById(R.id.password_error_message_text);
        ImageView unlockImageView = findViewById(R.id.unlock_icon);
        unlockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAnimate(view);
                playClickSound();
                onDone();
            }
        });

        passwordInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    onDone();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.log(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.log(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.log(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.log(TAG, "onDestroy()");
    }

    private void clickAnimate(View view) {
        Utils.fade(view, getApplicationContext());
    }

    private void playClickSound() {
        audioManager.playSoundEffect(SoundEffectConstants.CLICK,1.0f);
    }

    private void onDone() {
        String enterPassword = passwordInputEditText.getText().toString();
        String correctPassword = pctrl.getPassword();

        boolean passed = validatePassword(enterPassword, correctPassword);

        if (passed) {
            Logger.log(TAG, "passed !");

            // start main activity
            Logger.log(TAG, "start " + Main2Activity.class.getSimpleName() + "...");
            Intent intent = new Intent(PasswordActivity.this, Main2Activity.class);
            startActivity(intent);
            Logger.log(TAG, "finished.");
            finish();

        } else {
            Logger.log(TAG, "incorrect !");
            passwordErrorTextView.setText(R.string.password_incorrect_error_message);

        }
    }

    private boolean validatePassword(String enterPassword, String correctPassword) {
        if (enterPassword.length() != correctPassword.length()) {
            return false;
        } else {
            for (int i = 0; i < correctPassword.length(); i++) {
                if (enterPassword.charAt(i) != correctPassword.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
    }
}
