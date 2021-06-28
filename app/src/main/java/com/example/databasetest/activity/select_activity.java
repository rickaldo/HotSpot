package com.example.databasetest.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.databasetest.R;

public class select_activity extends AppCompatActivity {

    private ToggleButton tg_pp, tg_bp;

    private TextView tv_select;

    private RadioGroup toggleGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_activity);

        this.toggleGroup = (RadioGroup) findViewById(R.id.tg_group2);

        this.tg_bp = (ToggleButton)findViewById(R.id.tg_bp);
        this.tg_pp = (ToggleButton)findViewById(R.id.tg_pp);

        this.tv_select = (TextView)findViewById(R.id.tv_select_anwenden);

        toggleGroup.setOnCheckedChangeListener(ToggleListener);

        tg_bp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_day(v);
            }
        });

        tg_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_night(v);
            }
        });

        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tg_bp.isChecked()){
                    startActivity(new Intent(select_activity.this, CreateBusinessProfil.class));
                }else if(tg_pp.isChecked()){
                    startActivity(new Intent(select_activity.this, CreateNormalProfil.class));
                }
            }
        });
    }

    final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for(int j= 0; j < group.getChildCount(); j++){
                final ToggleButton view = (ToggleButton) group.getChildAt(j);
                view.setChecked(view.getId() == checkedId);
                if(view.getId() == checkedId){
                    view.setChecked(true);
                }else{
                    view.setChecked(false);
                }
            }
        }};

    public void layout_day(View view){
        ((RadioGroup)view.getParent()).check(view.getId());
    }

    public void layout_night(View view){
        ((RadioGroup)view.getParent()).check(view.getId());
    }

}