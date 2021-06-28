package com.example.databasetest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.databasetest.R;
import com.example.databasetest.fragment_activities.Fragment_Activity;
import com.example.databasetest.fragment_activities.Settings_Fragment;
import com.example.databasetest.fragments.bp_showprofil_fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DateBaseTest";

    private EditText etbenutzername, etpasswort;

    private TextView tv_login, tv_register;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private String designlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tv_login = (TextView)findViewById(R.id.tv_main_login);
        this.tv_register = (TextView)findViewById(R.id.tv_main_register);


        this.etbenutzername = (EditText)this.findViewById(R.id.etbenutzername);
        this.etpasswort = (EditText)this.findViewById(R.id.etpasswort);

        mAuth = FirebaseAuth.getInstance();

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etbenutzername.getText()) ^ TextUtils.isEmpty(etpasswort.getText())){
                    Toast.makeText(MainActivity.this,R.string.Toast_SelectAllFields,Toast.LENGTH_SHORT).show();
                }else {
                    signIn(etbenutzername.getText().toString(), etpasswort.getText().toString());
                }
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, select_activity.class));
            }
        });


    }

    public void signIn(String username, String passwort){
        ArrayList<String> useridss = new ArrayList<>();

        mAuth.signInWithEmailAndPassword(username, passwort)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            db.collection("businessuser")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot document: task.getResult()){
                                                    useridss.add(document.getId());
                                                }
                                                if(useridss.contains(uid)){
                                                    startActivity(new Intent(MainActivity.this, Settings_Fragment.class));
                                                }else{
                                                    startActivity(new Intent(MainActivity.this, Fragment_Activity.class));
                                                }
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, R.string.Toast_MailorPasswordIncorrect,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart(){
        super.onStart();

        SharedPreferences pref = getSharedPreferences("map", 0);
        designlayout = pref.getString("layout", "mapbox://styles/rickaldo/ck9fzm0451s231iqpio1e96ts");

        if(designlayout.equals("mapbox://styles/rickaldo/ck9fzm0451s231iqpio1e96ts")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            ArrayList<String> useridss = new ArrayList<>();
            String uid = firebaseUser.getUid();

            db.collection("businessuser")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document: task.getResult()){
                                    useridss.add(document.getId());
                                }
                                if(useridss.contains(uid)){
                                    startActivity(new Intent(MainActivity.this, Settings_Fragment.class));
                                }else{
                                    startActivity(new Intent(MainActivity.this,Fragment_Activity.class));
                                }
                            }
                        }
                    });
        }
    }
}
