package com.spontivly.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv = findViewById(R.id.splash_img);
        Animation fade = AnimationUtils.loadAnimation(this, R.anim.launch_transition);
        iv.startAnimation(fade);
        final Intent intent = new Intent(this, MainActivity.class);
        Thread timer = new Thread() {
            public void run(){
                try {
                    sleep(1500);
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}
