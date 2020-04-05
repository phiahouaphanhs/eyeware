package com.southiny.eyeware.database.model;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.southiny.eyeware.database.AppDatabase;
import com.southiny.eyeware.tool.AwardType;
import com.southiny.eyeware.tool.BreakingMode;

@Table(name = Award.TABLE_NAME, database = AppDatabase.class)
public class Award extends Model {

    public static final String TABLE_NAME = "award";

    public static final long NO_EXPIRATION = -1;

    public static final String COLUMN_AWARD_TYPE_ORDINAL = "type_ordinal";
    public static final String COLUMN_EARN_POINTS = "earn_points";
    public static final String COLUMN_RECEIVED_TIMESTAMP = "receive_timestamp";
    public static final String COLUMN_EXPIRED_TIMESTAMP = "expire_timestamp";

    @PrimaryKey
    private Long id;

    @Column(name = COLUMN_AWARD_TYPE_ORDINAL)
    private int awardTypeOrdinal;

    @Column(name = COLUMN_EARN_POINTS)
    private long earnPoints;

    @Column(name = COLUMN_RECEIVED_TIMESTAMP)
    private long receivedTimestamp;

    @Column(name = COLUMN_EXPIRED_TIMESTAMP)
    private long expiredTimestamp;

    // should never be called
    public Award() {
    }

    public Award(AwardType awardType, long earnPoints, long receivedTimestamp, long expiredTimestamp) {
        this.awardTypeOrdinal = awardType.ordinal();
        this.earnPoints = earnPoints;
        this.receivedTimestamp = receivedTimestamp;
        this.expiredTimestamp = expiredTimestamp;
    }

    public AwardType getAwardType() {
        return AwardType.getAwardTypeByOrdinal(awardTypeOrdinal);
    }

    public long getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public long getExpiredTimestamp() {
        return expiredTimestamp;
    }

    public long getEarnPoints() {
        return earnPoints;
    }
}
