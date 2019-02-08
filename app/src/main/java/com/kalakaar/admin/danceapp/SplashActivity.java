package com.kalakaar.admin.danceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    Handler handler;
    Context context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        context=this;
        pref = getApplicationContext().getSharedPreferences("MyPref2", 0); // 0 - for private mode
        editor = pref.edit();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    //FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    //displayFirebaseRegId();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);

                    //Log.e("Regid", "Firebase reg id: " + regId);

                    if (!TextUtils.isEmpty(regId)) {
                        //txtRegId.setText("Firebase Reg Id: " + regId);
                        //Toast.makeText(this, "Firebase Reg Id: " + regId, Toast.LENGTH_LONG).show();
                        Log.d("From Reg splash ", regId);
                        DataFields.Firebase_Token=regId;
                    }
                }
            }
        };

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //editor.clear();
                //editor.commit();
                if (pref.contains("terms")){
                    Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }else {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                    View mView = getLayoutInflater().inflate(R.layout.terms_condition, null);
                    final CheckBox mCheckBox = mView.findViewById(R.id.checkBox3);
                    Button submit=mView.findViewById(R.id.button8);
                    mBuilder.setView(mView);

                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCheckBox.isChecked()){
                                editor.putString("terms", "terms");
                                editor.commit();
                                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                                startActivity(intent);
                            }else {
                            }
                            SplashActivity.this.finish();
                        }
                    });
                }

            }
        },3000);

    }
}
