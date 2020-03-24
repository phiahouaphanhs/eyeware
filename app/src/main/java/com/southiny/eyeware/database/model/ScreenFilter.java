package com.southiny.eyeware.database.model;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.AppDatabase;

@Table(name = ScreenFilter.TABLE_NAME, database = AppDatabase.class)
public class ScreenFilter extends Model {

    public static final String TABLE_NAME = "filter_color";

    public static final String COLUMN_COLOR_CODE_0 = "color_code_0";
    public static final String COLUMN_COLOR_ACTIVATED_0 = "color_activated_0";

    public static final String COLUMN_COLOR_CODE_1 = "color_code_1";
    public static final String COLUMN_COLOR_ACTIVATED_1 = "color_activated_1";

    public static final String COLUMN_COLOR_CODE_2 = "color_code_2";
    public static final String COLUMN_COLOR_ACTIVATED_2 = "color_activated_2";

    public static final String COLUMN_COLOR_CODE_3 = "color_code_3";
    public static final String COLUMN_COLOR_ACTIVATED_3 = "color_activated_3";

    public static final String COLUMN_COLOR_CODE_4 = "color_code_4";
    public static final String COLUMN_COLOR_ACTIVATED_4 = "color_activated_4";

    public static final String COLUMN_COLOR_CODE_5 = "color_code_5";
    public static final String COLUMN_COLOR_ACTIVATED_5 = "color_activated_5";

    public static final String COLUMN_COLOR_CODE_6 = "color_code_6";
    public static final String COLUMN_COLOR_ACTIVATED_6 = "color_activated_6";

    public static final String COLUMN_COLOR_CODE_7 = "color_code_7";
    public static final String COLUMN_COLOR_ACTIVATED_7 = "color_activated_7";

    @PrimaryKey
    private Long id;

    @Column(name = COLUMN_COLOR_CODE_0)
    private String code0;

    @Column(name = COLUMN_COLOR_ACTIVATED_0)
    private boolean isActivated0;

    @Column(name = COLUMN_COLOR_CODE_1)
    private String code1;

    @Column(name = COLUMN_COLOR_ACTIVATED_1)
    private boolean isActivated1;

    @Column(name = COLUMN_COLOR_CODE_2)
    private String code2;

    @Column(name = COLUMN_COLOR_ACTIVATED_2)
    private boolean isActivated2;

    @Column(name = COLUMN_COLOR_CODE_3)
    private String code3;

    @Column(name = COLUMN_COLOR_ACTIVATED_3)
    private boolean isActivated3;

    @Column(name = COLUMN_COLOR_CODE_4)
    private String code4;

    @Column(name = COLUMN_COLOR_ACTIVATED_4)
    private boolean isActivated4;

    @Column(name = COLUMN_COLOR_CODE_5)
    private String code5;

    @Column(name = COLUMN_COLOR_ACTIVATED_5)
    private boolean isActivated5;

    @Column(name = COLUMN_COLOR_CODE_6)
    private String code6;

    @Column(name = COLUMN_COLOR_ACTIVATED_6)
    private boolean isActivated6;

    @Column(name = COLUMN_COLOR_CODE_7)
    private String code7;

    @Column(name = COLUMN_COLOR_ACTIVATED_7)
    private boolean isActivated7;




    public ScreenFilter() {
        init();
    }

    public void init() {
        isActivated0 = true;
        isActivated1 = true;
        isActivated2 = true;
        isActivated3 = true;
        isActivated4 = true;
        isActivated5 = true;
        isActivated6 = true;
        isActivated7 = true;

        code0 = Constants.DEFAULT_FILTER_COLORS[0];
        code1 = Constants.DEFAULT_FILTER_COLORS[1];
        code2 = Constants.DEFAULT_FILTER_COLORS[2];
        code3 = Constants.DEFAULT_FILTER_COLORS[3];
        code4 = Constants.DEFAULT_FILTER_COLORS[4];
        code5 = Constants.DEFAULT_FILTER_COLORS[5];
        code6 = Constants.DEFAULT_FILTER_COLORS[6];
        code7 = Constants.DEFAULT_FILTER_COLORS[7];
    }

    public String getCode0() {
        return code0;
    }

    public void setCode0(String code0) {
        this.code0 = code0;
    }

    public boolean isActivated0() {
        return isActivated0;
    }

    public void setActivated0(boolean activated0) {
        isActivated0 = activated0;
    }

    public String getCode1() {
        return code1;
    }

    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public boolean isActivated1() {
        return isActivated1;
    }

    public void setActivated1(boolean activated1) {
        isActivated1 = activated1;
    }

    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public boolean isActivated2() {
        return isActivated2;
    }

    public void setActivated2(boolean activated2) {
        isActivated2 = activated2;
    }

    public String getCode3() {
        return code3;
    }

    public void setCode3(String code3) {
        this.code3 = code3;
    }

    public boolean isActivated3() {
        return isActivated3;
    }

    public void setActivated3(boolean activated3) {
        isActivated3 = activated3;
    }

    public String getCode4() {
        return code4;
    }

    public void setCode4(String code4) {
        this.code4 = code4;
    }

    public boolean isActivated4() {
        return isActivated4;
    }

    public void setActivated4(boolean activated4) {
        isActivated4 = activated4;
    }

    public String getCode5() {
        return code5;
    }

    public void setCode5(String code5) {
        this.code5 = code5;
    }

    public boolean isActivated5() {
        return isActivated5;
    }

    public void setActivated5(boolean activated5) {
        isActivated5 = activated5;
    }

    public String getCode6() {
        return code6;
    }

    public void setCode6(String code6) {
        this.code6 = code6;
    }

    public boolean isActivated6() {
        return isActivated6;
    }

    public void setActivated6(boolean activated6) {
        isActivated6 = activated6;
    }

    public String getCode7() {
        return code7;
    }

    public void setCode7(String code7) {
        this.code7 = code7;
    }

    public boolean isActivated7() {
        return isActivated7;
    }

    public void setActivated7(boolean activated7) {
        isActivated7 = activated7;
    }
}
