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
import com.example.databasetest.activity.newBEvent;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class bp_showprofil_fragment extends Fragment {
    private TextView tv_profilname;

    private CircleImageView profileImage2;
    private FloatingActionButton bp_changepic;

    private ImageView ivbackwards, iveinstellungen;

    private RecyclerView rv_lastevents2, rv_liveevents;

    private Uri imguri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.layout_bp_showprofil_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        this.iveinstellungen = (ImageView)getActivity().findViewById(R.id.iveinstellungen2);
        this.ivbackwards = (ImageView)getActivity().findViewById(R.id.ivbackwards2);
        this.tv_profilname = (TextView)getActivity().findViewById(R.id.tv_profil_name2);
        this.profileImage2 = (CircleImageView)getActivity().findViewById(R.id.profile_image2);
        this.bp_changepic = (FloatingActionButton)getActivity().findViewById(R.id.fl_changepic2);

        this.rv_lastevents2 = (RecyclerView)getActivity().findViewById(R.id.rv_lastevents2);
        this.rv_liveevents = (RecyclerView)getActivity().findViewById(R.id.rv_liveevents);

        mAuth = FirebaseAuth.getInstance();

        getusername();

        ref = FirebaseStorage.getInstance().getReference("profilpic");

        ivbackwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), newBEvent.class));
            }
        });

        iveinstellungen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp_settings_fragment fragment = new bp_settings_fragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,fragment).commit();
            }
        });

        bp_changepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filechooser();
            }
        });

        db.collection("businessuser")
                .document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            String pic = task.getResult().get("pic").toString();

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getContext())
                                            .load(uri)
                                            .into(profileImage2);
                                }
                            });
                        }
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

        ArrayList<String> finaldatum2 = new ArrayList<>();
        ArrayList<String> finalname2 = new ArrayList<>();
        ArrayList<String> finalfilepath2 = new ArrayList<>();

        String uid = mAuth.getUid();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        Date c = cal.getTime();

        db.collection("businessuser")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String name = task.getResult().get("username").toString();
                            String pic = task.getResult().get("pic").toString();

                            tv_profilname.setText(name);

                            ref.child("profilpic/" +pic).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getActivity())
                                            .load(uri)
                                            .into(profileImage2);
                                }
                            });
                        }
                    }
                });

        db.collection("businessuser")
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
                                    filepath.add(document.get("pic").toString());
                                }
                            }
                            for(int i =0; i<datum.size();i++){
                                try {
                                    Date tmp = df.parse(datum.get(i));
                                    if(c.before(tmp)){
                                        finaldatum.add(datum.get(i));
                                        finalname.add(name.get(i));
                                        finalfilepath.add(filepath.get(i));
                                    }else if(c.after(tmp)){
                                        finaldatum2.add(datum.get(i));
                                        finalname2.add(name.get(i));
                                        finalfilepath2.add(filepath.get(i));
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        LastEventsAdapter adapter = new LastEventsAdapter(getContext(), finalfilepath, finalname, finaldatum);
                        rv_lastevents2.setAdapter(adapter);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
                        rv_lastevents2.setLayoutManager(layoutManager);

                        LastEventsAdapter adaptere = new LastEventsAdapter(getContext(), finalfilepath2, finalname2, finaldatum2);
                        rv_liveevents.setAdapter(adaptere);
                        LinearLayoutManager layoutManagere = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
                        rv_liveevents.setLayoutManager(layoutManagere);
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

        bitmap = Bitmap.createScaledBitmap(bitmap,200,300,false);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if (getExtension(imguri).equals("jpg")) {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
        }else{
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
        }
        byte[] bitmapdata = out.toByteArray();

        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

        StorageReference Ref = ref.child(tmp);

        Ref.putStream(bs)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        db.collection("businessuser")
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
            profileImage2.setImageURI(imguri);
            Fileuploader();
        }
    }
}
