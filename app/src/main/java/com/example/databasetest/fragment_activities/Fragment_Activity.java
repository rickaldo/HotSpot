package com.example.databasetest.fragment_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.databasetest.fragments.EntdeckeFragment;
import com.example.databasetest.R;
import com.example.databasetest.fragments.HomeFragment;
import com.example.databasetest.fragments.MapboxFragment;
import com.example.databasetest.fragments.TopEventsFragment;
import com.example.databasetest.fragments.pp_showprofil_fragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Fragment_Activity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference ref = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth;

    private static String TAG = "FRAGMENTACTIVITY";
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        checkForDynamicLinks();
        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_favorites:
                            selectedFragment = new EntdeckeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new TopEventsFragment();
                            break;
                        case R.id.nav_profil:
                            selectedFragment = new pp_showprofil_fragment();
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

                    return true;
                }
            };

    private void checkForDynamicLinks(){
        SharedPreferences pref = getSharedPreferences("map",0);
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if(pendingDynamicLinkData !=null){
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        if(deepLink != null){
                            MapboxFragment fragment = new MapboxFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                            pref.edit().putBoolean("link",true).apply();
                            pref.edit().putString("linkid",deepLink.getQueryParameter("id")).apply();
                        }else{
                            HomeFragment fragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
