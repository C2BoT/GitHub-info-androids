package com.tg.githubinfo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import pl.droidsonroids.gif.GifImageView;

public class GifWifi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_error);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) GifImageView gifImageView = findViewById(R.id.gifwifiview);
        gifImageView.setImageResource(R.drawable.wifi_error);

    }
}
