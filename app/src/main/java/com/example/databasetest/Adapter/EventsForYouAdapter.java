package com.example.databasetest.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.BitmapThumbnailImageViewTarget;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.databasetest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;

public class EventsForYouAdapter extends RecyclerView.Adapter<EventsForYouAdapter.ViewHolder>{

private static final String TAG = "EventsForYou";

private StorageReference ref = FirebaseStorage.getInstance().getReference();

private ArrayList<String> filepath = new ArrayList<>();
private ArrayList<String> datum = new ArrayList<>();
private ArrayList<String> name = new ArrayList<>();
private ArrayList<String> preis = new ArrayList<>();

private Context mContext;

private boolean firstclick = true;

public EventsForYouAdapter(Context context, ArrayList<String> Filepath,ArrayList<String> Name,ArrayList<String> Preis,ArrayList<String> Datum) {
        filepath = Filepath;
        datum = Datum;
        name = Name;
        preis = Preis;
        mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_eventsforyou, parent, false);
                ViewHolder holder = new ViewHolder(view);
                return holder;
                }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            ref.child("Images/" + filepath.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(mContext)
                            .load(uri)
                            .into(holder.iv_topevents_image);
                }
            });

            String fname = mContext.getResources().getString(R.string.Create_EventName) +": " +"\n" +name.get(position);
            String fpreis = mContext.getResources().getString(R.string.Filter_Price) +": " +preis.get(position) +"€";
            String fdatum = mContext.getResources().getString(R.string.Filter_Date) +": " +datum.get(position);

            holder.mname.setText(fname);
            holder.mpreis.setText(fpreis);
            holder.mdatum.setText(fdatum);

            holder.cv_imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.flipView2.flipTheView(true);
                }
            });

            holder.cv_imageview2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.flipView2.flipTheView(true);
                }
            });
        }

        @Override
        public int getItemCount() {
                return filepath.size();
                }

        public class ViewHolder extends RecyclerView.ViewHolder{
            EasyFlipView flipView2;
            CustomImageView iv_topevents_image;
            TextView mname, mdatum, mpreis;
            CardView cv_imageview,cv_imageview2;

            public ViewHolder(View itemView) {
                super(itemView);
                flipView2 = itemView.findViewById(R.id.flipview2);
                mname = itemView.findViewById(R.id.tv_eventsforyou_name);
                mdatum = itemView.findViewById(R.id.tv_eventsforyou_date);
                mpreis = itemView.findViewById(R.id.tv_eventsforyou_price);
                iv_topevents_image = itemView.findViewById(R.id.iv_eventsforyou_image);
                cv_imageview = itemView.findViewById(R.id.cv_imageview);
                cv_imageview2 = itemView.findViewById(R.id.cv_imageview2);
            }
        }
}
