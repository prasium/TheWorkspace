package com.example.android.theworkspace;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

public class RowItem {

    String filename;
    String category;
    String uid;
    String timestamp;
    String url;
    String type;
    public RowItem()
    {

    }

    public RowItem(String filename, String type,String category,String uid,String timestamp,String url)
    {
        this.filename=filename;
       this.category=category;
       this.timestamp=timestamp;
       this.uid=uid;
       this.url=url;
       this.type=type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String fTitle) {
        this.filename = fTitle;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
