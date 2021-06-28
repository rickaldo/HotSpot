package com.example.databasetest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.databasetest.R;
import com.example.databasetest.fragment_activities.Settings_Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateBusinessProfil extends AppCompatActivity {

    private EditText etbname, etbemail, etbpasswort,etbpcpasswort;
    private Button btbcreate;

    private FirebaseAuth mAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business_profil);

        this.etbname = (EditText)findViewById(R.id.etbname);
        this.btbcreate = (Button)findViewById(R.id.btbcreate);
        this.etbemail = (EditText)findViewById(R.id.etbemail);
        this.etbpasswort = (EditText)findViewById(R.id.etbpasswort);
        this.etbpcpasswort = (EditText)findViewById(R.id.etbpasswort2);

        mAuth = FirebaseAuth.getInstance();

        btbcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etbname.getText()) ^ TextUtils.isEmpty(etbemail.getText()) ^ TextUtils.isEmpty(etbpasswort.getText())
                        ^ TextUtils.isEmpty(etbpcpasswort.getText())){
                    Toast.makeText(CreateBusinessProfil.this,R.string.Toast_SelectAllFields,Toast.LENGTH_SHORT).show();
                }else {
                    String passworttmp = etbpasswort.getText().toString().trim();
                    String passwort2 = etbpcpasswort.getText().toString().trim();
                    if (passworttmp.equals(passwort2)) {
                        createAccount(etbemail.getText().toString(), etbpasswort.getText().toString(), etbname.getText().toString());
                    } else {
                        Toast.makeText(CreateBusinessProfil.this, R.string.Toast_PasswordsDontMatch, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void createAccount(String email, String password, String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Map<String, Object> data = new HashMap<>();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            data.put("username", username);
                            data.put("pic", "nopic");
                            data.put("rights", 1);

                            db.collection("businessuser")
                                    .document(uid)
                                    .set(data);

                            Intent intent = new Intent(CreateBusinessProfil.this, Settings_Fragment.class);
                            intent.putExtra("profil","bp_profil");
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CreateBusinessProfil.this, R.string.Toast_AuthenticationFailed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
