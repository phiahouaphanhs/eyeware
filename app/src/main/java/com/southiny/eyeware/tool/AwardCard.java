package com.southiny.eyeware.tool;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.southiny.eyeware.R;
import com.southiny.eyeware.database.model.Award;

public class AwardCard extends CardView {

    private Award award;

    public AwardCard(Context context, Award award) {
        super(context);
        this.award = award;

        create(context);
    }

    private void create(Context context) {
        CardView.LayoutParams params = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 16, 16, 16);
        this.setLayoutParams(params);

        LinearLayout cardLayout = new LinearLayout(context);
        cardLayout.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.setBackgroundColor(context.getColor(R.color.eyeware_light_black));
        cardLayout.setPadding(16, 16, 16, 16);
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardLayout.setGravity(Gravity.CENTER_VERTICAL);
        this.addView(cardLayout);

        ImageView awardIcon = new ImageView(context);
        awardIcon.setLayoutParams(new ViewGroup.LayoutParams(140, 140));
        awardIcon.setPadding(16, 16, 16, 16);
        awardIcon.setImageResource(R.drawable.ic_coin_accent);
        cardLayout.addView(awardIcon);

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textLayout.setGravity(Gravity.CENTER_VERTICAL);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.addView(textLayout);

        TextView awardName = new TextView(context);
        awardName.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        awardName.setTextColor(context.getColor(R.color.eyeware_gold));
        awardName.setTypeface(awardName.getTypeface(), Typeface.BOLD);
        awardName.setText(this.award.getAwardType().toString());
        textLayout.addView(awardName);

        TextView awardPoints = new TextView(context);
        awardPoints.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        awardPoints.setTextColor(context.getColor(R.color.eyeware_gold));
        String text = "+" + this.award.getEarnPoints() + " points";
        awardPoints.setText(text);
        textLayout.addView(awardPoints);

        TextView awardTimestamp = new TextView(context);
        awardTimestamp.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        awardTimestamp.setTextColor(context.getColor(R.color.colorAccent));
        text = Utils.getStringDatetimeFromTimestamp(this.award.getReceivedTimestamp());
        awardTimestamp.setText(text);
        textLayout.addView(awardTimestamp);

        if (this.award.getExpiredTimestamp() != Award.NO_EXPIRATION) {
            TextView awardExpireTimestamp = new TextView(context);
            awardExpireTimestamp.setLayoutParams(new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            awardExpireTimestamp.setTextColor(context.getColor(R.color.colorAccent));
            text = Utils.getStringDatetimeFromTimestamp(this.award.getExpiredTimestamp());
            awardExpireTimestamp.setText(text);
            textLayout.addView(awardExpireTimestamp);
        }


    }
}
