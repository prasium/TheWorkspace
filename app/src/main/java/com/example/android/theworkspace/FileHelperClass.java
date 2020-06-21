package com.example.android.theworkspace;

public class FileHelperClass {
    String filename;
    String uid;
    String url;
    String category;
    String filetype;

    public FileHelperClass()
    {

    }
    public FileHelperClass(String filename,String filetype, String uid,String url,String category) {
        this.filename = filename;
        this.filetype=filetype;
        this.url = url;
        this.uid = uid;
        this.category=category;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }
}


