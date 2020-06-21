package com.example.android.theworkspace;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.theworkspace.profileSection.DownloadActivity;
import com.example.android.theworkspace.profileSection.UploadActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {
    TextView fullNameLabel,userNameLabel,fullName,userName,userEmail,userGender;
    Button logout,userUp,userDown,delacc;
    String reauthpass="";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference ref;
    public Profile() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

            //Hooks
            fullNameLabel = view.findViewById(R.id.full_name);
            userNameLabel = view.findViewById(R.id.user_name);
            fullName = view.findViewById(R.id.fullname);
            userName = view.findViewById(R.id.username);
            userEmail = view.findViewById(R.id.email);
            userGender=view.findViewById(R.id.gender);
             userUp=view.findViewById(R.id.userUp);
            userDown=view.findViewById(R.id.userDown);
            logout=view.findViewById(R.id.logout);
            delacc=view.findViewById(R.id.delacc);
            //show UserData
            setUser();

            firebaseAuth=FirebaseAuth.getInstance();
            firebaseUser=firebaseAuth.getCurrentUser();
            ref=FirebaseDatabase.getInstance().getReference("Users");

            userUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent upIntent = new Intent(getContext(), UploadActivity.class);
                    startActivity(upIntent);
                                    }
            });
            userDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent downIntent = new Intent(getContext(), DownloadActivity.class);
                    startActivity(downIntent);
                                    }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    Intent loginIntent = new Intent(getContext(),LoginActivity.class);
                    startActivity(loginIntent);
                    getActivity().finish();
                }
            });
            delacc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.authpass, (ViewGroup) getView(), false);
                    final EditText input = (EditText) viewInflated.findViewById(R.id.userPass);
                    DialogInterface.OnClickListener dialogClickListener=new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Delete button clicked
                                    //Would like the second Alert Dialog to Display Now
                                    DialogInterface.OnClickListener dialogClickListener1 = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int select) {
                                            switch (select) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    reauthpass = input.getText().toString();
                                                    AuthCredential credential = EmailAuthProvider
                                                            .getCredential(firebaseUser.getEmail(),reauthpass );
// Prompt the user to re-provide their sign-in credentials
                                                    firebaseUser.reauthenticate(credential)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Log.d("tag", "User re-authenticated.");
                                                                    if(task.isSuccessful()) {
                                                                        Log.d("tag", "Usuccess.");
                                                                        final String uid = firebaseUser.getUid();
                                                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                for(DataSnapshot d : dataSnapshot.getChildren()){
                                                                                    if(d.getKey().toString().equals(uid)){
                                                                                            d.getRef().removeValue();
                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                        ref=FirebaseDatabase.getInstance().getReference("Uploads");
                                                                        ref.addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                Log.d("wor","working?");
                                                                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                                                                    if(ds.child("uid").getValue().toString().equals(uid)){
                                                                                        Log.d("wor","working?"+ds.getRef());
                                                                                        ds.getRef().removeValue();
                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(getActivity(), "Account Deleted", Toast.LENGTH_LONG).show();
                                                                                    Intent delIntent = new Intent(getContext(), LoginActivity.class);
                                                                                    startActivity(delIntent);
                                                                                    getActivity().finish();

                                                                                } else {
                                                                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }else{
                                                                        Toast.makeText(getActivity(),"Wrong Credentials!",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    dialog.cancel();
                                            }
                                        }
                                    };
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                                   builder1.setTitle("Enter Your Password")
                                            .setView(viewInflated)
                                            .setPositiveButton("OK", dialogClickListener1)
                                            .setNegativeButton("CANCEL", dialogClickListener1).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Are you sure?");
                    dialog.setMessage("Deleting account will result in completely removing your "+
                            "account and files associated with it,you won't be able to access this app.");
                  Log.d("abc","I W K");
                    dialog.setPositiveButton("Delete",dialogClickListener).setNegativeButton("Dismiss",dialogClickListener).show();
                }
           });
            return view;
        }



    private void setUser() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullname = dataSnapshot.child("name").getValue(String.class);
                String username = dataSnapshot.child("userName").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String gender = dataSnapshot.child("gender").getValue(String.class);
                fullNameLabel.setText(fullname);
                userNameLabel.setText(username);
                fullName.setText(fullname);
                userName.setText(username);
                userEmail.setText(email);
                userGender.setText(gender);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
