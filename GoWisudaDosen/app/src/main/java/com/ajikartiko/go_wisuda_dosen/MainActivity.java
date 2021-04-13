package com.ajikartiko.go_wisuda_dosen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.img);
        img.animate().alpha(2000).setDuration(0);

        handler = new Handler();
        handler.postDelayed(() -> {
            Intent dsp = new Intent(this, LoginActivity.class);
            startActivity(dsp);
            finish();
        }, 2000);

    }
}
