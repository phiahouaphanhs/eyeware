package com.southiny.eyeware.database.model;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.AppDatabase;
import com.southiny.eyeware.tool.ProtectionLevel;

@Table(name = ParentalControl.TABLE_NAME, database = AppDatabase.class)
public class ParentalControl extends Model {

    public static final String TABLE_NAME = "parental_control";

    public static final String COLUMN_PASSWORD_PROTECTION_ACTIVATED = "password_activated";
    public static final String COLUMN_PASSWORD = "password";

    public static final String COLUMN_LOCK_SCREEN_ACTIVATED = "lock_screen_activated";
    public static final String COLUMN_LOCK_SCREEN_IN_SEC = "lock_screen_in";

    public static final String COLUMN_UNLOCK_SCREEN_ACTIVATED = "unlock_screen_activated";
    public static final String COLUMN_UNLOCK_SCREEN_IN_SEC = "unlock_screen_in";


    @PrimaryKey
    private Long id;

    @Column(name = COLUMN_PASSWORD_PROTECTION_ACTIVATED)
    private boolean isPasswordActivated;

    @Column(name = COLUMN_PASSWORD)
    private String password;

    @Column(name = COLUMN_LOCK_SCREEN_ACTIVATED)
    private boolean isLockScreenActivated;

    @Column(name = COLUMN_LOCK_SCREEN_IN_SEC)
    private int lockScreenInSec;

    @Column(name = COLUMN_UNLOCK_SCREEN_ACTIVATED)
    private boolean isUnlockScreenActivated;

    @Column(name = COLUMN_UNLOCK_SCREEN_IN_SEC)
    private int unlockScreenInSec;



    public ParentalControl() {
        init();
    }

    public void init() {
        isPasswordActivated = Constants.DEFAULT_PASSWORD_ACTIVATE;
        password = Constants.DEFAULT_PASSWORD;
        isLockScreenActivated = Constants.DEFAULT_LOCK_SCREEN_ACTIVATE;
        lockScreenInSec = Constants.DEFAULT_LOCK_SCREEN_IN_SEC;
        isUnlockScreenActivated = Constants.DEFAULT_UNLOCK_SCREEN_ACTIVATE;
        unlockScreenInSec = Constants.DEFAULT_UNLOCK_SCREEN_IN_SEC;
    }

    public boolean isPasswordActivated() {
        return isPasswordActivated;
    }

    public void setPasswordActivated(boolean passwordActivated) {
        isPasswordActivated = passwordActivated;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLockScreenActivated() {
        return isLockScreenActivated;
    }

    public void setLockScreenActivated(boolean lockScreenActivated) {
        isLockScreenActivated = lockScreenActivated;
    }

    public int getLockScreenInSec() {
        return lockScreenInSec;
    }

    public void setLockScreenInSec(int lockScreenInSec) {
        this.lockScreenInSec = lockScreenInSec;
    }

    public boolean isUnlockScreenActivated() {
        return isUnlockScreenActivated;
    }

    public void setUnlockScreenActivated(boolean unlockScreenActivated) {
        isUnlockScreenActivated = unlockScreenActivated;
    }

    public int getUnlockScreenInSec() {
        return unlockScreenInSec;
    }

    public void setUnlockScreenInSec(int unlockScreenInSec) {
        this.unlockScreenInSec = unlockScreenInSec;
    }
}
