package com.example.android.theworkspace;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedView extends Fragment {

    private final String CHANNEL_ID = "download_notifications";
    private final int NOTIFICATION_ID=001;
    private ProgressDialog progress;
    private View view;
    RecyclerView reView;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    FirebaseRecyclerAdapter adapter;
    FirebaseStorage storage,refstore;
    String userID, vChoice;
    public FeedView() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_feed_view, container, false);
        progress = new ProgressDialog(getActivity());
        reView = (RecyclerView) view.findViewById(R.id.list_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        reView.setLayoutManager(linearLayoutManager);
        // reView.setHasFixedSize(true);

        //viewSet=view.findViewById(R.id.setView);
        //for drop down menu
       Bundle bundle= this.getArguments();
        vChoice=bundle.getString("vie");
        Log.d("SEE","check"+vChoice);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Uploads");
        storage = FirebaseStorage.getInstance();
        refstore = FirebaseStorage.getInstance();


        fetch();

        return view;
    }

    private void fetch() {
        Query query;
        if(vChoice.equals("All")){
            query=mDatabase;
        }
        else{
            query=mDatabase.orderByChild("category").equalTo(vChoice);
        }
        Log.d("idk","wha");
        FirebaseRecyclerOptions<RowItem> options  =new FirebaseRecyclerOptions.Builder<RowItem>()
                .setQuery(query, new SnapshotParser<RowItem>() {
                    @NonNull
                    @Override
                    public RowItem parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new RowItem(snapshot.child("filename").getValue().toString(),
                                snapshot.child("filetype").getValue().toString(),
                                snapshot.child("category").getValue().toString(),
                                snapshot.child("uid").getValue().toString(),
                                snapshot.getKey().toString(),
                                snapshot.child("url").getValue().toString()
                        );
                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<RowItem, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View mview = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fileitem_list, parent, false);
                return new ViewHolder(mview);
            }

            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, final RowItem rowItem) {
                holder.setFileName(rowItem.getFilename());
                holder.setCategory(rowItem.getCategory());
                holder.setuName(rowItem.getUid());
                holder.setDate(rowItem.getTimestamp());
                holder.viewbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progress.setMessage("Loading");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(true);
                        progress.show();

                        StorageReference storeRef = refstore.getReferenceFromUrl(rowItem.getUrl());
                        if(isOnline()) {
                            try {
                                final File localFile = File.createTempFile(rowItem.getFilename(), "." + rowItem.getType());
                                Log.d("rear", "z" + localFile);

                                storeRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", localFile);
                                        ContentResolver cr = getActivity().getContentResolver();
                                        String mime = cr.getType(uri);
                                        //   Log.d("rear","z"+uri);
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, mime);
                                        //    Log.d("rear","z"+mime);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        startActivity(intent);
                                        progress.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        Toast.makeText(getActivity(), "Error Occured ", Toast.LENGTH_SHORT).show();
                                        progress.dismiss();
                                    }
                                });
                            //To delete cache as it might eat storage
                               if(localFile.exists()) {
                                       File[] directory = getActivity().getCacheDir().listFiles();
                                       if(directory != null){
                                           for (File file : directory ){
                                               file.delete();
                                           }
                                       }
                               }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                        Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                        }
//This method was to load content directly into the memory, the above one is for create a file in cache which will be temporary
                        //                        Log.d("abcd",storeRef.toString());
//                        final long FIFTY_MEGABYTE = 1024 * 1024 * 50; //30MB Max
//                        storeRef.getBytes(FIFTY_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                            @Override
//                            public void onSuccess(byte[] bytes) {
//                                    Intent seeIntent = new Intent(getContext(), ViewActivity.class);
//                                    seeIntent.putExtra("info",bytes);
//                                seeIntent.putExtra("type",rowItem.getType());
//                                    startActivity(seeIntent);
//                                    progress.dismiss();
//                                     }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
//                                Toast.makeText(getActivity(),"Files of size greater than 30MB can be viewed after downloading them\nOnly Image Files can be viewed currently",Toast.LENGTH_LONG);
//                                // Handle any errors
//                            }
//                        });

                    }
                });


                holder.down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if(isOnline()){
                            final int progressMax = 100;
                            createNotificationChannel();
                            final NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                                    .setContentTitle("Download")
                                    .setContentText("Download In Progress")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setOnlyAlertOnce(true);

                            final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());
                            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
                            StorageReference httpsReference = storage.getReferenceFromUrl(rowItem.getUrl());
                            File storagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "WorkSpace");
                            Log.v("val", "c");
// Create directory if not exists
                            if (!storagePath.exists()) {
                                storagePath.mkdirs();
                                Log.v("val", "b");
                            }
                            final File myFile = new File(storagePath, rowItem.getFilename() + "." + rowItem.getType());
                            Log.v("val", "d");
                            httpsReference.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Log.v("val", "e");
                                    builder.setContentText("Download Complete");
                                    notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
                                    Toast.makeText(getActivity(), "File Downloaded into /Downloads/Workspace", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    builder.setContentText("Download Failed");
                                    notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
                                    Toast.makeText(getActivity(), "Download Failed, Check your Internet Connection ", Toast.LENGTH_SHORT).show();
                                }

                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    builder.setProgress(progressMax, currentProgress, false);
                                }
                            });

                        }
                            else{
                                Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            ActivityCompat.requestPermissions(getActivity(), new String[]
                                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 9);
                        }
                    }

                });
            }

        };
        reView.setAdapter(adapter);

    }

    // if ANDROID IS ABOVE 8.0 we create a channel for notification
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String name ="Download Notifications";
            String descsription ="App Downloads";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel= new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(descsription);

            NotificationManager notificationManager =(NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (reqCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Permission Granted!", Toast.LENGTH_SHORT).show();
            fetch();
        } else {
            Toast.makeText(getActivity(), "Permission Required to Download", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
