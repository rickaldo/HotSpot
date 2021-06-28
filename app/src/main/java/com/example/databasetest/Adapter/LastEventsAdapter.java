package com.example.databasetest.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databasetest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class LastEventsAdapter  extends RecyclerView.Adapter<LastEventsAdapter.ViewHolder>{

    private static final String TAG = "LastEvents";

    private StorageReference ref = FirebaseStorage.getInstance().getReference();


    private Context mContext;

    private ArrayList<String> datum = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> filepath = new ArrayList<>();

    private boolean firstclick = true;

    public LastEventsAdapter(Context context, ArrayList<String> Filepath, ArrayList<String> Name,ArrayList<String> Datum) {
        filepath = Filepath;
        mContext = context;
        datum = Datum;
        name = Name;
    }

    @Override
    public LastEventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pastevents, parent, false);
        LastEventsAdapter.ViewHolder holder = new LastEventsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LastEventsAdapter.ViewHolder holder, final int position) {

        ref.child("Images/" +filepath.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext)
                        .load(uri)
                        .into(holder.iv_leevents);
            }
        });

        holder.iv_leevents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstclick) {
                    holder.iv_leevents.setImageResource(R.drawable.foryoubackground);
                    String evname = "Name: " + name.get(position);
                    String Datum = "Datum: " + datum.get(position);
                    holder.mname.setVisibility(View.VISIBLE);
                    holder.mdatum.setVisibility(View.VISIBLE);
                    holder.mname.setText(evname);
                    holder.mdatum.setText(Datum);
                    firstclick = false;
                }else{
                    holder.mname.setVisibility(View.GONE);
                    holder.mdatum.setVisibility(View.GONE);
                    ref.child("Images/" +filepath.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(mContext)
                                    .load(uri)
                                    .into(holder.iv_leevents);
                        }
                    });
                    firstclick = true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return filepath.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_leevents;
        TextView mname,mdatum;
        RelativeLayout rl_pastevents;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_leevents = itemView.findViewById(R.id.iv_leevents);

            rl_pastevents = itemView.findViewById(R.id.rl_pastevents);
            mname = itemView.findViewById(R.id.tv_lename);
            mdatum = itemView.findViewById(R.id.tv_ledate);
        }
    }
}
