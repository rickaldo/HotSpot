package com.example.databasetest.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.databasetest.R;
import com.example.databasetest.fragment_activities.Fragment_Activity;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Filtereinstellungen extends AppCompatActivity {
    private final String TAG = "FILTEREINSTELLUNGEN";

    private SeekBar sb_entfernung, sb_preis;

    private Switch sw_verifizierte_user;

    private EditText et_filter_stadt;

    private TextView tv_filter_showumkreis,tv_filter_kategorien,tv_filter_datum,tv_filter_showprice;

    private Button btn_filter_anwenden, btn_filter_abbrechen, btn_datum_bestätigen;

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtereinstellungen);

        SharedPreferences pref = getSharedPreferences("map",0);
        int distance = pref.getInt("umkreis", 150);
        int preis = pref.getInt("preis", 300);

        this.et_filter_stadt = (EditText) findViewById(R.id.et_filter_stadt);
        //this.sw_verifizierte_user = (Switch) findViewById(R.id.sw_verifizierte_user);

        this.tv_filter_datum = (TextView) findViewById(R.id.tv_filter_datum);
        this.tv_filter_kategorien = (TextView) findViewById(R.id.tv_filter_kategorien);
        this.tv_filter_showprice = (TextView) findViewById(R.id.tv_filter_showprice);
        this.tv_filter_showumkreis = (TextView) findViewById(R.id.tv_filter_showumkreis);

        this.btn_filter_abbrechen = (Button) findViewById(R.id.btn_filter_abbrechen);
        this.btn_filter_anwenden = (Button) findViewById(R.id.btn_filter_anwenden);
        this.btn_datum_bestätigen = (Button)findViewById(R.id.btn_datum_bestätigen);

        this.sb_entfernung = (SeekBar) findViewById(R.id.sB_Entfernung);
        sb_entfernung.setProgress(distance);
        String test = String.valueOf(sb_entfernung.getProgress()) + "km";
        tv_filter_showumkreis.setText(test);
        this.sb_preis = (SeekBar) findViewById(R.id.sB_Preis);
        sb_preis.setProgress(preis);
        String tmp = String.valueOf(sb_preis.getProgress()) + "€";
        tv_filter_showprice.setText(tmp);


        sb_entfernung.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String text = String.valueOf(progress) + "km";
                tv_filter_showumkreis.setText(text);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_preis.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String text = String.valueOf(progress) + "€";
                tv_filter_showprice.setText(text);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_filter_abbrechen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Filtereinstellungen.this, Fragment_Activity.class);
                startActivity(intent);
            }
        });

        tv_filter_datum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayout gl_show_date = findViewById(R.id.gl_show_date);
                btn_filter_abbrechen.setVisibility(View.GONE);
                btn_filter_anwenden.setVisibility(View.GONE);
                btn_datum_bestätigen.setVisibility(View.VISIBLE);
                gl_show_date.setVisibility(View.VISIBLE);


                CalendarPickerView datePicker = findViewById(R.id.calendar);
                Date today = new Date();
                Calendar nextYear = Calendar.getInstance();
                nextYear.add(Calendar.YEAR, 1);

                datePicker.init(today,nextYear.getTime())
                        .inMode(CalendarPickerView.SelectionMode.RANGE)
                        .withSelectedDate(today);

                datePicker.bringToFront();
                datePicker.setVisibility(View.VISIBLE);

                datePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(Date date) {
                        Calendar calSelected = Calendar.getInstance();
                        calSelected.setTime(date);

                        String selectedDate = "" + calSelected.get(Calendar.DAY_OF_MONTH)
                                + " " + (calSelected.get(Calendar.MONTH) + 1)
                                + " " + calSelected.get(Calendar.YEAR);
                    }

                    @Override
                    public void onDateUnselected(Date date) {

                    }
                });
                btn_datum_bestätigen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Date> selected = datePicker.getSelectedDates();
                        int size = selected.size();
                        if(size > 1) {
                            String firstdate = selected.get(0).toString();
                            String lastdate = selected.get(size - 1).toString();


                            String firstdate_month = firstdate.substring(4, 7);
                            String firstdate_day = firstdate.substring(8, 10);
                            String firstdate_year = firstdate.substring(30, 34);

                            String firstday = firstdate_day + "/" + firstdate_month + "/" + firstdate_year;

                            String lastdate_month = lastdate.substring(4, 7);
                            String lastdate_day = lastdate.substring(8, 10);
                            String lastdate_year = lastdate.substring(30, 34);

                            String lastday = lastdate_day + "/" + lastdate_month + "/" + lastdate_year;

                            String ergebnis = firstday + "  -  " + lastday;
                            tv_filter_datum.setText(ergebnis);
                        }else {
                            String firstdate = selected.get(0).toString();
                            String firstdate_month = firstdate.substring(4, 7);
                            String firstdate_day = firstdate.substring(8, 10);
                            String firstdate_year = firstdate.substring(30, 34);

                            String firstday = firstdate_day + "/" + firstdate_month + "/" + firstdate_year;

                            String ergebnis = firstday;
                            tv_filter_datum.setText(ergebnis);
                        }
                        gl_show_date.setVisibility(View.GONE);
                        btn_datum_bestätigen.setVisibility(View.GONE);
                        btn_filter_anwenden.setVisibility(View.VISIBLE);
                        btn_filter_abbrechen.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;

                String date = month + "/" + dayOfMonth + "/" + year;
                tv_filter_datum.setText(date);
            }

        };
        tv_filter_kategorien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Filtereinstellungen.this, Kategorien.class);
                startActivity(intent);
            }
        });

        btn_filter_anwenden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("map", 0);
                SharedPreferences.Editor edit = pref.edit();
                int umkreis = sb_entfernung.getProgress();
                int preis = sb_preis.getProgress();
                String date = tv_filter_datum.getText().toString();
                String ort = et_filter_stadt.getText().toString();

                int size = date.length();
                if(size < 27){
                    if(date.equals("Datum auswählen")){
                        edit.putString("datum","no");
                        edit.putString("datum2","no");
                    }else {
                        String date1 = date.substring(0,11);
                        edit.putString("datum", date1);
                        edit.putString("datum2", "no");
                    }
                }else{
                    String date1 = date.substring(0,11);
                    String date2 = date.substring(16,27);

                    edit.putString("datum",date1);
                    edit.putString("datum2",date2);
                }

                edit.putInt("umkreis", umkreis);
                edit.putInt("preis", preis);


                if(ort.isEmpty()){
                    edit.putString("ort", "no");
                }else {
                    edit.putString("ort", ort);
                }
                edit.apply();
                Intent intent = new Intent(Filtereinstellungen.this, Fragment_Activity.class);
                startActivity(intent);
            }
        });
    }
}

