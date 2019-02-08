package com.kalakaar.admin.danceapp;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends AppCompatActivity {

    Button participate,post,skip,enter,admin;

    SharedPreferences.Editor editor;
    SharedPreferences pref;
    private Context context;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        activity = this;

        mAdView = (AdView) findViewById(R.id.adView);
        showadds();


        participate=(Button)findViewById(R.id.button);
        post=(Button)findViewById(R.id.button2);
        skip=(Button)findViewById(R.id.button3);
        enter=(Button)findViewById(R.id.button4);
        admin=(Button)findViewById(R.id.button5);

        if (!checkPermission()) {
            requestPermission();
        }

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    //FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    //txtMessage.setText(message);
                }
            }
        };
        displayFirebaseRegId();
        showHidebtns();

        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, " Participation", Toast.LENGTH_SHORT).show();
                Intent it=new Intent(getApplicationContext(),LoginActivity.class);
                it.putExtra("bd","PAR");
                startActivity(it);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, " Post", Toast.LENGTH_SHORT).show();
                Intent it=new Intent(getApplicationContext(),LoginActivity.class);
                it.putExtra("bd","POS");
                startActivity(it);
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataFields.role =DataFields.admin;
                Intent it=new Intent(getApplicationContext(),LoginadminActivity.class);
                startActivity(it);
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, " Search for lattest add", Toast.LENGTH_SHORT).show();
                DataFields.role =DataFields.guest;
                Intent it=new Intent(getApplicationContext(),CategoryActivity.class);
                startActivity(it);
            }
        });


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataFields.role = pref.getString("role", "");
                if (pref.getString("role","").equals(DataFields.user)) {
                    DataFields.user_name = pref.getString("user_name1", "");
                    DataFields.user_id = pref.getString("user_id1", "");
                    DataFields.user_mobile = pref.getString("mobile1", "");
                    Intent it = new Intent(getApplicationContext(), CategoryActivity.class);
                    startActivity(it);
                }else if (pref.getString("role","").equals(DataFields.admin)){
                    DataFields.user_name = pref.getString("user_name", "");
                    DataFields.user_id = pref.getString("user_id", "");
                    DataFields.user_mobile = pref.getString("mobile", "");
                    Intent it = new Intent(getApplicationContext(), AdminpostActivity.class);
                    startActivity(it);
                }
                else {
                    DataFields.user_name = pref.getString("user_name", "");
                    DataFields.user_id = pref.getString("user_id", "");
                    DataFields.user_mobile = pref.getString("mobile", "");
                    Intent it = new Intent(getApplicationContext(), PostsdetailsActivity.class);
                    startActivity(it);
                }
            }
        });
    }

    public void showHidebtns(){
        if (pref.contains("role")){
            participate.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            skip.setVisibility(View.GONE);
            admin.setVisibility(View.GONE);
            enter.setVisibility(View.VISIBLE);
        }else {
            enter.setVisibility(View.GONE);
            participate.setVisibility(View.VISIBLE);
            post.setVisibility(View.VISIBLE);
            skip.setVisibility(View.VISIBLE);
            admin.setVisibility(View.VISIBLE);
        }
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        //Log.e("Regid", "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            //txtRegId.setText("Firebase Reg Id: " + regId);
            //Toast.makeText(this, "Firebase Reg Id: " + regId, Toast.LENGTH_LONG).show();
            Log.d("From Registration ", regId);
            DataFields.Firebase_Token=regId;
        }
        else {
            //txtRegId.setText("Firebase Reg Id is not received yet!");
            //Toast.makeText(this, "Firebase Reg Id is not received yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(context,"WRITE_EXTERNAL_STORAGE permission allows us to access SDcards data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showHidebtns();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void showadds(){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                //.addTestDevice("0966A7CD82908B0B7EB0564D067E596E")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                //Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                //Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);

    }

}
