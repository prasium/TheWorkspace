package com.example.android.theworkspace;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ViewHolder extends RecyclerView.ViewHolder
         {
    TextView fileName,uName,category,date;
    Button down,viewbtn;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        fileName=itemView.findViewById(R.id.FileName);
        uName=itemView.findViewById(R.id.Uname);
        category=itemView.findViewById(R.id.Category);
        date=itemView.findViewById(R.id.Date);
        down=itemView.findViewById(R.id.down_btn);
        viewbtn=itemView.findViewById(R.id.view_btn);
    }
             public void setFileName(String string) {
                 fileName.setText(string);
             }


             public void setCategory(String string) {
                 category.setText(string);
             }

             public void setuName(final String string) {
                 DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");
                   ref.child(string).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        uName.setText(dataSnapshot.child("userName").getValue().toString());
                       }
                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
             }

             public void setDate(String string) {
                 SimpleDateFormat sf =new SimpleDateFormat("dd MMMM yyyy");
                 string=sf.format(new Date(Long.parseLong(string)));
                 date.setText(string);
             }




         }
