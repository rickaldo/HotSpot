package com.example.databasetest.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.databasetest.Adapter.AllEvents_Adapter;
import com.example.databasetest.activity.Filtereinstellungen;
import com.example.databasetest.R;
import com.example.databasetest.Adapter.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.layers.TransitionOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.turf.TurfMeasurement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import static android.content.Context.LOCATION_SERVICE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.mapbox.mapboxsdk.style.expressions.Expression.division;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lt;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

public class MapboxFragment extends Fragment implements PermissionsListener {

    private MapView mapView;

    private MapboxMap mapboxMap;

    private PermissionsManager permissionsManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String api = "";
    private FirebaseAuth mAuth;
    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    private String startdate, enddate;
    private int endday,endmonth,endyear,startday,startmonth,startyear;
    private JSONObject temp, key, locationobject;

    private RecyclerView recyclerView;
    private EditText etsearchevents;
    private FloatingActionButton btnmylocation;

    private final String TAG ="Mapboxfragment";

    private LocationManager mLocationManager;

    private boolean markerSelected = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Mapbox.getInstance(getActivity(),"");
        return inflater.inflate(R.layout.layout_mapfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        this.etsearchevents = (EditText) getActivity().findViewById(R.id.etsearchevents3);
        this.recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_on_top_of_map3);
        this.btnmylocation = (FloatingActionButton) getActivity().findViewById(R.id.btnrecenter);

        mapView = getActivity().findViewById(R.id.mapView3);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::onMapReady);
    }

    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        MapboxFragment.this.mapboxMap = mapboxMap;
        SharedPreferences pref = getActivity().getSharedPreferences("map", 0);
        boolean link = pref.getBoolean("link",false);
        String layout = pref.getString("layout", "mapbox://styles/rickaldo/ck9fzm0451s231iqpio1e96ts");
        boolean onclick = pref.getBoolean("topeventsonclick",false);
        mapboxMap.setStyle(new Style.Builder().fromUri(layout), new Style.OnStyleLoaded() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                if(onclick){
                    enableLocationComponent(style);
                    ArrayList<String>names = new ArrayList<>();
                    ArrayList<String>dates = new ArrayList<>();
                    ArrayList<String>address = new ArrayList<>();
                    ArrayList<String>time = new ArrayList<>();
                    ArrayList<String>eid = new ArrayList<>();
                    ArrayList<String>filepath = new ArrayList<>();
                    ArrayList<Double>lng = new ArrayList<>();
                    ArrayList<Double>lat = new ArrayList<>();

                    lat.add(Double.valueOf(pref.getString("tplat", null)));
                    lng.add(Double.valueOf(pref.getString("tplng", null)));
                    names.add(pref.getString("tpname",null));
                    dates.add(pref.getString("tpdate",null));
                    time.add(pref.getString("tptime",null));
                    address.add(pref.getString("tpadress",null));
                    eid.add(pref.getString("tpevid",null));
                    String uid = pref.getString("tpuid",null);
                    filepath.add(pref.getString("tpfilepath",null));

                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(lat.get(0), lng.get(0)))
                            .zoom(14)
                            .tilt(20)
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 70);
                    style.setTransition(new TransitionOptions(0, 0, false));
                    umkreistest(style,mapboxMap,mapView);

                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(names, dates,time, address,uid,eid,lat,lng,filepath, getContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    TypedValue a = new TypedValue();
                    getActivity().getTheme().resolveAttribute(android.R.attr.windowBackground,a,true);
                    int color = a.data;
                    Drawable d = getResources().getDrawable(R.drawable.myrect,getActivity().getTheme());
                    d.setTint(color);
                    recyclerView.setBackground(d);
                    recyclerView.setVisibility(View.VISIBLE);
                    btnmylocation.setVisibility(View.GONE);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("topeventsonclick",false).apply();
                }else if(link){
                    enableLocationComponent(style);
                    String id = pref.getString("linkid", "");
                    db.collection("events")
                            .document(id)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        ArrayList<String>names = new ArrayList<>();
                                        ArrayList<String>dates = new ArrayList<>();
                                        ArrayList<String>address = new ArrayList<>();
                                        ArrayList<String>time = new ArrayList<>();
                                        ArrayList<String>eid = new ArrayList<>();
                                        ArrayList<String>filepath = new ArrayList<>();
                                        ArrayList<Double>lng = new ArrayList<>();
                                        ArrayList<Double>lat = new ArrayList<>();

                                        names.add(task.getResult().get("name").toString());
                                        dates.add(task.getResult().get("date").toString());
                                        address.add(task.getResult().get("straße").toString() +" " +task.getResult().get("hnr"));
                                        time.add(task.getResult().get("time").toString());
                                        eid.add(id);
                                        filepath.add(task.getResult().get("filepath").toString());
                                        lat.add(Double.parseDouble(task.getResult().get("lat").toString()));
                                        lng.add(Double.parseDouble(task.getResult().get("lng").toString()));
                                        CameraPosition position = new CameraPosition.Builder()
                                                .target(new LatLng(lat.get(0), lng.get(0)))
                                                .zoom(14)
                                                .tilt(20)
                                                .build();
                                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 70);
                                        style.setTransition(new TransitionOptions(0, 0, false));
                                        umkreistest(style,mapboxMap,mapView);

                                        RecyclerViewAdapter adapter = new RecyclerViewAdapter(names, dates,time, address,mAuth.getUid(),eid,lat,lng,filepath, getContext());
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        TypedValue a = new TypedValue();
                                        getActivity().getTheme().resolveAttribute(android.R.attr.windowBackground,a,true);
                                        int color = a.data;
                                        Drawable d = getResources().getDrawable(R.drawable.myrect,getActivity().getTheme());
                                        d.setTint(color);
                                        recyclerView.setBackground(d);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        btnmylocation.setVisibility(View.GONE);

                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putBoolean("link",false).apply();
                                    }
                                }
                            });
                }else {
                    enableLocationComponent(style);
                    umkreistest(style,mapboxMap,mapView);
                }
                UiSettings uiSettings = mapboxMap.getUiSettings();
                uiSettings.setCompassEnabled(false);

                mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onCameraMove() {
                        btnmylocation.setVisibility(View.VISIBLE);
                    }
                });

                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
                        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel, "unclustered-points");
                        if (features.size() == 1) {
                            for (Feature feature : features) {
                                String test = feature.getStringProperty("name");
                                addfeste(test);
                            }
                            return true;
                        }else if (features.size() > 1){
                            ArrayList<String> temp = new ArrayList<>();
                            for(Feature feature : features){
                                String test = feature.getStringProperty("name");
                                temp.add(test);
                            }
                            showeventsview(temp);
                            return true;
                        }else{
                            recyclerView.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });

                etsearchevents.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int DRAWABLE_RIGHT = 2;
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            if(event.getRawX() >= (etsearchevents.getRight() - etsearchevents.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                                Intent intent = new Intent(getContext(), Filtereinstellungen.class);
                                startActivity(intent);
                                return true;
                            }
                        }
                        return false;
                    }
                });

                btnmylocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enableLocationComponent(style);
                    }
                });

                etsearchevents.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if(actionId == EditorInfo.IME_ACTION_SEND){
                            String text = etsearchevents.getText().toString();
                            ArrayList<String> names = new ArrayList<>();
                            ArrayList<String> address = new ArrayList<>();
                            ArrayList<String> time = new ArrayList<>();
                            ArrayList<String> eid = new ArrayList<>();
                            ArrayList<String> dates = new ArrayList<>();
                            ArrayList<Double> lat = new ArrayList<>();
                            ArrayList<Double> lng = new ArrayList<>();
                            ArrayList<String> filepath = new ArrayList<>();

                            db.collection("events")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    int ration = FuzzySearch.partialRatio(text, document.get("name").toString());
                                                    if (ration >= 70) {
                                                        eid.add(document.getId());
                                                        names.add(document.get("name").toString());
                                                        time.add(document.get("time").toString());
                                                        dates.add(document.get("date").toString());
                                                        address.add(document.get("straße").toString() + " " + document.get("hnr").toString());
                                                        lat.add(Double.parseDouble(document.get("lat").toString()));
                                                        lng.add(Double.parseDouble(document.get("lng").toString()));
                                                        filepath.add(document.get("filepath").toString());
                                                    }
                                                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(names, dates, time, address, mAuth.getUid(),
                                                            eid,lat, lng, filepath,getContext());
                                                    recyclerView.setAdapter(adapter);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
                                                    TypedValue a = new TypedValue();
                                                    getActivity().getTheme().resolveAttribute(android.R.attr.windowBackground,a,true);
                                                    int color = a.data;
                                                    Drawable d = getResources().getDrawable(R.drawable.myrect,getActivity().getTheme());
                                                    d.setTint(color);
                                                    recyclerView.setBackground(d);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    btnmylocation.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                    });
                            handled = true;
                            InputMethodManager mgr = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(etsearchevents.getWindowToken(),0);
                        }
                        return handled;
                    }});



            }
        });
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    private void umkreistest(Style stlye, MapboxMap mapboxMap, MapView mapView){
        stlye.addImage("hotspot",BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.hotspotsmall)));

        SharedPreferences pref = getActivity().getSharedPreferences("map", 0);
        SharedPreferences.Editor edit = pref.edit();
        String tmpstadt = pref.getString("ort","no");
        int tmpumkreis = pref.getInt("umkreis", 150);
        double umkreis = (double) tmpumkreis;
        int preiseinstellung = pref.getInt("preis",300);

        ArrayList<String> kategorien = kategorien();

        Calendar cal = Calendar.getInstance();
        Date c = cal.getTime();
        Calendar test = Calendar.getInstance();
        test.setTime(new Date());
        test.add(Calendar.MONTH,1);
        Date d = test.getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedstart = df.format(c);
        String formattedend = df.format(d);

        startdate = pref.getString("datum",formattedstart);
        enddate = pref.getString("datum2", formattedend);

        if(startdate.equals("no")){
            startdate = formattedstart;
            enddate = formattedend;
        }
        if(tmpstadt.equals("no")) {

            Location location = getLastKnownLocation();
            assert location != null;
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Point userlocation = Point.fromLngLat(lng, lat);

            Point norden = TurfMeasurement.destination(userlocation, umkreis, 0, "kilometers");
            Point osten = TurfMeasurement.destination(userlocation, umkreis, 90, "kilometers");
            Point süden = TurfMeasurement.destination(userlocation, umkreis, 180, "kilometers");
            Point westen = TurfMeasurement.destination(userlocation, umkreis, -90, "kilometers");

            double nordenlat = norden.latitude();
            double südlat = süden.latitude();
            double ostenlng = osten.longitude();
            double westenlng = westen.longitude();

            ArrayList<Double> latitude = new ArrayList<>();
            ArrayList<Double> longitude = new ArrayList<>();
            ArrayList<String> dates = new ArrayList<>();
            ArrayList<Integer> preisliste2 = new ArrayList<>();

            ArrayList<Double> latitude2 = new ArrayList<>();
            ArrayList<Double> longitude2 = new ArrayList<>();
            ArrayList<String> dates2 = new ArrayList<>();
            ArrayList<Integer> preisliste = new ArrayList<>();

            ArrayList<String> latid = new ArrayList<>();
            ArrayList<String> lngid = new ArrayList<>();

            ArrayList<String> kategorie = new ArrayList<>();
            ArrayList<String> kategorie2 = new ArrayList<>();

            ArrayList<String> tmpname = new ArrayList<>();
            ArrayList<String> tmpfilepath = new ArrayList<>();
            ArrayList<String> tmpadresse = new ArrayList<>();
            ArrayList<String> tmpzeit = new ArrayList<>();
            ArrayList<String> tmpdatum = new ArrayList<>();

            ArrayList<String> tmpname2 = new ArrayList<>();
            ArrayList<String> tmpfilepath2 = new ArrayList<>();
            ArrayList<String> tmpadresse2 = new ArrayList<>();
            ArrayList<String> tmpzeit2 = new ArrayList<>();
            ArrayList<String> tmpdatum2 = new ArrayList<>();

            ArrayList<String> finalname = new ArrayList<>();
            ArrayList<String> finalfilepath = new ArrayList<>();
            ArrayList<String> finaladresse = new ArrayList<>();
            ArrayList<String> finalzeit = new ArrayList<>();
            ArrayList<String> finaldatum = new ArrayList<>();

            List<Feature> features = new ArrayList<>();

            db.collection("events")
                    .whereGreaterThanOrEqualTo("lat", südlat)
                    .whereLessThanOrEqualTo("lat", nordenlat)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    latid.add(document.getId());
                                    longitude.add(Double.parseDouble(document.get("lng").toString()));
                                    latitude.add(Double.parseDouble(document.get("lat").toString()));
                                    dates.add(document.get("date").toString());
                                    kategorie.add(document.get("kategorie").toString());
                                    preisliste2.add(Integer.parseInt(document.get("preis").toString()));

                                    tmpname.add(document.get("name").toString());
                                    tmpadresse.add(document.get("straße").toString() +" " +document.get("hnr").toString());
                                    tmpzeit.add(document.get("time").toString());
                                    tmpdatum.add(document.get("date").toString());
                                    tmpfilepath.add(document.get("filepath").toString());
                                }
                                db.collection("events")
                                        .whereGreaterThanOrEqualTo("lng", westenlng)
                                        .whereLessThanOrEqualTo("lng", ostenlng)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        lngid.add(document.getId());
                                                        longitude2.add(Double.parseDouble(document.get("lng").toString()));
                                                        latitude2.add(Double.parseDouble(document.get("lat").toString()));
                                                        dates2.add(document.get("date").toString());
                                                        kategorie2.add(document.get("kategorie").toString());
                                                        preisliste.add(Integer.parseInt(document.get("preis").toString()));

                                                        tmpname2.add(document.get("name").toString());
                                                        tmpadresse2.add(document.get("straße").toString() +" " +document.get("hnr").toString());
                                                        tmpzeit2.add(document.get("time").toString());
                                                        tmpdatum2.add(document.get("date").toString());
                                                        tmpfilepath2.add(document.get("filepath").toString());
                                                    }
                                                }
                                                if(startdate.length() < 11) {
                                                    startday = Integer.parseInt(startdate.substring(0,2));
                                                    startmonth = Integer.parseInt(startdate.substring(3,5));
                                                    startyear = Integer.parseInt(startdate.substring(6,10));
                                                    if (!enddate.equals("no")) {
                                                        endday = Integer.parseInt(enddate.substring(0,2));
                                                        endmonth = Integer.parseInt(enddate.substring(3,5));
                                                        endyear = Integer.parseInt(enddate.substring(6,10));
                                                    }
                                                }else{
                                                    startday = Integer.parseInt(startdate.substring(0,2));
                                                    String tmpstartmonth = startdate.substring(3,6);
                                                    startmonth = Integer.parseInt(getmonthnumber(tmpstartmonth));
                                                    startyear = Integer.parseInt(startdate.substring(7,11));
                                                    if (!enddate.equals("no")) {
                                                        endday = Integer.parseInt(enddate.substring(0,2));
                                                        String tmpendmonth = enddate.substring(3,6);
                                                        endmonth = Integer.parseInt(getmonthnumber(tmpendmonth));
                                                        endyear = Integer.parseInt(enddate.substring(7, 11));
                                                    }
                                                }
                                                if (lngid.size() >= latid.size()) {
                                                    for (int i = 0; i < latid.size(); i++) {
                                                        if (lngid.contains(latid.get(i))) {
                                                            Point tmppoint = Point.fromLngLat(longitude.get(i), latitude.get(i));
                                                            double distance = TurfMeasurement.distance(userlocation, tmppoint);
                                                            if (distance < umkreis) {
                                                                if(kategorien.contains(kategorie.get(i))) {
                                                                    int eventdate = Integer.parseInt(dates.get(i).substring(0, 2));
                                                                    int eventmonth = Integer.parseInt(dates.get(i).substring(3, 5));
                                                                    int eventyear = Integer.parseInt(dates.get(i).substring(6, 10));
                                                                    if (!enddate.equals("no")) {
                                                                        if (eventyear <= endyear && eventmonth <= endmonth && eventdate <= endyear
                                                                                && eventyear >= startyear && eventmonth >= startmonth && eventdate >= startday) {
                                                                            if (preisliste2.get(i) <= preiseinstellung) {
                                                                                finaladresse.add(tmpadresse.get(i));
                                                                                finaldatum.add(tmpdatum.get(i));
                                                                                finalfilepath.add(tmpfilepath.get(i));
                                                                                finalname.add(tmpname.get(i));
                                                                                finalzeit.add(tmpzeit.get(i));

                                                                                Geometry geometry = Point.fromLngLat(longitude.get(i),latitude.get(i));
                                                                                Feature amena = Feature.fromGeometry(geometry);
                                                                                amena.addStringProperty("name", latid.get(i));
                                                                                features.add(amena);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        if (eventdate == startday && eventmonth == startmonth && eventyear == startyear) {
                                                                            if (preisliste2.get(i) <= preiseinstellung) {
                                                                                finaladresse.add(tmpadresse.get(i));
                                                                                finaldatum.add(tmpdatum.get(i));
                                                                                finalfilepath.add(tmpfilepath.get(i));
                                                                                finalname.add(tmpname.get(i));
                                                                                finalzeit.add(tmpzeit.get(i));

                                                                                Geometry geometry = Point.fromLngLat(longitude.get(i),latitude.get(i));
                                                                                Feature amena = Feature.fromGeometry(geometry);
                                                                                amena.addStringProperty("name", latid.get(i));
                                                                                features.add(amena);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }else{
                                                    for (int i = 0; i < lngid.size(); i++) {
                                                        if (latid.contains(lngid.get(i))) {
                                                            Point tmppoint = Point.fromLngLat(longitude2.get(i), latitude2.get(i));
                                                            double distance = TurfMeasurement.distance(userlocation, tmppoint);
                                                            if (distance < umkreis) {
                                                                if (kategorien.contains(kategorie2.get(i))) {
                                                                    int eventdate = Integer.parseInt(dates2.get(i).substring(0, 2));
                                                                    int eventmonth = Integer.parseInt(dates2.get(i).substring(3, 5));
                                                                    int eventyear = Integer.parseInt(dates2.get(i).substring(6, 10));
                                                                    if (!enddate.equals("no")) {
                                                                        if (eventyear <= endyear && eventmonth <= endmonth && eventdate <= endyear
                                                                                && eventyear >= startyear && eventmonth >= startmonth && eventdate >= startday) {
                                                                            if (preisliste.get(i) <= preiseinstellung) {
                                                                                finaladresse.add(tmpadresse2.get(i));
                                                                                finaldatum.add(tmpdatum2.get(i));
                                                                                finalfilepath.add(tmpfilepath2.get(i));
                                                                                finalname.add(tmpname2.get(i));
                                                                                finalzeit.add(tmpzeit2.get(i));

                                                                                Geometry geometry = Point.fromLngLat(longitude2.get(i),latitude2.get(i));
                                                                                Feature amena = Feature.fromGeometry(geometry);
                                                                                amena.addStringProperty("name", lngid.get(i));
                                                                                features.add(amena);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        if (eventdate == startday && eventmonth == startmonth && eventyear == startyear) {
                                                                            if (preisliste.get(i) <= preiseinstellung) {
                                                                                finaladresse.add(tmpadresse2.get(i));
                                                                                finaldatum.add(tmpdatum2.get(i));
                                                                                finalfilepath.add(tmpfilepath2.get(i));
                                                                                finalname.add(tmpname2.get(i));
                                                                                finalzeit.add(tmpzeit2.get(i));

                                                                                Geometry geometry = Point.fromLngLat(longitude2.get(i),latitude2.get(i));
                                                                                Feature amena = Feature.fromGeometry(geometry);
                                                                                amena.addStringProperty("name", lngid.get(i));
                                                                                features.add(amena);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
                                                GeoJsonSource source = new GeoJsonSource("markers",featureCollection,new GeoJsonOptions().withCluster(true));

                                                stlye.addSource(source);

                                                SymbolLayer unclustered = new SymbolLayer("unclustered-points","markers")
                                                        .withProperties(
                                                                iconImage("hotspot"),
                                                                iconOffset(new Float[]{0f,-9f})
                                                        );

                                                stlye.addLayer(unclustered);

                                                stlye.addLayer(new SymbolLayer("count", "markers").withProperties(
                                                        textField(Expression.toString(get("point_count"))),
                                                        textSize(18f),
                                                        textColor(Color.BLACK),
                                                        textIgnorePlacement(true),
                                                        textOffset(new Float[]{0f, -0.5f}),
                                                        textAllowOverlap(true)
                                                ));
                                            }
                                        });
                            }
                        }
                    });
        }else {
            sucheinandererStadt(tmpstadt, umkreis, preiseinstellung,startdate,enddate,kategorien,stlye);
        }
    }

    private void sucheinandererStadt(String stadt, double umkreis, int preiseinstellung, String startdate, String enddate, ArrayList<String> getkategorien, Style stlye){

        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=" +stadt +"+" +api);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
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

                            locationobject = key.optJSONObject("location");

                            double lat = Double.parseDouble(locationobject.getString("lat"));

                            double lng = Double.parseDouble(locationobject.getString("lng"));

                            Point Stadtlocation = Point.fromLngLat(lng, lat);

                            Point norden = TurfMeasurement.destination(Stadtlocation, umkreis, 0, "kilometers");
                            Point osten = TurfMeasurement.destination(Stadtlocation, umkreis, 90, "kilometers");
                            Point süden = TurfMeasurement.destination(Stadtlocation, umkreis, 180, "kilometers");
                            Point westen = TurfMeasurement.destination(Stadtlocation, umkreis, -90, "kilometers");

                            double nordenlat = norden.latitude();
                            double südlat = süden.latitude();
                            double ostenlng = osten.longitude();
                            double westenlng = westen.longitude();

                            ArrayList<Double> latitude = new ArrayList<>();
                            ArrayList<Double> longitude = new ArrayList<>();
                            ArrayList<String> dates = new ArrayList<>();
                            ArrayList<Integer> preisliste2 = new ArrayList<>();

                            ArrayList<Double> latitude2 = new ArrayList<>();
                            ArrayList<Double> longitude2 = new ArrayList<>();
                            ArrayList<String> dates2 = new ArrayList<>();
                            ArrayList<Integer> preisliste = new ArrayList<>();

                            ArrayList<String> latid = new ArrayList<>();
                            ArrayList<String> lngid = new ArrayList<>();

                            ArrayList<String> kategorie = new ArrayList<>();
                            ArrayList<String> kategorie2 = new ArrayList<>();

                            ArrayList<String> tmpname = new ArrayList<>();
                            ArrayList<String> tmpfilepath = new ArrayList<>();
                            ArrayList<String> tmpadresse = new ArrayList<>();
                            ArrayList<String> tmpzeit = new ArrayList<>();
                            ArrayList<String> tmpdatum = new ArrayList<>();

                            ArrayList<String> tmpname2 = new ArrayList<>();
                            ArrayList<String> tmpfilepath2 = new ArrayList<>();
                            ArrayList<String> tmpadresse2 = new ArrayList<>();
                            ArrayList<String> tmpzeit2 = new ArrayList<>();
                            ArrayList<String> tmpdatum2 = new ArrayList<>();

                            ArrayList<String> finalname = new ArrayList<>();
                            ArrayList<String> finalfilepath = new ArrayList<>();
                            ArrayList<String> finaladresse = new ArrayList<>();
                            ArrayList<String> finalzeit = new ArrayList<>();
                            ArrayList<String> finaldatum = new ArrayList<>();

                            List<Feature> features = new ArrayList<>();

                            db.collection("events")
                                    .whereGreaterThanOrEqualTo("lat", südlat)
                                    .whereLessThanOrEqualTo("lat", nordenlat)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    latid.add(document.getId());
                                                    longitude.add(Double.parseDouble(document.get("lng").toString()));
                                                    latitude.add(Double.parseDouble(document.get("lat").toString()));
                                                    dates.add(document.get("date").toString());
                                                    kategorie.add(document.get("kategorie").toString());
                                                    preisliste2.add(Integer.parseInt(document.get("preis").toString()));

                                                    tmpname.add(document.get("name").toString());
                                                    tmpadresse.add(document.get("straße").toString() +" " +document.get("hnr").toString());
                                                    tmpzeit.add(document.get("time").toString());
                                                    tmpdatum.add(document.get("date").toString());
                                                    tmpfilepath.add(document.get("filepath").toString());
                                                }
                                                db.collection("events")
                                                        .whereGreaterThanOrEqualTo("lng", westenlng)
                                                        .whereLessThanOrEqualTo("lng", ostenlng)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        lngid.add(document.getId());
                                                                        longitude2.add(Double.parseDouble(document.get("lng").toString()));
                                                                        latitude2.add(Double.parseDouble(document.get("lat").toString()));
                                                                        dates2.add(document.get("date").toString());
                                                                        kategorie2.add(document.get("kategorie").toString());
                                                                        preisliste.add(Integer.parseInt(document.get("preis").toString()));

                                                                        tmpname2.add(document.get("name").toString());
                                                                        tmpadresse2.add(document.get("straße").toString() +" " +document.get("hnr").toString());
                                                                        tmpzeit2.add(document.get("time").toString());
                                                                        tmpdatum2.add(document.get("date").toString());
                                                                        tmpfilepath2.add(document.get("filepath").toString());
                                                                    }
                                                                }
                                                                if(startdate.length() < 11) {
                                                                    startday = Integer.parseInt(startdate.substring(0,2));
                                                                    startmonth = Integer.parseInt(startdate.substring(3,5));
                                                                    startyear = Integer.parseInt(startdate.substring(6,10));
                                                                    if (!enddate.equals("no")) {
                                                                        endday = Integer.parseInt(enddate.substring(0,2));
                                                                        endmonth = Integer.parseInt(enddate.substring(3,5));
                                                                        endyear = Integer.parseInt(enddate.substring(6,10));
                                                                    }
                                                                }else{
                                                                    startday = Integer.parseInt(startdate.substring(0,2));
                                                                    String tmpstartmonth = startdate.substring(3,6);
                                                                    startmonth = Integer.parseInt(getmonthnumber(tmpstartmonth));
                                                                    startyear = Integer.parseInt(startdate.substring(7,11));
                                                                    if (!enddate.equals("no")) {
                                                                        endday = Integer.parseInt(enddate.substring(0,2));
                                                                        String tmpendmonth = enddate.substring(3,6);
                                                                        endmonth = Integer.parseInt(getmonthnumber(tmpendmonth));
                                                                        endyear = Integer.parseInt(enddate.substring(7, 11));
                                                                    }
                                                                }
                                                                if (lngid.size() >= latid.size()) {
                                                                    for (int i = 0; i < latid.size(); i++) {
                                                                        if (lngid.contains(latid.get(i))) {
                                                                            Point tmppoint = Point.fromLngLat(longitude.get(i), latitude.get(i));
                                                                            double distance = TurfMeasurement.distance(Stadtlocation, tmppoint);
                                                                            if (distance < umkreis) {
                                                                                if(getkategorien.contains(kategorie.get(i))) {
                                                                                    int eventdate = Integer.parseInt(dates.get(i).substring(0, 2));
                                                                                    int eventmonth = Integer.parseInt(dates.get(i).substring(3, 5));
                                                                                    int eventyear = Integer.parseInt(dates.get(i).substring(6, 10));
                                                                                    if (!enddate.equals("no")) {
                                                                                        if (eventyear <= endyear && eventmonth <= endmonth && eventdate <= endyear
                                                                                                && eventyear >= startyear && eventmonth >= startmonth && eventdate >= startday) {
                                                                                            if (preisliste2.get(i) <= preiseinstellung) {
                                                                                                finaladresse.add(tmpadresse.get(i));
                                                                                                finaldatum.add(tmpdatum.get(i));
                                                                                                finalfilepath.add(tmpfilepath.get(i));
                                                                                                finalname.add(tmpname.get(i));
                                                                                                finalzeit.add(tmpzeit.get(i));

                                                                                                Geometry geometry = Point.fromLngLat(longitude.get(i),latitude.get(i));
                                                                                                Feature amena = Feature.fromGeometry(geometry);
                                                                                                features.add(amena);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        if (eventdate == startday && eventmonth == startmonth && eventyear == startyear) {
                                                                                            if (preisliste2.get(i) <= preiseinstellung) {
                                                                                                finaladresse.add(tmpadresse.get(i));
                                                                                                finaldatum.add(tmpdatum.get(i));
                                                                                                finalfilepath.add(tmpfilepath.get(i));
                                                                                                finalname.add(tmpname.get(i));
                                                                                                finalzeit.add(tmpzeit.get(i));

                                                                                                Geometry geometry = Point.fromLngLat(longitude.get(i),latitude.get(i));
                                                                                                Feature amena = Feature.fromGeometry(geometry);
                                                                                                features.add(amena);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }else{
                                                                    for (int i = 0; i < lngid.size(); i++) {
                                                                        if (latid.contains(lngid.get(i))) {
                                                                            Point tmppoint = Point.fromLngLat(longitude2.get(i), latitude2.get(i));
                                                                            double distance = TurfMeasurement.distance(Stadtlocation, tmppoint);
                                                                            if (distance < umkreis) {
                                                                                if (getkategorien.contains(kategorie2.get(i))) {
                                                                                    int eventdate = Integer.parseInt(dates2.get(i).substring(0, 2));
                                                                                    int eventmonth = Integer.parseInt(dates2.get(i).substring(3, 5));
                                                                                    int eventyear = Integer.parseInt(dates2.get(i).substring(6, 10));
                                                                                    if (!enddate.equals("no")) {
                                                                                        if (eventyear <= endyear && eventmonth <= endmonth && eventdate <= endyear
                                                                                                && eventyear >= startyear && eventmonth >= startmonth && eventdate >= startday) {
                                                                                            if (preisliste.get(i) <= preiseinstellung) {
                                                                                                finaladresse.add(tmpadresse2.get(i));
                                                                                                finaldatum.add(tmpdatum2.get(i));
                                                                                                finalfilepath.add(tmpfilepath2.get(i));
                                                                                                finalname.add(tmpname2.get(i));
                                                                                                finalzeit.add(tmpzeit2.get(i));

                                                                                                Geometry geometry = Point.fromLngLat(longitude2.get(i),latitude2.get(i));
                                                                                                Feature amena = Feature.fromGeometry(geometry);
                                                                                                features.add(amena);
                                                                                            }
                                                                                        }
                                                                                    } else {
                                                                                        if (eventdate == startday && eventmonth == startmonth && eventyear == startyear) {
                                                                                            if (preisliste.get(i) <= preiseinstellung) {
                                                                                                finaladresse.add(tmpadresse2.get(i));
                                                                                                finaldatum.add(tmpdatum2.get(i));
                                                                                                finalfilepath.add(tmpfilepath2.get(i));
                                                                                                finalname.add(tmpname2.get(i));
                                                                                                finalzeit.add(tmpzeit2.get(i));

                                                                                                Geometry geometry = Point.fromLngLat(longitude2.get(i),latitude2.get(i));
                                                                                                Feature amena = Feature.fromGeometry(geometry);
                                                                                                features.add(amena);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
                                                                GeoJsonSource source = new GeoJsonSource("markers",featureCollection,new GeoJsonOptions().withCluster(true));

                                                                stlye.addSource(source);

                                                                SymbolLayer unclustered = new SymbolLayer("unclustered-points","markers")
                                                                        .withProperties(
                                                                                iconImage("hotspot")
                                                                        );

                                                                stlye.addLayer(unclustered);

                                                                stlye.addLayer(new SymbolLayer("count", "markers").withProperties(
                                                                        textField(Expression.toString(get("point_count"))),
                                                                        textSize(18f),
                                                                        textColor(Color.BLACK),
                                                                        textIgnorePlacement(true),
                                                                        textOffset(new Float[]{0f, .13f}),
                                                                        textAllowOverlap(true)
                                                                ));
                                                            }
                                                        });
                                                CameraPosition position = new CameraPosition.Builder()
                                                        .target(new LatLng(lat, lng))
                                                        .zoom(10)
                                                        .tilt(20)
                                                        .build();
                                                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 70);
                                            }
                                        }});
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);
    }

    private void addfeste(String names) {
        //On Markertouch
        String uid = mAuth.getCurrentUser().getUid();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<String> address = new ArrayList<>();
        ArrayList<String> eid = new ArrayList<>();
        ArrayList<Double> late = new ArrayList<>();
        ArrayList<Double> lnge = new ArrayList<>();
        ArrayList<String> filepath = new ArrayList<>();
        recyclerView.setVisibility(View.VISIBLE);
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();

                                if(id.equals(names)){
                                    name.add(document.get("name").toString());
                                    time.add(document.get("time").toString());
                                    String street = document.get("straße").toString();
                                    String hnr = document.get("hnr").toString();
                                    date.add(document.get("date").toString());
                                    address.add(street +" " +hnr);
                                    eid.add(document.getId());
                                    late.add(Double.parseDouble(document.get("lat").toString()));
                                    lnge.add(Double.parseDouble(document.get("lng").toString()));
                                    filepath.add(document.get("filepath").toString());

                                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(name, date,time, address,uid,eid,late,lnge,filepath,getContext());
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    recyclerView.setVisibility(View.VISIBLE);
                                    btnmylocation.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
    }

    public void showeventsview(ArrayList<String> names){
        String uid = mAuth.getCurrentUser().getUid();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> date = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<String> address = new ArrayList<>();
        ArrayList<String> eid = new ArrayList<>();
        ArrayList<Double> late = new ArrayList<>();
        ArrayList<Double> lnge = new ArrayList<>();
        ArrayList<String> filepath = new ArrayList<>();
        recyclerView.setVisibility(View.VISIBLE);
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                if(names.contains(id)){
                                    name.add(document.get("name").toString());
                                    time.add(document.get("time").toString());
                                    String street = document.get("straße").toString();
                                    String hnr = document.get("hnr").toString();
                                    date.add(document.get("date").toString());
                                    address.add(street +" " +hnr);
                                    eid.add(document.getId());
                                    late.add(Double.parseDouble(document.get("lat").toString()));
                                    lnge.add(Double.parseDouble(document.get("lng").toString()));
                                    filepath.add(document.get("filepath").toString());

                                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(name, date,time, address,uid,eid,late,lnge,filepath,getContext());
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
    }

    public String getmonthnumber(String month){
        String num = "00";

        if(month.equals("Jan")){
            num = "01";
        }else if(month.equals("Feb")){
            num = "02";
        }else if(month.equals("Mar")){
            num = "03";
        }else if(month.equals("Apr")){
            num = "04";
        }else if(month.equals("May")){
            num = "05";
        }else if(month.equals("Jun")){
            num = "06";
        }else if(month.equals("Jul")){
            num = "07";
        }else if(month.equals("Aug")){
            num = "08";
        }else if(month.equals("Sep")){
            num = "09";
        }else if(month.equals("Oct")){
            num = "10";
        }else if(month.equals("Nov")){
            num = "11";
        }else if(month.equals("Dec")){
            num = "12";
        }
        return num;
    }
    private ArrayList<String> kategorien(){
        SharedPreferences pref = getActivity().getSharedPreferences("map",0);

        boolean feste = pref.getBoolean("feste",true);
        boolean kultur = pref.getBoolean("kultur",true);
        boolean sport = pref.getBoolean("sport",true);
        boolean musik = pref.getBoolean("musik",true);
        boolean flohmarkt = pref.getBoolean("flohmarkt",true);
        boolean info= pref.getBoolean("info",true);

        ArrayList<String> tmp = new ArrayList<>();

        if(feste){
            tmp.add("feste");
        }
        if(kultur){
            tmp.add("kultur");
        }
        if(sport){
            tmp.add("sport");
        }
        if(musik){
            tmp.add("musik");
        }
        if(flohmarkt){
            tmp.add("flohmarkt");
        }
        if(info){
            tmp.add("info");
        }
        return tmp;
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            // Get an instance of the LocationComponent.
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate the LocationComponent
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());

            // Enable the LocationComponent so that it's actually visible on the map
            locationComponent.setLocationComponentEnabled(true);

            // Set the LocationComponent's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the LocationComponent's render mode
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(), R.string.Toast_PermissionExplanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(getContext(), R.string.Toast_PermissionNotGranted, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
}
