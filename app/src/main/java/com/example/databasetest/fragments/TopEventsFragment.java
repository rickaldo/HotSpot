package com.example.databasetest.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databasetest.Adapter.MyEventsAdapter;
import com.example.databasetest.R;
import com.example.databasetest.Adapter.TopEventsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TopEventsFragment extends Fragment {

    private RecyclerView recyclerView, gvtopevents;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private static final String TAG = "TOPEVENTS";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.layout_topeventsfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<String> filepath = new ArrayList<>();
        ArrayList<String> evdate =new ArrayList<>();
        ArrayList<String> evadress =new ArrayList<>();
        ArrayList<String> evid =new ArrayList<>();
        ArrayList<Double> lat = new ArrayList<>();
        ArrayList<Double> lng = new ArrayList<>();


        ArrayList<String> topfile = new ArrayList<>();
        ArrayList<String> topdatum = new ArrayList<>();
        ArrayList<String> topname = new ArrayList<>();
        ArrayList<String> toppreis = new ArrayList<>();

        name.clear();
        time.clear();
        filepath.clear();
        evdate.clear();
        evadress.clear();
        evid.clear();
        lat.clear();
        lng.clear();
        topdatum.clear();
        topfile.clear();
        topname.clear();
        toppreis.clear();

        mAuth = FirebaseAuth.getInstance();

        String uid = mAuth.getCurrentUser().getUid();

        this.recyclerView = (RecyclerView)getActivity().findViewById(R.id.rv_meine_events2);
        this.gvtopevents = (RecyclerView) getActivity().findViewById(R.id.rv_topevents2);

        db.collection("events")
                .orderBy("counter", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                topfile.add(document.get("filepath").toString());
                                topdatum.add(document.get("date").toString());
                                topname.add(document.get("name").toString());
                                toppreis.add(document.get("preis").toString());
                            }
                            TopEventsAdapter adapter = new TopEventsAdapter(getContext(),topfile,topname,toppreis,topdatum);
                            gvtopevents.setAdapter(adapter);
                            gvtopevents.setLayoutManager(new GridLayoutManager(getContext(),2));
                        }

                    }
                });

        db.collection("normaluser")
                .document(uid)
                .collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot doc = task.getResult();
                            int size = doc.size();
                            if(size > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(!document.get("name").toString().equals("filler")) {
                                        name.add(document.get("name").toString());
                                        time.add(document.get("time").toString());
                                        filepath.add(document.get("filepath").toString());
                                        evdate.add(document.get("date").toString());
                                        evadress.add(document.get("addresse").toString());
                                        evid.add(document.getId());
                                        lat.add(Double.parseDouble(document.get("lat").toString()));
                                        lng.add(Double.parseDouble(document.get("lng").toString()));

                                        Log.d(TAG, "" + name + " " + time);
                                    }
                                }
                            }else {

                            }
                            if(name.size() > 0) {
                                MyEventsAdapter adapter = new MyEventsAdapter(name, evdate, filepath, name, time, evadress, evid, lat, lng, uid, getContext());
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            }else{
                                name.add("empty");
                                MyEventsAdapter adapter = new MyEventsAdapter(name, evdate, filepath, name, time, evadress, evid, lat, lng, uid, getContext());
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            }

                        }
                    }
                });
    }

}

