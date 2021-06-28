package com.example.databasetest.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databasetest.R;
import com.example.databasetest.fragments.MapboxFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.ViewHolder> {
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> filepath = new ArrayList<>();

    ArrayList<String> evname =new ArrayList<>();
    ArrayList<String> evtime =new ArrayList<>();
    ArrayList<String> evadress =new ArrayList<>();
    ArrayList<String> evid =new ArrayList<>();
    ArrayList<Double> lat = new ArrayList<>();
    ArrayList<Double> lng = new ArrayList<>();
    String Uid;
    private Context context;
    private static final String TAG = "EVETNSADAPTER";

    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    public MyEventsAdapter(ArrayList Title, ArrayList Date,ArrayList File,ArrayList name, ArrayList time, ArrayList addres, ArrayList id, ArrayList Lat, ArrayList Lng,String uid, Context ctx) {
        title = Title;
        date = Date;
        filepath = File;
        evname = name;
        evtime = time;
        evadress = addres;
        evid = id;
        lat = Lat;
        lng = Lng;
        Uid = uid;
        context = ctx;
    }



    @NonNull
    @Override
    public MyEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mytopevents,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(title.contains("empty")) {
            holder.iv_poster.setVisibility(View.GONE);
            holder.titel.setVisibility(View.GONE);
            holder.datum.setText(R.string.MyEvents_NoEventsAddedYet);

        }else{
                ref.child("Images/" +filepath.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context)
                                .load(uri)
                                .into(holder.iv_poster);
                    }
                });
                holder.titel.setText(title.get(position));
                holder.datum.setText(date.get(position));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatActivity activity = (AppCompatActivity) context;
                        MapboxFragment mp = new MapboxFragment();

                        SharedPreferences pref = context.getSharedPreferences("map", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("topeventsonclick", true);
                        editor.putString("tplat",lat.get(position).toString());
                        editor.putString("tplng",lng.get(position).toString());
                        editor.putString("tpname",evname.get(position));
                        editor.putString("tpdate",date.get(position));
                        editor.putString("tptime",evtime.get(position));
                        editor.putString("tpadress",evadress.get(position));
                        editor.putString("tpevid",evid.get(position));
                        editor.putString("tpuid",Uid);
                        editor.putString("tpfilepath",filepath.get(position));

                        editor.apply();

                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,mp).addToBackStack(null).commit();
                    }
                });
        }
    }


    @Override
    public int getItemCount() {
        return title.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titel, datum;
        ImageView iv_poster;
        LinearLayout topevents_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titel = itemView.findViewById(R.id.tv_myevents_title);
            datum = itemView.findViewById(R.id.tv_myevents_date);
            topevents_layout = itemView.findViewById(R.id.topevents_layout);
            iv_poster = itemView.findViewById(R.id.iv_myeventsposter);
        }
    }
}
