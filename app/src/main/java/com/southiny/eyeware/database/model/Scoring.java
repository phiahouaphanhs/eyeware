package com.southiny.eyeware.database.model;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.AppDatabase;
import com.southiny.eyeware.tool.Utils;

import java.util.Calendar;

@Table(name = Scoring.TABLE_NAME, database = AppDatabase.class)
public class Scoring extends Model {

    public static final String TABLE_NAME = "scoring";

    public static final String COLUMN_SCORE_OF_TODAY = "score_of_today";
    public static final String COLUMN_SCORE_TOTAL = "score_total";
    public static final String COLUMN_SCORE_LEVEL = "score_level";

    public static final String COLUMN_EARN_CURRENT_CHAMPION_AWARD = "earn_current_champion";

    public static final String COLUMN_EARN_CHECKOUT_WARD = "earn_checkout";
    public static final String COLUMN_CHECKOUT_DAY_TIMESTAMP = "checkout_day_timestamp";

    public static final String COLUMN_NB_EARN_LEVEL_UP_AWARD = "earn_level_up";
    public static final String COLUMN_NB_EARN_TODAY_PERFORMANCE_AWARD = "earn_today_performance";
    public static final String COLUMN_NB_EARN_EXCELLENCY_AWARD = "earn_excellency";

    public static final String COLUMN_NB_POINTS_EARN_FOR_LEVEL_UPS = "point_for_earn_level_up";

    @PrimaryKey
    private Long id;

    @Column(name = COLUMN_SCORE_OF_TODAY)
    private long scoreOfToday;

    @Column(name = COLUMN_SCORE_TOTAL)
    private long scoreTotal;

    @Column(name = COLUMN_SCORE_LEVEL)
    private int scoreLevel;

    @Column(name = COLUMN_EARN_CURRENT_CHAMPION_AWARD)
    private int earnCurrentChampion;

    @Column(name = COLUMN_EARN_CHECKOUT_WARD)
    private int earnCheckoutAward;

    @Column(name = COLUMN_CHECKOUT_DAY_TIMESTAMP)
    private long checkoutDayTimestamp;

    @Column(name = COLUMN_NB_EARN_LEVEL_UP_AWARD)
    private int earnLevelUpAward;

    @Column(name = COLUMN_NB_POINTS_EARN_FOR_LEVEL_UPS)
    private long scoreEarnedForLevelUp;

    @Column(name = COLUMN_NB_EARN_TODAY_PERFORMANCE_AWARD)
    private int earnTodayPerformanceAward;

    @Column(name = COLUMN_NB_EARN_EXCELLENCY_AWARD)
    private int  earnExcellencyAward;



    public Scoring() {
        scoreOfToday = 1;
        scoreTotal = 1;
        scoreLevel = Constants.FIRST_LEVEL;
        earnCurrentChampion = 0;
        earnCheckoutAward = 1;
        checkoutDayTimestamp = Calendar.getInstance().getTimeInMillis();
        earnLevelUpAward = 0;
        scoreEarnedForLevelUp = 0;
        earnTodayPerformanceAward = 0;
        earnExcellencyAward = 0;

    }

    public long getScoreOfToday() {
        return scoreOfToday;
    }

    public long getScoreTotal() {
        return scoreTotal;
    }

    public int getScoreLevel() {
        return scoreLevel;
    }

    public void setScoreLevel(int scoreLevel) {
        this.scoreLevel = scoreLevel;
    }

    public int getEarnCurrentChampion() {
        return earnCurrentChampion;
    }

    public void setEarnCurrentChampion(int earnCurrentChampion) {
        this.earnCurrentChampion = earnCurrentChampion;
    }

    public int getEarnCheckoutAward() {
        return earnCheckoutAward;
    }

    public void setEarnCheckoutAward(int earnCheckoutAward) {
        this.earnCheckoutAward = earnCheckoutAward;
    }

    public long getCheckoutDayTimestamp() {
        return checkoutDayTimestamp;
    }

    public void setCheckoutDayTimestamp(long checkoutDayTimestamp) {
        this.checkoutDayTimestamp = checkoutDayTimestamp;
    }

    public int getEarnLevelUpAward() {
        return earnLevelUpAward;
    }

    public void setEarnLevelUpAward(int earnLevelUpAward) {
        this.earnLevelUpAward = earnLevelUpAward;
    }

    public int getEarnTodayPerformanceAward() {
        return earnTodayPerformanceAward;
    }

    public void setEarnTodayPerformanceAward(int earnTodayPerformanceAward) {
        this.earnTodayPerformanceAward = earnTodayPerformanceAward;
    }

    public int getEarnExcellencyAward() {
        return earnExcellencyAward;
    }

    public void setEarnExcellencyAward(int earnExcellencyAward) {
        this.earnExcellencyAward = earnExcellencyAward;
    }

    public long getScoreEarnedForLevelUp() {
        return scoreEarnedForLevelUp;
    }

    public void setScoreEarnedForLevelUp(long scoreEarnedForLevelUp) {
        this.scoreEarnedForLevelUp = scoreEarnedForLevelUp;
    }

    /****************/

    public synchronized void gainPoints(long point) {
        this.scoreOfToday += point;
        this.scoreTotal += point;
        levelUp();
        this.save();
    }

    private void levelUp() {
        long nextLevelMinScore = Constants.getDefaultMinScoreOfLevel(this.scoreLevel + 1);

        if (this.scoreTotal >= nextLevelMinScore) {
            // level up !
            this.scoreLevel++;
            this.earnLevelUpAward++;
            this.scoreEarnedForLevelUp += Utils.getReachLevelPoint(this.scoreLevel);
        }

    }

    public void newDay() {
        this.scoreOfToday = 1;
        this.earnCheckoutAward++;
        this.checkoutDayTimestamp = Calendar.getInstance().getTimeInMillis();
        this.save();
    }

}
