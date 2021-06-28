package com.example.databasetest.fragment_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.databasetest.R;
import com.example.databasetest.fragments.bp_showprofil_fragment;

public class Settings_Fragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__fragment);

        bp_showprofil_fragment bp_fragment = new bp_showprofil_fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, bp_fragment).commit();
    }
}