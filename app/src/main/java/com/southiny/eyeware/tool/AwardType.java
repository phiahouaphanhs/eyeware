package com.southiny.eyeware.tool;

public enum AwardType {
    EXCELLENCY_AWARD, TODAY_PERFORMANCE_AWARD, SURPRISE_AWARD,
    DOUBLE_SCORE_AWARD, TRIPLE_SCORE_AWARD,
    LEVEL_UP_AWARD, CHAMPION_AWARD, NEW_ARRIVAL_AWARD, TODAY_CHECKOUT_AWARD;


    public static AwardType getAwardTypeByOrdinal(int ordinal) {

        if (ordinal == EXCELLENCY_AWARD.ordinal()) return EXCELLENCY_AWARD;

        if (ordinal == TODAY_PERFORMANCE_AWARD.ordinal()) return TODAY_PERFORMANCE_AWARD;

        if (ordinal == SURPRISE_AWARD.ordinal()) return SURPRISE_AWARD;

        if (ordinal == DOUBLE_SCORE_AWARD.ordinal()) return DOUBLE_SCORE_AWARD;

        if (ordinal == TRIPLE_SCORE_AWARD.ordinal()) return TRIPLE_SCORE_AWARD;

        if (ordinal == LEVEL_UP_AWARD.ordinal()) return LEVEL_UP_AWARD;

        if (ordinal == CHAMPION_AWARD.ordinal()) return CHAMPION_AWARD;

        if (ordinal == TODAY_CHECKOUT_AWARD.ordinal()) return TODAY_CHECKOUT_AWARD;

        if (ordinal == NEW_ARRIVAL_AWARD.ordinal()) return NEW_ARRIVAL_AWARD;

        return SURPRISE_AWARD;
    }

    @Override
    public String toString() {
        switch (this) {
            case EXCELLENCY_AWARD:
                return "Excellency Award";
            case TODAY_PERFORMANCE_AWARD:
                return "Today Performance Award";
            case SURPRISE_AWARD:
                return "Surprise Award";
            case DOUBLE_SCORE_AWARD:
                return "Double Score Award";
            case TRIPLE_SCORE_AWARD:
                return "Triple Score Award";
            case LEVEL_UP_AWARD:
                return "Level Up Award";
            case CHAMPION_AWARD:
                return "Champion Award";
            case NEW_ARRIVAL_AWARD:
                return "New Arrival Award";
            case TODAY_CHECKOUT_AWARD:
                return "Checkout Award";
            default:
                return "Unknown Award";
        }
    }


    /*public long getScore() {
        switch (this) {
            case EXCELLENCY_AWARD:
                return Constants.AWARD_SCORE_EXCELLENCY;
            case TODAY_PERFORMANCE_AWARD:
                return Constants.AWARD_SCORE_TODAY_PERFORMANCE;
            case SURPRISE_AWARD:
                return Constants.AWARD_SCORE_SURPRISE_BASE;
            case DOUBLE_SCORE_AWARD:
                return SQLRequest.getRun().getScoring().getScoreTotal() * 2;
            case TRIPLE_SCORE_AWARD:
                return SQLRequest.getRun().getScoring().getScoreTotal() * 3;
            case LEVEL_UP_AWARD:
                return Constants.AWARD_SCORE_LEVEL_UP_BASE;
            case CHAMPION_AWARD:
                return Constants.AWARD_SCORE_CHAMPION;
            case NEW_ARRIVAL_AWARD:
                return Constants.AWARD_SCORE_NEW_ARRIVAL;
            case TODAY_CHECKOUT_AWARD:
                return Constants.AWARD_SCORE_CHECKOUT;
            default:
                return Constants.AWARD_SCORE_DEFAULT;
        }
    }*/
}
