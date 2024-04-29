package com.cn.game.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.cn.game.sdk.R;

public class ViewTopByeView  extends LinearLayout {
    private AppCompatImageView ivOff,ivOk;


    public ViewTopByeView(Context context) {
        super(context);
    }

    public ViewTopByeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ViewTopByeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public ViewTopByeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }
    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.view_go_beyond, this);
        ivOff = (AppCompatImageView) rootView.findViewById(R.id.ivOff);
        ivOk = (AppCompatImageView) rootView.findViewById(R.id.ivOk);
//        ivOff.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(context,"2222222222",Toast.LENGTH_SHORT).show();
//            }
//        });

    }
}
