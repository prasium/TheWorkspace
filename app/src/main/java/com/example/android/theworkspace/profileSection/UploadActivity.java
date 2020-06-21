package com.example.android.theworkspace.profileSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.theworkspace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    UploadAdapter adapter;
    TextView emptyUploadLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        recyclerView = (RecyclerView) findViewById(R.id.rvUp);
        emptyUploadLabel=findViewById(R.id.empty_upload);
        emptyUploadLabel.setVisibility(View.GONE);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        final String uid=user.getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Uploads");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> list = new ArrayList<>() ;
                ArrayList<String> cat = new ArrayList<>();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    if(ds.child("uid").getValue().toString().equals(uid)){
                       String x=ds.child("filename").getValue().toString();
                        list.add(x);
                        cat.add(ds.child("category").getValue().toString());
                        Log.d("Tiles", "TileName:" +list);
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(UploadActivity.this));
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                adapter=new UploadAdapter(UploadActivity.this, list,cat,uid);
                if(list.size()==0){
                    emptyUploadLabel.setVisibility(View.VISIBLE);
                }
                else{
                    emptyUploadLabel.setVisibility(View.GONE);
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        }
}
