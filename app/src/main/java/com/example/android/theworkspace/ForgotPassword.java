package com.example.android.theworkspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ImageView imageView = findViewById(R.id.back_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

        public void resetPassword(View view) {
            TextInputLayout reset_Mail = findViewById(R.id.resetMail);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String rMail= reset_Mail.getEditText().getText().toString();
            if (TextUtils.isEmpty(rMail)) {
                Toast.makeText(ForgotPassword.this, "Please enter email id", Toast.LENGTH_LONG).show();
            } else {
                auth.sendPasswordResetEmail(rMail)
                        .addOnCompleteListener(ForgotPassword.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPassword.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ForgotPassword.this, "Unable to send reset mail", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        });
            }

            Intent resetIntent = new Intent(ForgotPassword.this, ResetPasswordSucces.class);
            startActivity(resetIntent);
            finish();
        }



}
