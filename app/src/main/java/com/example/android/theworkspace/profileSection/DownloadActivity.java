package com.example.android.theworkspace.profileSection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.example.android.theworkspace.R;

import java.io.File;
import java.util.Arrays;

public class DownloadActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DownloadAdapter adapter;
    TextView emptyDownloadLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        emptyDownloadLabel=findViewById(R.id.empty_download);
        recyclerView = (RecyclerView) findViewById(R.id.rvDown);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        File storagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator + "WorkSpace");
        //Create DownloadFolder directory for the first time app opens
// Create direcorty if not exists
        if (!storagePath.exists()) {
            storagePath.mkdirs();
            Log.v("val", "b");
        }

        File[] filepath = storagePath.listFiles();//file path list
        String[] files = storagePath.list();
    //    Log.d("filepath",filepath[0].toString());
        if(files.length!=0) {
            emptyDownloadLabel.setVisibility(View.GONE);
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                files[i] = files[i].substring(0, files[i].lastIndexOf("."));
                Log.d("Files", "FileName:" + files[i]);
            }
            adapter = new DownloadAdapter(this, Arrays.asList(files), Arrays.asList(filepath));
            recyclerView.setAdapter(adapter);
        }
        else{
            emptyDownloadLabel.setVisibility(View.VISIBLE);
        }
    }
}
