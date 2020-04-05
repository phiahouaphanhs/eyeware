package com.southiny.eyeware.database;

import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.southiny.eyeware.Constants;
import com.southiny.eyeware.database.model.Award;
import com.southiny.eyeware.database.model.ParentalControl;
import com.southiny.eyeware.database.model.ProtectionMode;
import com.southiny.eyeware.database.model.Run;
import com.southiny.eyeware.database.model.Scoring;
import com.southiny.eyeware.database.model.ScreenFilter;
import com.southiny.eyeware.tool.Logger;

import java.util.ArrayList;
import java.util.List;

public final class SQLRequest {

    public static final String TAG = SQLRequest.class.getSimpleName();

    public static Run getRun() {
        Logger.log(TAG, "getRun()");
        Run run = Select.from(Run.class).fetchSingle();
        if (run == null) {
            Logger.log(TAG, "save new run");
            run = new Run();
            run.save();
        }
        return run;
    }

    public static void deleteAllData() {
        Logger.log(TAG, "deleteAllData()");
        Delete.from(Run.class).execute();
        Delete.from(ProtectionMode.class).execute();
        Delete.from(ScreenFilter.class).execute();
        Delete.from(ParentalControl.class).execute();
        Delete.from(Scoring.class).execute();
        Delete.from(Award.class).execute();
    }

    public static void whatInDB() {
        Logger.log(TAG, "whatInDB()");
        List<Run> runs = Select.from(Run.class).fetch();
        Logger.log(TAG, "nb runs : " + runs.size());
        List<ProtectionMode> pms = Select.from(ProtectionMode.class).fetch();
        Logger.log(TAG, "nb pms : " + pms.size());
        List<ParentalControl> pctrls = Select.from(ParentalControl.class).fetch();
        Logger.log(TAG, "nb pctrls : " + pctrls.size());
        List<ScreenFilter> sfs = Select.from(ScreenFilter.class).fetch();
        Logger.log(TAG, "nb sfs : " + sfs.size());
        List<Scoring> scorings = Select.from(Scoring.class).fetch();
        Logger.log(TAG, "nb scorings : " + scorings.size());
        List<Award> awards = Select.from(Award.class).fetch();
        Logger.log(TAG, "nb awards : " + awards.size());
    }

    public static ArrayList<Award> getAwards() {
        Logger.log(TAG, "getAwards()");
        List<Award> awards = Select.from(Award.class).fetch();
        if (awards == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(awards);
    }

}
