package com.southiny.eyeware.database.model;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.AppDatabase;
import com.southiny.eyeware.tool.AwardType;
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
    public static final String COLUMN_THIS_DAY_GOAL_POINT = "this_day_goal_point";
    public static final String COLUMN_HAS_EARN_TODAY_PERFORMANCE = "has_earn_today_performance";
    public static final String COLUMN_HAS_EARN_TODAY_LEVEL_UP = "has_earn_today_level_up";

    public static final String COLUMN_NB_EARN_LEVEL_UP_AWARD = "earn_level_up";
   
    public static final String COLUMN_NB_POINTS_FOR_EXCELLENCY = "point_for_excellency";
    public static final String COLUMN_NB_POINTS_EARN_FOR_LEVEL_UPS = "point_for_earn_level_up";
    public static final String COLUMN_NB_POINTS_EARN_FOR_SURPRISE = "point_for_earn_surprise";
    public static final String COLUMN_NB_POINTS_FOR_TODAY_PERFORMANCE = "point_for_today_performance";

    public static final String COLUMN_NB_CONSECUTIVE_EARN_TODAY_PERFORMANCE = "consecutive_earn_today_performance";
    public static final String COLUMN_NB_CONSECUTIVE_EARN_TODAY_LEVEL_UP = "consecutive_earn_today_level_up";

    
    
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
    private volatile long checkoutDayTimestamp;
    
    @Column(name = COLUMN_THIS_DAY_GOAL_POINT)
    private long goalPoints;
    
    @Column(name = COLUMN_HAS_EARN_TODAY_LEVEL_UP)
    private boolean hasEarnTodayLevelUp;
    
    @Column(name = COLUMN_HAS_EARN_TODAY_PERFORMANCE)
    private boolean hasEarnTodayPerformance;

    @Column(name = COLUMN_NB_CONSECUTIVE_EARN_TODAY_PERFORMANCE)
    private int consecutiveEarnTodayPerformance;

    @Column(name = COLUMN_NB_CONSECUTIVE_EARN_TODAY_LEVEL_UP)
    private int consecutiveEarnLevelUp;
    
    

    @Column(name = COLUMN_NB_EARN_LEVEL_UP_AWARD)
    private int earnLevelUpAward;
    

    @Column(name = COLUMN_NB_POINTS_EARN_FOR_LEVEL_UPS)
    private long scoreEarnedForLevelUp;

    @Column(name = COLUMN_NB_POINTS_FOR_TODAY_PERFORMANCE)
    private long scoreEarnedForTodayPerformance;

    @Column(name = COLUMN_NB_POINTS_FOR_EXCELLENCY)
    private long scoreEarnedForExcellency;

    @Column(name = COLUMN_NB_POINTS_EARN_FOR_SURPRISE)
    private long scoreEarnedForSurprise;



    public Scoring() {
        scoreOfToday = 1;
        scoreTotal = 1;
        scoreLevel = Constants.FIRST_LEVEL;
        
        earnCurrentChampion = 0;
        
        earnCheckoutAward = 1;
        checkoutDayTimestamp = Calendar.getInstance().getTimeInMillis();
        goalPoints = Utils.getDefaultMinScoreOfLevel(2) * 2;
        hasEarnTodayLevelUp = false;
        hasEarnTodayPerformance = false;
        consecutiveEarnLevelUp = 0;
        consecutiveEarnTodayPerformance = 0;

        earnLevelUpAward = 0;
        
        scoreEarnedForTodayPerformance = Constants.AWARD_SCORE_TODAY_PERFORMANCE;
        scoreEarnedForSurprise = 0;
        scoreEarnedForLevelUp = 0;
        scoreEarnedForExcellency = 0;

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

    public long getScoreEarnedForTodayPerformance() {
        return scoreEarnedForTodayPerformance;
    }

    public void setScoreEarnedForTodayPerformance(long scoreEarnedForTodayPerformance) {
        this.scoreEarnedForTodayPerformance = scoreEarnedForTodayPerformance;
    }

    public long getScoreEarnedForExcellency() {
        return scoreEarnedForExcellency;
    }

    public void setScoreEarnedForExcellency(long scoreEarnedForExcellency) {
        this.scoreEarnedForExcellency = scoreEarnedForExcellency;
    }

    public long getScoreEarnedForLevelUp() {
        return scoreEarnedForLevelUp;
    }

    public long getScoreEarnedForSurprise() {
        return scoreEarnedForSurprise;
    }

    public void setScoreEarnedForSurprise(long scoreEarnedForSurprise) {
        this.scoreEarnedForSurprise = scoreEarnedForSurprise;
    }

    public long getGoalPoints() {
        return goalPoints;
    }

    public boolean isHasEarnTodayLevelUp() {
        return hasEarnTodayLevelUp;
    }

    public boolean isHasEarnTodayPerformance() {
        return hasEarnTodayPerformance;
    }

    public void setHasEarnTodayPerformance(boolean hasEarnTodayPerformance) {
        this.hasEarnTodayPerformance = hasEarnTodayPerformance;
    }

    /****************/

    public synchronized void gainPoints(long point) {
        this.scoreOfToday += point;
        this.scoreTotal += point;
        levelUp();
        this.save();
    }

    private void levelUp() {
        long nextLevelMinScore = Utils.getDefaultMinScoreOfLevel(this.scoreLevel + 1);

        if (this.scoreTotal >= nextLevelMinScore) {
            // level up !
            this.scoreLevel++;
            this.earnLevelUpAward++;
            this.scoreEarnedForLevelUp += Utils.getReachLevelPoints(this.scoreLevel);
            this.hasEarnTodayLevelUp = true;
        }

    }

    // call after do the SQLRequest, so no sync problem
    public synchronized void newDay() {
        this.scoreOfToday = 1;
        this.earnCheckoutAward++;
        this.checkoutDayTimestamp = System.currentTimeMillis();
        this.goalPoints = Utils.getDefaultMinScoreOfLevel(this.scoreLevel + 1);
        this.scoreEarnedForTodayPerformance = Constants.AWARD_SCORE_TODAY_PERFORMANCE;

        if (this.hasEarnTodayPerformance) {
            this.consecutiveEarnTodayPerformance += 1;
            this.hasEarnTodayPerformance = false;
        } else {
            this.consecutiveEarnTodayPerformance = 0;
        }

        if (this.hasEarnTodayLevelUp) {
            this.consecutiveEarnLevelUp += 1;
            this.hasEarnTodayLevelUp = false;
        } else {
            consecutiveEarnLevelUp = 0;
        }

        if (Constants.shouldEarnExcellency(this.consecutiveEarnTodayPerformance, this.consecutiveEarnLevelUp)) {
            this.scoreEarnedForExcellency = Constants.AWARD_SCORE_EXCELLENCY;
        }

        this.save();
    }

    public void raiseScoreEarnedForSurprise(long scoreEarnedForSurprise) {
        this.scoreEarnedForSurprise += scoreEarnedForSurprise;
    }

    /***************/

    public void earnCheckoutAward(long newPoints) {
        this.earnCheckoutAward = 0;
        gainPoints(newPoints);
        Award award = new Award(AwardType.TODAY_CHECKOUT_AWARD, newPoints,
                System.currentTimeMillis(), Award.NO_EXPIRATION);
        award.save();
    }

    public void earnLevelUpAward(long newPoints) {
        this.earnLevelUpAward = 0;
        this.scoreEarnedForLevelUp = 0;
        gainPoints(newPoints);
        Award award = new Award(AwardType.LEVEL_UP_AWARD, newPoints,
                System.currentTimeMillis(), Award.NO_EXPIRATION);
        award.save();
    }

    public void earnTodayPerformanceAward(long newPoints) {
        this.scoreEarnedForTodayPerformance = 0;
        this.hasEarnTodayPerformance = true;
        gainPoints(newPoints);
        Award award = new Award(AwardType.TODAY_PERFORMANCE_AWARD, newPoints,
                System.currentTimeMillis(), Award.NO_EXPIRATION);
        award.save();
    }

    public void earnSurpriseAward(long newPoints) {
        this.scoreEarnedForSurprise = 0;
        this.gainPoints(newPoints);
        Award award = new Award(AwardType.SURPRISE_AWARD, newPoints,
                System.currentTimeMillis(), Award.NO_EXPIRATION);
        award.save();
    }

    public void earnExcellencyAward(long newPoints) {
        this.scoreEarnedForExcellency = 0;
        gainPoints(newPoints);
        Award award = new Award(AwardType.EXCELLENCY_AWARD, newPoints,
                System.currentTimeMillis(), Award.NO_EXPIRATION);
        award.save();
    }

}
