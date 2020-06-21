package com.example.android.theworkspace;


import android.Manifest;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Add extends Fragment {

    FirebaseStorage storage; // used for uploading files
    FirebaseDatabase database;//to store URL of uploaded files
    Button upload, select;
    Uri fileUri; //local storage path
    ProgressBar progressBar;
    TextInputLayout setFilename,setCategoryname;
    TextView uploadLabel,uploadStat,noteLabel;
    Spinner spinner;

    String category,type;
    public Add() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        progressBar = view.findViewById(R.id.progressBar);
        select = view.findViewById(R.id.select_file);
        upload = view.findViewById(R.id.upload_file);
        setFilename=view.findViewById(R.id.setFilename);
        setCategoryname=view.findViewById(R.id.setCategory);
        spinner=view.findViewById(R.id.category);
        uploadLabel=view.findViewById(R.id.upload_label);
        uploadStat=view.findViewById(R.id.upload_stat);
        setCategoryname.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        uploadLabel.setVisibility(View.GONE);
        uploadStat.setVisibility(View.GONE);
        noteLabel=view.findViewById(R.id.note_label);
        noteLabel.setVisibility(View.GONE);
        //for drop down menu
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               category= spinner.getSelectedItem().toString();
                Log.d("test","abc"+category);
               if(category.equals("Other"))
               {
                   Log.d("test","def");
                   setCategoryname.setVisibility(View.VISIBLE);
                   noteLabel.setVisibility(View.VISIBLE);
                }
               else
               {
                   noteLabel.setVisibility(View.GONE);
                   setCategoryname.setVisibility(View.GONE);
                   Log.d("woa","sada"+category);
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectFile();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fileUri != null) {
                    uploadFile();

                } else {
                    Toast.makeText(getActivity(), "Select a File to Upload", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    private Boolean validateTitle(String s)
    {
      if(s.isEmpty()) {
          setFilename.setError("File Title cannot be blank");
          return false;
      }     else
        {
            setFilename.setError(null);
            setFilename.setErrorEnabled(false);
            return true;
        }
    }


    //for file selection
    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (reqCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFile();
        } else {
            Toast.makeText(getActivity(), "Permission Required to Upload file", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFile() {
        // Defining Implicit Intent
        Intent select = new Intent();
        select.setType("*/*");
        select.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(select, "Choose File to Upload"), 86);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //checks whether user has selected a file or not
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();// return the url of file selected
            ContentResolver cR = getContext().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getExtensionFromMimeType(cR.getType(fileUri)); // gives file extension
            //givese file name with extension
            String result = null;
            if (fileUri.getScheme().equals("content")) {
                Cursor cursor = getContext().getContentResolver().query(fileUri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            }
            if (result == null) {
                result = fileUri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            if (result.lastIndexOf(".") > 0)
                result = result.substring(0, result.lastIndexOf("."));
            Toast.makeText(getActivity(), "File Selected!", Toast.LENGTH_SHORT).show();
            setFilename.getEditText().setText(result);
        } else {
            Toast.makeText(getActivity(), "Select a file", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile() {


       final String checkTitle= setFilename.getEditText().getText().toString();
        if (!validateTitle(checkTitle)) // ensures no empty file name
            return;
        progressBar.setVisibility(View.VISIBLE);
        uploadLabel.setVisibility(View.VISIBLE);
        uploadStat.setVisibility(View.VISIBLE);
        final String timeStamp = System.currentTimeMillis() + "";

         StorageReference storageReference = storage.getReference(); // returns root path
        storageReference.child("Uploads").child(timeStamp).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String otherCat = setCategoryname.getEditText().getText().toString();
                        Log.d("wpja","sada"+otherCat);
                        if(!otherCat.isEmpty()) {
                            category = otherCat;
                        }
                        Uri url = uri;//return url of your uploaded file
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String setName=checkTitle;
                        String setType=type;
                        String uid = user.getUid();
                        FileHelperClass upfile = new FileHelperClass(setName,setType,uid,url.toString(),category);
                        DatabaseReference reference = database.getInstance().getReference("Uploads").child(timeStamp); // return the path to root
                        reference.setValue(upfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "File Successfully uploaded", Toast.LENGTH_SHORT).show();
                                    setFilename.getEditText().setText("");
                                } else {
                                    Toast.makeText(getActivity(), "File not successfully uploaded", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                                uploadLabel.setVisibility(View.GONE);
                                noteLabel.setVisibility(View.GONE);
                                uploadStat.setVisibility(View.GONE);
                            }
                        });

                    }

                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Upload Failed Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //track progress of the upload
                int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressBar.setProgress(currentProgress);
                uploadStat.setText(currentProgress+"/100");
            }
        });

    }

}
