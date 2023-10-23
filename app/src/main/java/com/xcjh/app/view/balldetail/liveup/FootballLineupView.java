package com.xcjh.app.view.balldetail.liveup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xcjh.app.R;
import com.xcjh.app.bean.FootballLineupBean;

import org.jetbrains.annotations.NotNull;

public class FootballLineupView extends LinearLayout {

    private TextView tv_home_lineup, tv_home_value;
    private TextView tv_away_lineup, tv_away_value;
    private FootballLiveUpMiddleView lineUpMiddleView;

    public FootballLineupView(Context context) {
        super(context);
        initView(context);
    }

    public FootballLineupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FootballLineupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public FootballLineupView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    @SuppressLint("MissingInflatedId")
    public void initView(Context context) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_detail_game_liveup, this);
        tv_home_lineup = v.findViewById(R.id.tv_match_home_lineup);
        tv_home_value = v.findViewById(R.id.tv_match_home_value);
        tv_away_lineup = v.findViewById(R.id.tv_match_away_lineup);
        tv_away_value = v.findViewById(R.id.tv_match_away_value);
        lineUpMiddleView = v.findViewById(R.id.lineUpMiddleView);
    }

    public void setHomeTeamInfo(String lineup, String value) {
        tv_home_lineup.setText(lineup);//阵型
        tv_home_value.setText(value);//身价
    }
    public void setData(@NotNull FootballLineupBean it) {
        lineUpMiddleView.setData(it);
    }
    public void setAwayTeamInfo(String lineup, String value) {
        tv_away_lineup.setText(lineup);//阵型
        tv_away_value.setText(value);//身价
    }
}
