package com.kalakaar.admin.danceapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FullScreenViewActivity extends AppCompatActivity {

    ImageView imgDisplay;
    Button btnClose;
    public ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
        btnClose = (Button) findViewById(R.id.btnClose);
        imageLoader = new ImageLoader(this);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("message");

        ImageView image =imgDisplay;
        imageLoader.DisplayImage(message, image);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenViewActivity.this.finish();
            }
        });
    }
}