package com.example.databasetest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.example.databasetest.R;
import com.example.databasetest.fragment_activities.Fragment_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateNormalProfil extends AppCompatActivity {

    private final String TAG = "CreateProfil";

    private Button BtnRegister;
    private EditText eTUsername, etnemail, etnpasswort,etnpasswort2;
    private TextView tVAge;

    private FirebaseAuth mAuth;

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private Uri imguri;
    private StorageReference mStorageRef;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profil);


        this.BtnRegister = (Button)findViewById(R.id.BtnRegister);
        this.tVAge = (TextView)findViewById(R.id.tVAge);
        this.eTUsername = (EditText)findViewById(R.id.eTUsername);
        this.etnemail = (EditText)findViewById(R.id.etnemail);
        this.etnpasswort = (EditText)findViewById(R.id.etnpasswort);
        this.etnpasswort2 = (EditText)findViewById(R.id.etnpasswort2);

        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference("profilpic");

        tVAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateNormalProfil.this,
                        AlertDialog.THEME_HOLO_DARK,
                        onDateSetListener,
                        year, month, day);
                dialog.show();
            }}
                );
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;
                String formattedDayOfMonth = "" + dayOfMonth;
                String formattedMonth = "" +month;

                if(month < 10){
                    formattedMonth = "0" +month;
                }
                if(dayOfMonth < 10){
                    formattedDayOfMonth = "0" +dayOfMonth;
                }

                String date = formattedDayOfMonth + "/" + formattedMonth + "/" +year;
                tVAge.setText(date);
            }
        };

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etnemail.getText()) ^ TextUtils.isEmpty(etnpasswort.getText()) ^ TextUtils.isEmpty(eTUsername.getText())
                        ^ TextUtils.isEmpty(tVAge.getText()) ^ TextUtils.isEmpty(etnpasswort2.getText())){
                    Toast.makeText(CreateNormalProfil.this,R.string.Toast_SelectAllFields,Toast.LENGTH_SHORT).show();
                }else {
                    String passworttmp = etnpasswort.getText().toString().trim();
                    String passwort2 = etnpasswort2.getText().toString().trim();
                    if (passworttmp.equals(passwort2)) {
                        createAccount(etnemail.getText().toString(), etnpasswort.getText().toString(),getUsername(),getAge());
                    } else {
                        Toast.makeText(CreateNormalProfil.this, R.string.Toast_PasswordsDontMatch, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private String getUsername(){
        String tmp = eTUsername.getText().toString();
        return tmp;
    }

    private String getAge(){
        String tmp = tVAge.getText().toString();
        return tmp;
    }

    public void loadHome(){
        Intent intent = new Intent(this, Fragment_Activity.class);
        startActivity(intent);
    }

    public void createAccount(String email, String password, String username, String age){
        SharedPreferences pref = getSharedPreferences("map", 0);
        SharedPreferences.Editor editor = pref.edit();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Map<String, Object> data = new HashMap<>();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            data.put("username", username);
                            data.put("geburtstag", age);
                            data.put("pic", "nopic");
                            db.collection("normaluser")
                                    .document(uid)
                                    .set(data);
                            loadHome();
                            editor.apply();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CreateNormalProfil.this, R.string.Toast_AuthenticationFailed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}
