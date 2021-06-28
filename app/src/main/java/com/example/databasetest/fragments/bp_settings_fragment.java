package com.example.databasetest.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.databasetest.activity.MainActivity;
import com.example.databasetest.R;
import com.google.firebase.auth.FirebaseAuth;


import javax.annotation.Nullable;

public class bp_settings_fragment extends Fragment {

    private TextView tv_support, tv_feedback, tv_verify, tv_reichweite,tv_abmelden;

    private ImageView iv_backtomaps2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.layout_bp_settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        this.iv_backtomaps2 = (ImageView)getActivity().findViewById(R.id.iv_backttomaps2);

        this.tv_abmelden = (TextView)getActivity().findViewById(R.id.bp_tv_abmelden);
        this.tv_support = (TextView)getActivity().findViewById(R.id.tv_support);
        this.tv_feedback = (TextView)getActivity().findViewById(R.id.tv_feedback);
        this.tv_reichweite = (TextView)getActivity().findViewById(R.id.tv_reichweite);
        this.tv_verify = (TextView)getActivity().findViewById(R.id.tv_verify);

        tv_abmelden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        iv_backtomaps2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp_showprofil_fragment fragment = new bp_showprofil_fragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,fragment).commit();
            }
        });

    }
}
