package com.example.databasetest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databasetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.core.content.FileProvider.getUriForFile;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private String UID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference ref = FirebaseStorage.getInstance().getReference();

    private ArrayList<String> evname =new ArrayList<>();
    private ArrayList<String> evdate =new ArrayList<>();
    private ArrayList<String> evtime =new ArrayList<>();
    private ArrayList<String> evadress =new ArrayList<>();
    private ArrayList<String> evid =new ArrayList<>();
    private ArrayList<Double> lat = new ArrayList<>();
    private ArrayList<Double> lng = new ArrayList<>();
    private ArrayList<String> filepath = new ArrayList<>();

    private File localfile;

    private Context context;

    private final String TAG = "RecyclerViewAdapter";

    public RecyclerViewAdapter(ArrayList eventname, ArrayList eventdate, ArrayList uhrzeit, ArrayList adresse, String uid, ArrayList eid,ArrayList Lat,
                               ArrayList Lng,ArrayList file,Context context) {
        evname = eventname;
        evdate = eventdate;
        evtime = uhrzeit;
        evadress = adresse;
        UID = uid;
        evid = eid;
        lat = Lat;
        lng = Lng;
        filepath = file;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_listitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        SharedPreferences pref = context.getSharedPreferences("map", 0);
        String layout = pref.getString("layout", "mapbox://styles/rickaldo/ck9fzm0451s231iqpio1e96ts");
        if(layout.equals("mapbox://styles/rickaldo/ck9n667n92gwb1")) {
            view.setForceDarkAllowed(true);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ref.child("Images/" +filepath.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(holder.ivposter);
            }
        });

        holder.tvshowevname.setText(evname.get(position));
        holder.tvshowevdate.setText(evdate.get(position));
        holder.tvshowevtime.setText(evtime.get(position));
        holder.tvshowevadresse.setText(evadress.get(position));
        holder.ivbell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EVENTID = evid.get(position);

                ArrayList<String> eventid = new ArrayList<>();
                Map<String, Object> test = new HashMap<>();
                test.put("Eventid: ", EVENTID);
                test.put("name", evname.get(position));
                test.put("time", evtime.get(position));
                test.put("filepath", filepath.get(position));
                test.put("date", evdate.get(position));
                test.put("lat", lat.get(position));
                test.put("lng", lng.get(position));
                test.put("addresse", evadress.get(position));

                db.collection("normaluser")
                        .document(UID)
                        .collection("events")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document: task.getResult()) {
                                        eventid.add(document.getId());
                                    }
                                    if(eventid.contains(EVENTID)){
                                        db.collection("normaluser")
                                                .document(UID)
                                                .collection("events")
                                                .document(EVENTID)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(context, R.string.Toast_EventRemovedFromMyEvents, Toast.LENGTH_SHORT).show();
                                                        db.collection("events")
                                                                .document(EVENTID)
                                                                .update("counter", FieldValue.increment(-1));
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context,R.string.Toast_ErrorOccured, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else{
                                        db.collection("normaluser")
                                                .document(UID)
                                                .collection("events")
                                                .document(EVENTID)
                                                .set(test);

                                        db.collection("events")
                                                .document(EVENTID)
                                                .update("counter", FieldValue.increment(1));
                                        Toast.makeText(context, R.string.Toast_AddedToMyEvents, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
        holder.ivmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+lat.get(position)+","+lng.get(position)));
                context.startActivity(intent);
            }
        });

        holder.ivshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputDir = context.getCacheDir();
                try {
                    localfile = File.createTempFile("images",".jpg",outputDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ref.child("Images/" +filepath.get(position)).getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Uri contentUri = getUriForFile(context, "com.example.databasetest.fileprovider", localfile);
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);

                        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                                .setLink(Uri.parse("https://www.example.com/?id=" +evid.get(position)))
                                .setDomainUriPrefix("https://databasetest.page.link")
                                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("Sie dir dieses Event an!")
                                .setImageUrl(contentUri)
                                .build())
                                .buildDynamicLink();
                        Uri uri = dynamicLink.getUri();

                        FirebaseDynamicLinks.getInstance().createDynamicLink()
                                .setLongLink(uri)
                                .buildShortDynamicLink()
                                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                                    @Override
                                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                        Uri link = task.getResult().getShortLink();
                                        if(link != null){
                                            sendIntent.putExtra(Intent.EXTRA_TEXT,"Folgendes Event: " +link +"\n" +"\n" +"\n"
                                                    +"Eventname: " +evname.get(position));
                                            sendIntent.setType("text/plain");
                                            sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            context.startActivity(Intent.createChooser(sendIntent,null));
                                        }
                                    }
                                });
                    }
                });

                    }
                });
    }

    @Override
    public int getItemCount() {
        return evname.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvshowevname, tvshowevdate, tvshowevtime, tvshowevadresse;
        ImageView ivbell, ivmaps, ivposter,ivshare;
        LinearLayout tmp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tmp = itemView.findViewById(R.id.parent_layout);
            tvshowevname = itemView.findViewById(R.id.tvshowevname);
            tvshowevdate = itemView.findViewById(R.id.tvshowevdate);
            tvshowevtime = itemView.findViewById(R.id.tvshowevtime);
            tvshowevadresse = itemView.findViewById(R.id.tvshowevadresse);
            ivbell = itemView.findViewById(R.id.ivbell);
            ivmaps = itemView.findViewById(R.id.ivmaps);
            ivposter = itemView.findViewById(R.id.ivposter);
            ivshare = itemView.findViewById(R.id.iv_share);
        }
    }
}
