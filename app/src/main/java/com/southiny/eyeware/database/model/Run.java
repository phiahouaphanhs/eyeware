package com.southiny.eyeware.database.model;

import android.support.annotation.NonNull;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.AppDatabase;
import com.southiny.eyeware.tool.Language;
import com.southiny.eyeware.tool.ProtectionLevel;

@Table(name = Run.TABLE_NAME, database = AppDatabase.class)
public class Run extends Model {

    public static final String TABLE_NAME = "run";

    public static final String COLUMN_SMART_DETECT_ACTIVATED = "smart_detect_activated";
    public static final String COLUMN_VIBRATION_ACTIVATED = "vibration_activated";
    public static final String COLUMN_LANGUAGE = "language_id";

    public static final String COLUMN_PROTECTION_MODE_STANDARD = "protection_mode_standard_id";
    public static final String COLUMN_PROTECTION_MODE_HIGH = "protection_mode_high_id";
    public static final String COLUMN_PROTECTION_MODE_LOW = "protection_mode_low_id";
    public static final String COLUMN_PROTECTION_MODE_GAMER = "protection_mode_gamer_id";

    public static final String COLUMN_PARENTAL_CONTROL = "parental_control_id";

    @PrimaryKey
    private Long id;

    @Column(name = COLUMN_SMART_DETECT_ACTIVATED)
    private boolean smartDetectActivated;

    @Column(name = COLUMN_VIBRATION_ACTIVATED)
    private boolean vibrationActivated;

    @Column(name = COLUMN_LANGUAGE)
    private int languageOrdinal;

    @Column(name = COLUMN_PARENTAL_CONTROL)
    private ParentalControl parentalControl;

    @Column(name = COLUMN_PROTECTION_MODE_STANDARD)
    private ProtectionMode protectionModeStandard;

    @Column(name = COLUMN_PROTECTION_MODE_HIGH)
    private ProtectionMode protectionModeHigh;

    @Column(name = COLUMN_PROTECTION_MODE_LOW)
    private ProtectionMode protectionModeLow;

    @Column(name = COLUMN_PROTECTION_MODE_GAMER)
    private ProtectionMode protectionModeGamer;

    public Run() {
        init();
    }

    public void init() {
        smartDetectActivated = Constants.DEFAULT_SMART_DETECT;
        vibrationActivated = Constants.DEFAULT_VIBRATION;
        languageOrdinal = Constants.DEFAULT_LANGUAGE.ordinal();

        protectionModeStandard = ProtectionMode.getStandardInstance();
        protectionModeHigh = ProtectionMode.getHighInstance();
        protectionModeLow = ProtectionMode.getLowInstance();
        protectionModeGamer = ProtectionMode.getGamerInstance();

        protectionModeStandard.setCurrent(true);
        parentalControl = new ParentalControl();
    }

    public Long getId() {
        return id;
    }


    public boolean isSmartDetectActivated() {
        return smartDetectActivated;
    }

    public void setSmartDetectActivated(boolean smartDetectActivated) {
        this.smartDetectActivated = smartDetectActivated;
    }

    public boolean isVibrationActivated() {
        return vibrationActivated;
    }

    public void setVibrationActivated(boolean vibrationActivated) {
        this.vibrationActivated = vibrationActivated;
    }

    public Language getLanguage() {
        return Language.getLanguageByOrdinal(languageOrdinal);
    }

    public void setLanguage(Language language) {
        this.languageOrdinal = language.ordinal();
    }

    public ProtectionMode getProtectionModeStandard() {
        return protectionModeStandard;
    }

    public void setProtectionModeStandard(ProtectionMode protectionModeStandard) {
        this.protectionModeStandard = protectionModeStandard;
    }

    public ProtectionMode getProtectionModeHigh() {
        return protectionModeHigh;
    }

    public void setProtectionModeHigh(ProtectionMode protectionModeHigh) {
        this.protectionModeHigh = protectionModeHigh;
    }

    public ProtectionMode getProtectionModeLow() {
        return protectionModeLow;
    }

    public void setProtectionModeLow(ProtectionMode protectionModeLow) {
        this.protectionModeLow = protectionModeLow;
    }

    public ProtectionMode getProtectionModeGamer() {
        return protectionModeGamer;
    }

    public void setProtectionModeGamer(ProtectionMode protectionModeGamer) {
        this.protectionModeGamer = protectionModeGamer;
    }

    public ParentalControl getParentalControl() {
        return parentalControl;
    }

    public void setParentalControl(ParentalControl parentalControl) {
        this.parentalControl = parentalControl;
    }

    public ProtectionMode getCurrentProtectionMode() {
        if (this.protectionModeStandard.isCurrent()) {
            return this.protectionModeStandard;
        }

        if (this.protectionModeHigh.isCurrent()) {
            return this.protectionModeHigh;
        }

        if (this.protectionModeLow.isCurrent()) {
            return this.protectionModeLow;
        }

        if (this.protectionModeGamer.isCurrent()) {
            return this.protectionModeGamer;
        }

        return protectionModeStandard;
    }

    public void setCurrentProtectionMode(ProtectionMode protectionMode) {
        switch (protectionMode.getProtectionLevel()) {
            case STANDARD:
                protectionModeStandard.setCurrent(true);
                protectionModeHigh.setCurrent(false);
                protectionModeLow.setCurrent(false);
                protectionModeGamer.setCurrent(false);
                break;

            case HIGH:
                protectionModeStandard.setCurrent(false);
                protectionModeHigh.setCurrent(true);
                protectionModeLow.setCurrent(false);
                protectionModeGamer.setCurrent(false);
                break;

            case LOW:
                protectionModeStandard.setCurrent(false);
                protectionModeHigh.setCurrent(false);
                protectionModeLow.setCurrent(true);
                protectionModeGamer.setCurrent(false);
                break;

            case GAMER:
                protectionModeStandard.setCurrent(false);
                protectionModeHigh.setCurrent(false);
                protectionModeLow.setCurrent(false);
                protectionModeGamer.setCurrent(true);
                break;

        }
    }
}
