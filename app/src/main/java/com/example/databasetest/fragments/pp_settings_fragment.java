package com.example.databasetest.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.databasetest.activity.MainActivity;
import com.example.databasetest.R;
import com.google.firebase.auth.FirebaseAuth;

import javax.annotation.Nullable;

public class pp_settings_fragment extends Fragment {

    private TextView tv_hp_empfehlen,tv_abmelden,tv_settings_ja;

    private ToggleButton tg_layout_day, tg_layout_night;

    private RadioGroup tg_group;

    private ImageView iv_backttomaps;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.layout_pp_settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        this.tg_layout_day = (ToggleButton) getActivity().findViewById(R.id.tg_layout_day);
        this.tg_layout_night = (ToggleButton) getActivity().findViewById(R.id.tg_layout_night);

        this.tv_abmelden = (TextView) getActivity().findViewById(R.id.tv_abmelden);
        this.tv_hp_empfehlen = (TextView) getActivity().findViewById(R.id.tv_hp_empfehlen);
        this.tv_settings_ja = (TextView) getActivity().findViewById(R.id.tv_settings_ja);

        this.tg_group = (RadioGroup) getActivity().findViewById(R.id.tg_group);

        this.iv_backttomaps = (ImageView)getActivity().findViewById(R.id.iv_backttomaps);

        pref = getActivity().getSharedPreferences("map", 0);
        editor = pref.edit();
        String mapdesign = pref.getString("layout", "mapbox://styles/rickaldo/ck9fzm0451s231iqpio1e96ts");

        if(mapdesign.equals("mapbox://styles/rickaldo/ck9fzm0451s231iqpio1e96ts")){
            tg_layout_day.setChecked(true);
        }else{
            tg_layout_night.setChecked(true);
        }

        tg_group.setOnCheckedChangeListener(ToggleListener);

        tg_layout_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_day(v);
            }
        });

        tg_layout_night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_night(v);
            }
        });

        iv_backttomaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment fragment = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        tv_abmelden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onnClick(v);
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
        map_day();
    }

    public void layout_night(View view){
        ((RadioGroup)view.getParent()).check(view.getId());
        map_night();
    }

    private void map_day(){
        editor.putString("layout", "mapbox://styles/rickaldo/ck9fzm0451s231iqpio1e96ts");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        editor.apply();
    }

    private void map_night(){
        editor.putString("layout", "mapbox://styles/rickaldo/ck9n667n92gwb1immykdsmg0w");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        editor.apply();
    }

    public void onnClick(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
