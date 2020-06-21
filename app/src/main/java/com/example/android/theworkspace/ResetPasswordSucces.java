package com.example.android.theworkspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ResetPasswordSucces extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_succes);
        ImageView imageView = findViewById(R.id.back_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void goLogin (View view){
        Intent goLoginIntent = new Intent(ResetPasswordSucces.this, LoginActivity.class);
        startActivity(goLoginIntent);
        finish();
    }
}
