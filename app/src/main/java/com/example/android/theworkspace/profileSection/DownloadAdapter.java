package com.example.android.theworkspace.profileSection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.theworkspace.BuildConfig;
import com.example.android.theworkspace.R;

import java.io.File;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.mViewHolder>{

    Context context;
    List<String> list;
    List<File> file;
    public DownloadAdapter(Context context, List<String> list,List<File> file){
        this.context=context;
        this.list=list;
        this.file=file;
    }
    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_item,parent,false);
        return new mViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, final int position) {
        holder.dTitle.setText(list.get(position));

        holder.dViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider",file.get(position));
                String mime = get_mime_type(uri.toString());
                Log.d("type","this is "+mime);
                // Open file with user selected app
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, mime);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        });
    }

    public String get_mime_type(String url) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(url);
        String mime = null;
        if (ext != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        return mime;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{
        TextView dTitle;
        Button dViewbtn;
        public  mViewHolder(View itemView){
                super(itemView);
              dTitle = itemView.findViewById(R.id.dFilename);
              dViewbtn= itemView.findViewById(R.id.dviewbtn);
        }
        }
}
