package com.example.android.theworkspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    ProgressDialog progress;
    TextInputLayout regName, regUserName, regEmail, regPassword;
    Button regBtn, regToLoginBtn;
    RadioGroup gender;
    RadioButton radioMale, radioFemale, radioOther;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progress = new ProgressDialog(SignUp.this);
        //Connects all xml elements in activity_sign_up
        regName = findViewById(R.id.name);
        regUserName = findViewById(R.id.username);
        regEmail = findViewById(R.id.email);
        regPassword = findViewById(R.id.password);
        radioMale = findViewById(R.id.male);
        radioFemale = findViewById(R.id.female);
        radioOther = findViewById(R.id.other);
        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.callLogin);
        gender = findViewById(R.id.gender);

        // Save data in Firebase on button click
        reference = FirebaseDatabase.getInstance().getReference("Users");
        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    //Checks the input validation by the user
    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();
        String noDigits = "^([a-zA-Z])+([\\ A-Za-z]+)*$";
        if (val.isEmpty()) {
            regName.setError("Field cannot be empty");
            return false;
        }
        else if(!val.matches(noDigits)){
            regName.setError("Name cannot have digits");
            return false;
        }
        else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = regUserName.getEditText().getText().toString();
        String noChars = "^([a-zA-Z0-9])+([\\w@.])+$";
        if (val.isEmpty()) {
            regUserName.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15 || val.length() < 4) {
            regUserName.setError("Username limit (4-15) characters");
            return false;
        } else if (!val.matches(noChars)) {
            regUserName.setError("Only . and @ are allowed");
            return false;
        } else {
            regUserName.setError(null);
            regUserName.setErrorEnabled(false);
            return true;
        }
    }


    private Boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "^(.+)@(.+)$";
        if (val.isEmpty()) {
            regEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        } else {
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();
        String passVal = "^(?=\\S+$).{6,}$";
        if (val.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passVal)) {
            regPassword.setError("Password must have atleast 6 characters and no space-char");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }


    private Boolean validateGender() {
        if (gender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getBaseContext(), "Gender must be Selected", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    //Sava Data in firebase on button click
    public void registerUser(View view) {

        if (!validateEmail() | !validateName() | !validateUsername() | !validatePassword() | !validateGender()) {
            return;
        }

        progress.setMessage("Signing Up...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();

        String name = regName.getEditText().getText().toString();
        String username = regUserName.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();
        String gender = "";
        if (radioMale.isChecked()) {
            gender = "Male";
        } else if (radioFemale.isChecked()) {
            gender = "Female";
        } else {
            gender = "other";
        }

        final UserHelperClass helperClass = new UserHelperClass(name, username, email, gender);
        //Creates an account
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    progress.dismiss();
                    Toast.makeText(getBaseContext(), "An account with this email already exists!", Toast.LENGTH_SHORT).show();
                } else {
                  FirebaseDatabase.getInstance().getReference("Users")
                           .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(helperClass);
                    Toast.makeText(getBaseContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                    callLoginScreen(regBtn); //redirects to login screen
                }
            }
        });


    }


    //Calls Login Screen
    public void callLoginScreen(View view) {

        Intent logInIntent = new Intent(SignUp.this, LoginActivity.class);
       startActivity(logInIntent);
        progress.dismiss();
        finish();
    }


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
}
