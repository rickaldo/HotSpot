package com.example.databasetest.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.databasetest.activity.MyCallback;
import com.example.databasetest.R;
import com.example.databasetest.fragment_activities.Settings_Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class newBEvent extends AppCompatActivity {

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "NEWBEVENT";

    private JSONObject temp, key, location;
    private String lat, lng, amPm;

    private StorageReference mStorageRef;

    private int currentHour, currentMinute;

    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog timePickerDialog;

    private Button BtnSearch2;
    private EditText eTStadt2,eTStraße2,eTHNR2,eTpreis, etbeventname,etPlz2;
    private TextView tVDate2,tVTimee2,tv_kategorien;

    private ImageView uploadimage;
    private Spinner sp_kategorien;
    private String api = "";

    private Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_b_event);

        this.BtnSearch2 = (Button)findViewById(R.id.btnSearch2);
        this.eTStadt2 = (EditText)findViewById(R.id.eTStadt2);
        this.eTStraße2 = (EditText)findViewById(R.id.eTStraße2);
        this.eTHNR2 = (EditText)findViewById(R.id.eTHNR2);
        this.tVDate2 = (TextView)findViewById(R.id.tVDate2);
        this.tVTimee2 = (TextView)findViewById(R.id.tVTimee2);
        this.eTpreis = (EditText)findViewById(R.id.etpreis);
        this.etbeventname = (EditText)findViewById(R.id.etbeventname);
        this.sp_kategorien = (Spinner)findViewById(R.id.sp_kategorie);
        this.etPlz2 = (EditText)findViewById(R.id.etPlz2);
        this.tv_kategorien = (TextView)findViewById(R.id.tv_kategorien);

        this.uploadimage = (ImageView)findViewById(R.id.iv_uploadimage);

        ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(newBEvent.this,R.array.Categorie_Array,android.R.layout.simple_spinner_dropdown_item);
        sp_kategorien.setAdapter(adapter);

        Calendar cal = Calendar.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filechooser();
            }
        });
        tVDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        newBEvent.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year,month,day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

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
                tVDate2.setText(date);
            }
        };

        tVTimee2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentHour = cal.get(Calendar.HOUR_OF_DAY);
                currentMinute = cal.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(
                        newBEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(hourOfDay >= 12){
                                    amPm = "PM";
                                }else{
                                    amPm = "AM";
                                }
                                tVTimee2.setText(String.format("%02d:%02d", hourOfDay,minute) +amPm);
                            }
                        }, currentHour,currentMinute, false);
                timePickerDialog.show();
            }
        });
        sp_kategorien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (sp_kategorien.getItemAtPosition(position).toString()){
                    case ("Sport"):
                        String tmp = "sport";
                        tv_kategorien.setText(tmp);
                        tv_kategorien.setVisibility(View.GONE);
                        break;
                    case ("Party"):
                        String tmp1 = "party";
                        tv_kategorien.setText(tmp1);
                        tv_kategorien.setVisibility(View.GONE);
                        break;
                    case ("Musik"):
                        String tmp2 = "musik";
                        tv_kategorien.setText(tmp2);
                        tv_kategorien.setVisibility(View.GONE);
                        break;
                    case ("Infoveranstaltung"):
                        String tmp3 = "info";
                        tv_kategorien.setText(tmp3);
                        tv_kategorien.setVisibility(View.GONE);
                        break;
                    case ("Feste"):
                        String tmp4 = "feste";
                        tv_kategorien.setText(tmp4);
                        tv_kategorien.setVisibility(View.GONE);
                        break;
                    case ("Kultur"):
                        String tmp5 = "kultur";
                        tv_kategorien.setText(tmp5);
                        tv_kategorien.setVisibility(View.GONE);
                        break;
                    case ("Flohmärkte"):
                        String tmp6 = "flohmarkt";
                        tv_kategorien.setText(tmp6);
                        tv_kategorien.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BtnSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventexits(getPlz(),getStadt(),getStraße(),getHNr(),getDate(),getTime());
                Intent intent = new Intent(newBEvent.this, Settings_Fragment.class);
                startActivity(intent);
            }
        });
    }

    private String Fileuploader(){
        String tmp = String.valueOf(System.currentTimeMillis());
        String end = tmp +"."+getExtension(imguri);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imguri);
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

        StorageReference Ref = mStorageRef.child(tmp);


        Ref.putStream(bs)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(newBEvent.this, R.string.Toast_EventCreated, Toast.LENGTH_SHORT).show();
                    }
                });

        return tmp;
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void Filechooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode ==RESULT_OK && data != null & data.getData() != null){
            imguri = data.getData();
            uploadimage.setImageURI(imguri);
        }
    }

    public Context getContext(){
        return (Context)this;
    }

    public void jsonrequest(String url){

        final Map<String, Object> adddata = new HashMap<>();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray tmp = response.getJSONArray("results");
                            for (int i = 0; i < tmp.length(); i++) {
                                temp = tmp.getJSONObject(i);
                            }

                            key = temp.getJSONObject("geometry");

                            location = key.optJSONObject("location");

                            lat = location.getString("lat");

                            lng = location.getString("lng");

                            double late = Double.parseDouble(lat);
                            double lnge = Double.parseDouble(lng);

                            MyCallback.addbeventkord(late, lnge, getDate(), getTime(), getStraße(), getHNr(), getStadt(),getPreis(), getname(),getPlz(),getKategorie(), Fileuploader());

                        } catch (JSONException e) { ;
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
            }
        });
        queue.add(request);
    }

    public String getStraße(){
        return eTStraße2.getText().toString();
    }
    public String getHNr(){
        return eTHNR2.getText().toString();
    }
    public String getStadt(){
        return eTStadt2.getText().toString();
    }

    public String getKategorie(){return tv_kategorien.getText().toString();}
    public String getDate(){
        return tVDate2.getText().toString();
    }

    public String getTime(){
        return tVTimee2.getText().toString();
    }

    public String getUrl(){
        String plz = getPlz();
        String stadt = getStadt();
        String straße = getStraße();
        String hnr = getHNr();
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=" +plz +"+"+stadt +"+" +straße +"+" +hnr +"+" +api);
        return url;
    }

    public int getPreis(){
        return Integer.parseInt(eTpreis.getText().toString());
    }

    public String getname(){
        return etbeventname.getText().toString();
    }

    public String getPlz(){return etPlz2.getText().toString();}

    public void eventexits(String Postleitzahl, String City, String Street, String HNr, String Date, String Time){
        ArrayList<String> street = new ArrayList<String>();
        ArrayList<String> hnr = new ArrayList<String>();
        ArrayList<String> ort = new ArrayList<String>();
        ArrayList<String> date = new ArrayList<String>();
        ArrayList<String> time = new ArrayList<String>();
        ArrayList<String> plz = new ArrayList<>();
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                street.add(document.get("straße").toString());
                                hnr.add(document.get("hnr").toString());
                                ort.add(document.get("stadt").toString());
                                date.add(document.get("date").toString());
                                time.add(document.get("time").toString());
                                plz.add(document.get("plz").toString());
                            }
                            if(ort.contains(City) && street.contains(Street) && hnr.contains(HNr) && date.contains(Date) && time.contains(Time) && plz.contains(Postleitzahl)){
                                Toast.makeText(getContext(), R.string.Toast_EventExists,Toast.LENGTH_LONG).show();
                            }else{
                                locationindatabase();
                            }
                        }

                    }
                });
    }

    public void locationindatabase(){
        ArrayList<String> ort = new ArrayList<String>();
        ArrayList<String> straße = new ArrayList<String>();
        ArrayList<String> hnr = new ArrayList<String>();
        ArrayList<Double> lat = new ArrayList<>();
        ArrayList<Double> lng = new ArrayList<>();
        ArrayList<String> postleitzahl = new ArrayList<>();

        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                ort.add(document.get("stadt").toString());
                                straße.add(document.get("straße").toString());
                                hnr.add(document.get("hnr").toString());
                                lat.add(Double.parseDouble(document.get("lat").toString()));
                                lng.add(Double.parseDouble(document.get("lng").toString()));
                                postleitzahl.add(document.get("plz").toString());
                            }
                            if(ort.contains(getStadt()) && straße.contains(getStraße()) && hnr.contains(getHNr()) && postleitzahl.contains(getPlz())){
                                for(int i = 0; i< ort.size(); i++) {
                                    if (ort.get(i).equals(getStadt()) && straße.get(i).equals(getStraße()) && hnr.get(i).equals(getHNr()) && postleitzahl.get(i).equals(getPlz())) {
                                        MyCallback.addbevent(lat.get(i), lng.get(i), getDate(), getTime(), getStraße(), getHNr(), getStadt(),getPreis(), getname(),getPlz(),getKategorie(),Fileuploader());
                                        break;
                                    }
                                }
                            }else{
                                jsonrequest(getUrl());
                            }
                        }
                    }
                });
    }
}
