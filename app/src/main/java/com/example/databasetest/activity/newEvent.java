package com.example.databasetest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.databasetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
interface MyCallback{
    static void latlng(double lat, double lng, String date, String time, String Straße, String Hnr, String Stadt, String eventname, String kategorie){
        newEvent event = new newEvent();
            Map<String, Object> data = new HashMap<>();

            data.put("lat", lat);
            data.put("lng", lng);
            data.put("date", date);
            data.put("time", time);
            data.put("stadt", Stadt);
            data.put("straße", Straße);
            data.put("hnr", Hnr);
            data.put("name", eventname);
            data.put("kategorie", kategorie);
            data.put("counter", 0);
            event.db.collection("events")
                    .add(data);


        Map<String, Object> dataa = new HashMap<>();
        dataa.put("stadt", Stadt);
        dataa.put("straße", Straße);
        dataa.put("hnr", Hnr);
        dataa.put("lat", lat);
        dataa.put("lng", lng);

        event.db.collection("locations")
                .add(dataa);
    }

    static void addevent(double lat, double lng, String date, String time, String Straße, String Hnr, String Stadt, String eventname, String kategorie){
        newEvent event = new newEvent();

            Map<String, Object> data = new HashMap<>();

            data.put("lat", lat);
            data.put("lng", lng);
            data.put("date", date);
            data.put("time", time);
            data.put("stadt", Stadt);
            data.put("straße", Straße);
            data.put("hnr", Hnr);
            data.put("name", eventname);
            data.put("kategorie", kategorie);
            data.put("counter", 0);

            event.db.collection("events")
                    .add(data);

    }

    static void addbeventkord(double lat, double lng, String date, String time, String Straße, String Hnr, String Stadt, int preis, String eventname, String Plz,String kategorie, String File){
        newEvent event = new newEvent();
        //Events erstellen
        Map<String, Object> data = new HashMap<>();
        data.put("lat", lat);
        data.put("lng", lng);
        data.put("date", date);
        data.put("time", time);
        data.put("stadt", Stadt);
        data.put("straße", Straße);
        data.put("hnr", Hnr);
        data.put("preis", preis);
        data.put("name", eventname);
        data.put("counter", 0);
        data.put("plz", Plz);
        data.put("kategorie", kategorie);
        data.put("filepath", File);
        event.db.collection("events")
                .add(data);

        Map<String, Object> dataa = new HashMap<>();
        dataa.put("plz", Plz);
        dataa.put("stadt", Stadt);
        dataa.put("straße", Straße);
        dataa.put("hnr", Hnr);
        dataa.put("lat", lat);
        dataa.put("lng", lng);

        event.db.collection("locations")
                .add(dataa);
    }

    static void addbevent(double lat, double lng, String date, String time, String Straße, String Hnr, String Stadt, int preis, String eventname, String plz, String kategorie, String File){
        newEvent event = new newEvent();
        Map<String, Object> data = new HashMap<>();
        data.put("lat", lat);
        data.put("lng", lng);
        data.put("date", date);
        data.put("time", time);
        data.put("stadt", Stadt);
        data.put("straße", Straße);
        data.put("hnr", Hnr);
        data.put("preis", preis);
        data.put("name", eventname);
        data.put("counter", 0);
        data.put("plz", plz);
        data.put("kategorie", kategorie);
        data.put("filepath", File);
        event.db.collection("events")
                .add(data);
    }
}

public class newEvent extends AppCompatActivity implements MyCallback {
    private final String TAG = "NEWEVENT";

    private Button BtnSearch;
    private EditText eTStadt, eTStraße, eTHNR, eteventname, eTkategorieen;
    private TextView tVDate, tVTimee;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    private JSONObject temp, key, location;
    private String lat, lng, amPm;

    private int currentHour, currentMinute;

    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog timePickerDialog;


    private String api = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        /*


                    AUSGESETZT



        this.BtnSearch = (Button)findViewById(R.id.BtnSearch);
        this.eTStadt = (EditText)findViewById(R.id.eTStadt);
        this.eTStraße = (EditText)findViewById(R.id.eTStraße);
        this.eTHNR = (EditText)findViewById(R.id.eTHNR);
        this.tVDate = (TextView)findViewById(R.id.tVDate);
        this.tVTimee = (TextView)findViewById(R.id.tVTimee);
        this.eteventname = (EditText) findViewById(R.id.eteventname);
        this.eTkategorieen = (EditText)findViewById(R.id.eTkategorieen);


        Calendar cal = Calendar.getInstance();


        tVDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        newEvent.this,
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

                String date =  dayOfMonth + "/" +month + "/" +year;
                tVDate.setText(date);
            }
        };


        tVTimee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentHour = cal.get(Calendar.HOUR_OF_DAY);
                currentMinute = cal.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(
                        newEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                view.is24HourView();
                                view.setIs24HourView(true);
                                tVTimee.setText(String.format("%02d:%02d", hourOfDay,minute));
                            }
                        }, currentHour,currentMinute, false);
                timePickerDialog.show();
            }
        });

        BtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventexits(getStadt(),getStraße(),getHNr(),getDate(),getTime());
                Intent intent = new Intent(newEvent.this, mapbox.class);
                startActivity(intent);
            }
        });

    }

    public Context getContext(){
        return (Context)this;
    }

    public void jsonrequest(String url){

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

                            MyCallback.latlng(late, lnge, getDate(), getTime(), getStraße(), getHNr(), getStadt(), getevetname(), getkategorie());

                        } catch (JSONException e) {
                            Log.d(TAG, "Nicht gut");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "JO");
            }
        });
        Log.d(TAG, "FICK MICH NICHT");
        queue.add(request);
    }

    public String getStraße(){
        return eTStraße.getText().toString();
    }

    public String getHNr(){
        return eTHNR.getText().toString();
    }

    public String getStadt(){
        return eTStadt.getText().toString();
    }

    public String getDate(){
        return tVDate.getText().toString();
    }

    public String getTime(){
        return tVTimee.getText().toString();
    }

    public String getevetname(){
        return eteventname.getText().toString();
    }

    public String getkategorie(){
        return eTkategorieen.getText().toString();
    }

    public String getUrl(){
        String stadt = getStadt();
        String straße = getStraße();
        String hnr = getHNr();
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=" +stadt +"+" +straße +"+" +hnr +"+" +api);
        Log.d(TAG, url);
        return url;
    }

    public void eventexits(String City, String Street, String HNr, String Date, String Time){
        ArrayList<String> street = new ArrayList<String>();
        ArrayList<String> hnr = new ArrayList<String>();
        ArrayList<String> ort = new ArrayList<String>();
        ArrayList<String> date = new ArrayList<String>();
        ArrayList<String> time = new ArrayList<String>();
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

                                Log.d(TAG, "onCompl" +document);
                                Log.d(TAG, "street" +street);
                            }
                            if(ort.contains(City) && street.contains(Street) && hnr.contains(HNr) && date.contains(Date) && time.contains(Time)){
                                Toast.makeText(getContext(), "Gibt es schon",Toast.LENGTH_LONG).show();
                            }else{
                                Log.d(TAG, "location wird geladen" );
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
        ArrayList<Double> lat = new ArrayList<Double>();
        ArrayList<Double> lng = new ArrayList<Double>();
        Log.d(TAG, "locationindatabase: ICH LEBE");
        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "IRGENDWAS STIMMT HIER NICHT ");
                            for (QueryDocumentSnapshot document : task.getResult()){
                                ort.add(document.get("stadt").toString());
                                straße.add(document.get("straße").toString());
                                hnr.add(document.get("hnr").toString());
                                lat.add(Double.parseDouble(document.get("lat").toString()));
                                lng.add(Double.parseDouble(document.get("lng").toString()));
                            }
                        if(ort.contains(getStadt()) && straße.contains(getStraße()) && hnr.contains(getHNr())){
                            for(int i = 0; i< ort.size(); i++) {
                                Log.d(TAG, "FUCK" +ort.get(i));
                                if (ort.get(i).equals(getStadt()) && straße.get(i).equals(getStraße()) && hnr.get(i).equals(getHNr())) {
                                    Log.d(TAG, "ES IST IN DER DATENBANK ");
                                    MyCallback.addevent(lat.get(i), lng.get(i), getDate(), getTime(), getStraße(), getHNr(), getStadt(),getevetname(), getkategorie());
                                    break;
                                }
                                Log.d(TAG, "DIE IF ANWEISUNG IST KAPUTT AMKOI");
                            }
                        }else{
                            Log.d(TAG, "json? ");
                            jsonrequest(getUrl());
                            }
                        }
                    }
                });
    }

         */
    }
}
