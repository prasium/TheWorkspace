package com.example.android.theworkspace;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog progress;
    Button callSignUp, login_btn;
    ImageView image;
    CheckBox rememberMe;
    TextView logoText, descText;
    TextInputLayout email, pwd;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to cover status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        progress = new ProgressDialog(LoginActivity.this);
        // Hooks for transition
        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logoImage);
        logoText = findViewById(R.id.welcome_name);
        descText = findViewById(R.id.subtext_name);
        email = findViewById(R.id.email);
        pwd = findViewById(R.id.password);
        login_btn = findViewById(R.id.btn);
       // rememberMe = findViewById(R.id.remember_me);

//
//        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(compoundButton.isChecked()){
//                    SharedPreferences preferences= getSharedPreferences("checkbox",MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString("remember","true");
//                    editor.apply();
//                }
//                else
//                {
//                    SharedPreferences preferences= getSharedPreferences("checkbox",MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString("remember","false");
//                    editor.apply();
//                }
//            }
//        });




//        SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
//        String checkbox = preferences.getString("remember","");
//        if(checkbox.equals("true")) {
            mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

            if (mFirebaseUser != null) {
                Intent dashboardIntent = new Intent(getApplicationContext(), UserFeed.class);
                startActivity(dashboardIntent);
                finishAffinity();
            }
//        }


        }

    private Boolean validateEmail() {
        String val = email.getEditText().getText().toString();
        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }


    private Boolean validatePassword() {
        String val = pwd.getEditText().getText().toString();
        if (val.isEmpty()) {
            pwd.setError("Field cannot be empty");
            return false;
        } else {
            pwd.setError(null);
            pwd.setErrorEnabled(false);
            return true;
        }
    }

    //Validate login infor
    public void loginUser(View view) {
        if (!validateEmail() | !validatePassword()) {
            return;
        } else {
            progress.setMessage("Signing in...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            isUser();
        }
    }

    private void isUser() {
        final String userEnteredEmail = email.getEditText().getText().toString().trim();
        final String userEnteredPass = pwd.getEditText().getText().toString().trim();
        mFirebaseAuth=FirebaseAuth.getInstance();
        mFirebaseAuth.signInWithEmailAndPassword(userEnteredEmail, userEnteredPass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Wrong Credentials,try again!", Toast.LENGTH_SHORT).show();
                        } else {

                                    Intent feedIntent = new Intent(getApplicationContext(), UserFeed.class);
                                    startActivity(feedIntent);
                                    finish();

                        }
                        progress.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "Check your internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Call Sign Up Screen
    public void callSignUpScreen(View view) {
        Intent signUpIntent = new Intent(LoginActivity.this, SignUp.class);

        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<View, String>(image, "logo_image");
        pairs[1] = new Pair<View, String>(logoText, "logo_text");
        pairs[2] = new Pair<View, String>(descText, "logo_desc");
        pairs[3] = new Pair<View, String>(email, "email_tran");
        pairs[4] = new Pair<View, String>(pwd, "pwd_tran");
        pairs[5] = new Pair<View, String>(login_btn, "button_tran");
        pairs[6] = new Pair<View, String>(callSignUp, "signup_tran");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
        startActivity(signUpIntent, options.toBundle());
        finish();
    }

    //To exit the app
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit the Application")
                .setMessage("Are you sure you want to close the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void forgotPassword(View view) {
        Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPassword.class);
        startActivity(forgotPasswordIntent);
    }


   }