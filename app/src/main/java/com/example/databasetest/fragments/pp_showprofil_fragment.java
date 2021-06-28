package com.example.databasetest.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databasetest.Adapter.LastEventsAdapter;
import com.example.databasetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class pp_showprofil_fragment extends Fragment {

    private ImageView iveinstellungen, ivbackward;
    private CircleImageView profile_image;
    private TextView tv_profil_name;
    private RecyclerView rv_lastevents;
    private FloatingActionButton pp_changepic;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference ref;
    private FirebaseAuth mAuth;
    private Uri imguri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.layout_pp_showprofil_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        this.ivbackward = (ImageView)getActivity().findViewById(R.id.ivbackwards);
        this.iveinstellungen = (ImageView)getActivity().findViewById(R.id.iveinstellungen);
        this.tv_profil_name = (TextView)getActivity().findViewById(R.id.tv_profil_name);
        this.profile_image = (CircleImageView)getActivity().findViewById(R.id.profile_image);
        this.rv_lastevents = (RecyclerView)getActivity().findViewById(R.id.rv_lastevents);
        this.pp_changepic = (FloatingActionButton)getActivity().findViewById(R.id.fl_changepic);

        mAuth = FirebaseAuth.getInstance();

        getusername();

        ref = FirebaseStorage.getInstance().getReference("profilpic");
        ivbackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment fragment = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        iveinstellungen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pp_settings_fragment fragment = new pp_settings_fragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

        pp_changepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filechooser();
            }
        });
    }

    public void getusername() {
        ArrayList<String> datum = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> filepath = new ArrayList<>();

        ArrayList<String> finaldatum = new ArrayList<>();
        ArrayList<String> finalname = new ArrayList<>();
        ArrayList<String> finalfilepath = new ArrayList<>();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        Date c = cal.getTime();

        String uid = mAuth.getUid();

        db.collection("normaluser")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String bday = task.getResult().get("geburtstag").toString();
                            String name = task.getResult().get("username").toString();
                            String pic = task.getResult().get("pic").toString();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                            LocalDate birthday = LocalDate.parse(bday, formatter);

                            Date date = new Date();
                            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            int year = localDate.getYear();
                            int month = localDate.getMonthValue();
                            int day = localDate.getDayOfMonth();
                            String formattedday, formattedmonth;

                            if(day < 10){
                                formattedday = "0" +day;
                            }else{
                                formattedday = Integer.toString(day);
                            }
                            if(month < 10){
                                formattedmonth = "0"+ month;
                            }else{
                                formattedmonth = Integer.toString(month);
                            }

                            String toda = formattedday + "/" +formattedmonth + "/" + Integer.toString(year);

                            LocalDate today = LocalDate.parse(toda, formatter);

                            int age = Period.between(birthday,today).getYears();

                            tv_profil_name.setText(name + " " + "(" +age +")");

                            ref.child(pic).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getContext())
                                            .load(uri)
                                            .into(profile_image);
                                }
                            });
                        }
                    }
                });

        db.collection("normaluser")
                .document(uid)
                .collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                if(!document.get("name").toString().equals("filler")) {
                                    datum.add(document.get("date").toString());
                                    name.add(document.get("name").toString());
                                    filepath.add(document.get("filepath").toString());
                                }
                            }
                            for(int i =0; i<datum.size();i++){
                                try {
                                    Date tmp = df.parse(datum.get(i));
                                    if(c.before(tmp)){
                                        finaldatum.add(datum.get(i));
                                        finalname.add(name.get(i));
                                        finalfilepath.add(filepath.get(i));
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        LastEventsAdapter adapter = new LastEventsAdapter(getContext(), finalfilepath, finalname, finaldatum);
                        rv_lastevents.setAdapter(adapter);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
                        rv_lastevents.setLayoutManager(layoutManager);
                    }
                });
    }

    private void Filechooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private String Fileuploader(){
        String tmp = String.valueOf(System.currentTimeMillis());
        String end = tmp +"."+getExtension(imguri);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imguri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if (getExtension(imguri).equals("jpg")) {
            bitmap.compress(Bitmap.CompressFormat.JPEG,75,out);
        }else{
            bitmap.compress(Bitmap.CompressFormat.PNG,75,out);
        }
        byte[] bitmapdata = out.toByteArray();

        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

        StorageReference Ref = ref.child(tmp);

        Ref.putStream(bs)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        db.collection("normaluser")
                                .document(mAuth.getUid())
                                .update("pic",tmp);
                        Toast.makeText(getContext(), R.string.Toast_ImageUploaded, Toast.LENGTH_SHORT).show();
                    }
                });

        return tmp;
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null & data.getData() != null){
            imguri = data.getData();
            profile_image.setImageURI(imguri);
            Fileuploader();
        }
    }
}
