package com.example.android.theworkspace.profileSection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.theworkspace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.mViewHolder>{

    Context context;
    ArrayList<String> list;
    ArrayList<String> cat;
    String uid;
    public UploadAdapter(Context context, ArrayList<String> list,ArrayList<String> cat,String uid){
        this.context=context;
        this.list=list;
        this.cat=cat;
        this.uid=uid;
    }
    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_item_second,parent,false);
        return new mViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewHolder holder, int position) {
        final String temp = list.get(position);
        final int pos=position;// CHECK HERE FOR ANOMALIES if in app
        holder.uTitle.setText(list.get(position));
        holder.usubTitle.setText(cat.get(position));
        holder.uDelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete File")
                        .setMessage("Are you sure you want to delete the uploaded file?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Uploads");
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds: dataSnapshot.getChildren())
                                        {
                                            if(ds.child("filename").getValue().toString().equals(temp)){
                                                Log.d("kya","kuch bhi");
                                                RemoveFromStorage(ds.getKey().toString());//for removal from firebase storage
                                                 ds.getRef().removeValue();
                                                Toast.makeText(context,"File Deleted",Toast.LENGTH_SHORT).show();
                                                list.remove(pos);
                                                cat.remove(pos);
                                                notifyItemRemoved(pos);
                                                notifyItemRangeChanged(pos, list.size());

                                            }
                                        }
                                     }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{
        TextView uTitle,usubTitle;
        Button uDelbtn;
        public  mViewHolder(View itemView){
            super(itemView);
            uTitle = itemView.findViewById(R.id.uFilename);
            usubTitle = itemView.findViewById(R.id.uCat);
            uDelbtn=itemView.findViewById(R.id.udelbtn);
        }
    }



    //For storage removal
    private void RemoveFromStorage(String ts) {
        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference fileRef = mFirebaseStorage.getReference("Uploads").child(ts);
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

}


