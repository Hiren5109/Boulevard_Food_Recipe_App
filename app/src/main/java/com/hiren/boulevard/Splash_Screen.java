package com.hiren.boulevard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash_Screen extends AppCompatActivity {
    InternetReceiver internetReceiver = new InternetReceiver();
    BroadcastReceiver broadcastReceiver = null;
    private static int SPLASH_TIME_OUT = 5000;
    TextView tvversion, tvtitle;
    ImageView ivsplash;
    Animation topAnimantion, bottomAnimation, middleAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.Emerald));
        }
        tvtitle = findViewById(R.id.tVtitle);
        tvversion = findViewById(R.id.tVversion);

        tvtitle = findViewById(R.id.tVtitle);
        tvversion = findViewById(R.id.tVversion);
        ivsplash = findViewById(R.id.iVsplash);

        topAnimantion = AnimationUtils.loadAnimation(this, R.anim.top_animantion);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animantion);
        middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);

        ivsplash.setAnimation(topAnimantion);
        tvtitle.setAnimation(middleAnimation);
        tvversion.setAnimation(bottomAnimation);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
