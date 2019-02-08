package com.kalakaar.admin.danceapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostsdetailsActivity extends AppCompatActivity {

    ArrayList<DataModel> dataModels;
    ListView listView;
    TextView intructtext;
    private static CustomProviAdapter adapter;
    Context context;

    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/fetchevents.php";
    ProgressDialog dialog;

    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 1;

    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postsdetails);

        context=this;
        activity = this;

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        DataFields.user_id = pref.getString("user_id", "");
        DataFields.user_name = pref.getString("user_name", "");
        DataFields.user_mobile = pref.getString("mobile", "");

        listView=(ListView)findViewById(R.id.list);
        intructtext=(TextView)findViewById(R.id.textView23);
        intructtext.setVisibility(View.GONE);

        showadds();
        serverData();

        if (!checkPermission()) {
            requestPermission();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,AddpostActivity.class);
                PostsdetailsActivity.this.finish();
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_providor,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.profile:
                Intent inten=new Intent(context,ProvidEditActivity.class);
                startActivity(inten);
                break;
            case R.id.applyed:
                Intent it=new Intent(context,AppliedApplicationActivity.class);
                startActivity(it);
                break;
            case R.id.aboutus:
                Intent intent1=new Intent(context,AboutusActivity.class);
                startActivity(intent1);
                break;
            case R.id.contactus:
                Intent intent2=new Intent(context,ContactusActivity.class);
                startActivity(intent2);
                break;
            case R.id.logout:
                editor.clear();
                editor.commit();
                Intent intent3=new Intent(context,MainActivity.class);
                PostsdetailsActivity.this.finish();
                startActivity(intent3);
                break;
            case R.id.participant:
                if (pref.contains("user_name1")){
                    Intent intent=new Intent(context,CategoryActivity.class);
                    PostsdetailsActivity.this.finish();
                    startActivity(intent);
                }else {
                    Intent itns=new Intent(getApplicationContext(),LoginActivity.class);
                    itns.putExtra("bd","PAR");
                    PostsdetailsActivity.this.finish();
                    startActivity(itns);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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

    public void serverData(){

        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading....");
        dialog.show();
        StringRequest request2 = new StringRequest(Request.Method.POST,HttpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                dialog.dismiss();
                dataModels= new ArrayList<>();
                try {
                    JSONArray fruitsArray =  new  JSONArray(string);
                    for(int i = 0; i < fruitsArray.length(); ++i) {
                        JSONObject jsonObject=fruitsArray.getJSONObject(i);
                        String Event_Id=jsonObject.getString("Event_Id");
                        String Heading=jsonObject.optString("Heading");
                        String desc=jsonObject.getString("description");
                        String Image=jsonObject.getString("image_path");
                        String State=jsonObject.optString("State");
                        String city=jsonObject.optString("City");
                        String EndDate=jsonObject.optString("EndDate");
                        String category_id=jsonObject.optString("Category_Id");
                        String prov_id=jsonObject.optString("Prov_Id");
                        Image=Image.replaceAll("\\/", "/");
                        String approved=jsonObject.optString("approved");
                        String reason;
                        if (jsonObject.isNull("reason")){
                            reason="";
                        }else {
                            reason=jsonObject.optString("reason");
                        }
                        DataModel entriesModel=new DataModel(Heading,desc,EndDate,category_id,prov_id,city,State,Image,approved,reason);
                        dataModels.add(entriesModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (dataModels.size()>0) {
                    adapter= new CustomProviAdapter(dataModels,getApplicationContext());
                    listView.setAdapter(adapter);
                    intructtext.setVisibility(View.GONE);
                }else {
                    intructtext.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Some error occurred!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Something went wrong Check your Connection !");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        serverData();
                    }
                });
                alertDialogBuilder.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("provid", DataFields.user_id);
                return params;
            }
        };
        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request2);
    }

    public void showadds(){
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                //.addTestDevice("0966A7CD82908B0B7EB0564D067E596E")
                .build();

        mAdView.loadAd(adRequest);

    }
}
