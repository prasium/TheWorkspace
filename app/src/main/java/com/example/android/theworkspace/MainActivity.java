package com.example.android.theworkspace;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static int SPLASH_TIME_OUT=4000;
    //animation variables
    Animation topAnim,bottomAnim;
    ImageView image;
    TextView logo,credits;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);
        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        //Hooks
        image = findViewById(R.id.imageLogo);
        logo = findViewById(R.id.textName);
        credits= findViewById(R.id.textCredit);
        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        credits.startAnimation(fadeIn);
        fadeIn.setDuration(3000);
        fadeIn.setFillAfter(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent logInIntent = new Intent(MainActivity.this,LoginActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logo, "logo_text");
                    checkPermission();
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(logInIntent, options.toBundle());
                finish();
            }
        }, SPLASH_TIME_OUT);


    }

        public void checkPermission(){

        //ask for permission
            PermissionListener permissionListener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
}

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };


        TedPermission.with(MainActivity.this)
                    .setPermissionListener(permissionListener)
                .setDeniedMessage("The App requires the permission to run smoothly,Turn on the permissions at [Settings]>[Permissions]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }

}
