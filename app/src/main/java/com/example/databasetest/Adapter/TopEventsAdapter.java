package com.example.databasetest.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databasetest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;

public class TopEventsAdapter extends RecyclerView.Adapter<TopEventsAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    private ArrayList<String> filepath = new ArrayList<>();
    private Context mContext;

    private ArrayList<String> datum = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> preis = new ArrayList<>();

    public TopEventsAdapter(Context context, ArrayList<String> Filepath, ArrayList<String> Name,ArrayList<String> Preis,ArrayList<String> Datum) {
        filepath = Filepath;
        mContext = context;
        datum = Datum;
        name = Name;
        preis = Preis;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_events, parent, false);
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
                        .into(holder.iv_topevents_image);
            }
        });
        String numb = String.valueOf(position+1);
        holder.tv_number.setText(numb);

        String fname = mContext.getResources().getString(R.string.Create_EventName) +": " +"\n" +name.get(position);
        String fpreis = mContext.getResources().getString(R.string.Filter_Price) +": " +preis.get(position) +"€";
        String fdatum = mContext.getResources().getString(R.string.Filter_Date) +": " +datum.get(position);

        holder.mname.setText(fname);
        holder.mpreis.setText(fpreis);
        holder.mdatum.setText(fdatum);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.flipView.flipTheView(true);
            }
        });

        holder.cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.flipView.flipTheView(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filepath.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_number;
        CustomImageView iv_topevents_image;
        TextView mname, mdatum, mpreis;
        EasyFlipView flipView;
        CardView cardView,cardView2;

        public ViewHolder(View itemView) {
            super(itemView);
            flipView = itemView.findViewById(R.id.flipview);
            iv_topevents_image = itemView.findViewById(R.id.iv_topevents_image);
            mname = itemView.findViewById(R.id.tv_topevents_name);
            mdatum = itemView.findViewById(R.id.tv_topevents_date);
            mpreis = itemView.findViewById(R.id.tv_topevents_price);
            tv_number = itemView.findViewById(R.id.tv_number);
            cardView = itemView.findViewById(R.id.cv_tp_imageview);
            cardView2 = itemView.findViewById(R.id.cv_tp_imageview2);
        }
    }

}