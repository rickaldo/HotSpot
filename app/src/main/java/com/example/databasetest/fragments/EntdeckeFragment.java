package com.example.databasetest.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databasetest.Adapter.EventsForYouAdapter;
import com.example.databasetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EntdeckeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> test = new ArrayList<>();

    private static final String TAG = "EVENTSFORYOU";

    private TextView tveventsforyou;
    private RecyclerView rv_eventsforyou;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.layout_entdeckefragment, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        this.tveventsforyou = (TextView)getActivity().findViewById(R.id.tveventsforyou2);
        this.rv_eventsforyou = (RecyclerView)getActivity().findViewById(R.id.rv_eventsforyou2);


        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> datum = new ArrayList<>();
        ArrayList<String> preis = new ArrayList<>();

        db.collection("events")
                .orderBy("counter", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                test.add(document.get("filepath").toString());
                                name.add(document.get("name").toString());
                                datum.add(document.get("date").toString());
                                preis.add(document.get("preis").toString());
                            }
                            EventsForYouAdapter adapter = new EventsForYouAdapter(getContext(),test,name,preis,datum);
                            rv_eventsforyou.setAdapter(adapter);
                            rv_eventsforyou.setLayoutManager(new GridLayoutManager(getContext(),2));
                            rv_eventsforyou.setNestedScrollingEnabled(false);
                        }

                    }
                });
    }
}
