package com.example.databasetest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.example.databasetest.R;
import com.example.databasetest.fragment_activities.Fragment_Activity;

public class Kategorien extends AppCompatActivity {

    private Button btn_kategorie_save;

    private ToggleButton tg_kultur, tg_feste, tg_musik, tg_info, tg_sport, tg_flohmarkt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategorien);

        this.btn_kategorie_save = (Button)findViewById(R.id.btn_kategorie_save);

        this.tg_feste = (ToggleButton)findViewById(R.id.tg_feste);
        this.tg_kultur = (ToggleButton)findViewById(R.id.tg_kultur);
        this.tg_musik = (ToggleButton)findViewById(R.id.tg_musik);
        this.tg_info = (ToggleButton)findViewById(R.id.tg_info);
        this.tg_sport = (ToggleButton)findViewById(R.id.tg_sport);
        this.tg_flohmarkt = (ToggleButton)findViewById(R.id.tg_flohmarkt);


        btn_kategorie_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setpref();
                Intent intent = new Intent(Kategorien.this, Fragment_Activity.class);
                startActivity(intent);
            }
        });

    }

    private void setpref(){
        SharedPreferences pref = getSharedPreferences("map", 0);
        SharedPreferences.Editor edit = pref.edit();

        if(tg_feste.isChecked()){
            edit.putBoolean("feste",true);
        }else{
            edit.putBoolean("feste",false);
        }
        if(tg_kultur.isChecked()){
            edit.putBoolean("kultur",true);
        }else{
            edit.putBoolean("kultur",false);
        }
        if(tg_sport.isChecked()){
            edit.putBoolean("sport",true);
        }else{
            edit.putBoolean("sport",false);
        }
        if(tg_musik.isChecked()){
            edit.putBoolean("musik",true);
        }else{
            edit.putBoolean("musik",false);
        }
        if(tg_flohmarkt.isChecked()){
            edit.putBoolean("flohmarkt",true);
        }else{
            edit.putBoolean("flohmarkt",false);
        }
        if(tg_info.isChecked()){
            edit.putBoolean("info",true);
        }else{
            edit.putBoolean("info",false);
        }
        edit.apply();
    }
}
