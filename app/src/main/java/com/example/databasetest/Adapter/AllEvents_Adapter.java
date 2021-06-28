package com.example.databasetest.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databasetest.R;
import com.example.databasetest.fragments.MapboxFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AllEvents_Adapter extends RecyclerView.Adapter<AllEvents_Adapter.ViewHolder>{

    private static final String TAG = "AllEventsAdapter";

    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    private ArrayList<String> filepath = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> datum = new ArrayList<>();
    private ArrayList<String> zeit = new ArrayList<>();
    private ArrayList<String> addresse = new ArrayList<>();
    ArrayList<String> evid =new ArrayList<>();
    ArrayList<String> lat = new ArrayList<>();
    ArrayList<String> lng = new ArrayList<>();
    String Uid;
    private Context mContext;

    public AllEvents_Adapter(Context context, ArrayList<String> Filepath,ArrayList Name, ArrayList Datum, ArrayList Zeit, ArrayList Addresse, ArrayList id, ArrayList Lat, ArrayList Lng,String uid) {
        name = Name;
        datum = Datum;
        zeit = Zeit;
        addresse = Addresse;
        filepath = Filepath;
        evid = id;
        lat = Lat;
        lng = Lng;
        Uid = uid;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_allevents, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ref.child("Images/" +filepath.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext)
                        .load(uri)
                        .into(holder.iv_allevents_poster);
            }
        });

        holder.tv_allevents_datum.setText(datum.get(position));
        holder.tv_allevents_adresse.setText(addresse.get(position));
        holder.tv_allevents_name.setText(name.get(position));
        holder.tv_allevents_zeit.setText(zeit.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) mContext;
                SharedPreferences pref = mContext.getSharedPreferences("map", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("topeventsonclick", true);
                editor.putString("tplat",String.valueOf(lat.get(position)));
                editor.putString("tplng",String.valueOf(lng.get(position)));
                editor.putString("tpname",name.get(position));
                editor.putString("tpdate",datum.get(position));
                editor.putString("tptime",zeit.get(position));
                editor.putString("tpadress",addresse.get(position));
                editor.putString("tpevid",evid.get(position));
                editor.putString("tpuid",Uid);
                editor.putString("tpfilepath",filepath.get(position));

                editor.apply();

                MapboxFragment mp = new MapboxFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,mp).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filepath.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_allevents_name, tv_allevents_datum,tv_allevents_zeit,tv_allevents_adresse;
        ImageView iv_allevents_poster;
        LinearLayout ll_allevents;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_allevents_adresse = itemView.findViewById(R.id.tv_allevents_adresse);
            tv_allevents_zeit = itemView.findViewById(R.id.tv_allevents_zeit);
            tv_allevents_datum = itemView.findViewById(R.id.tv_allevents_datum);
            tv_allevents_name = itemView.findViewById(R.id.tv_allevents_name);
            iv_allevents_poster = itemView.findViewById(R.id.iv_allevents_poster);
            ll_allevents = itemView.findViewById(R.id.ll_allevents);

        }
    }
}
