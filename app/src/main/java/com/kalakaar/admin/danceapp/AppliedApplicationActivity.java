package com.kalakaar.admin.danceapp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppliedApplicationActivity extends AppCompatActivity {

    ListView listView;
    ImageView imageView;
    Context context;
    ProgressDialog dialog;
    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/fetchappliedevents.php";
    private static ApplicationAdapter adapter;
    ArrayList<ApplicationModel> dataModels;

    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_application);

        context=this;

        listView=(ListView)findViewById(R.id.list);
        imageView=(ImageView)findViewById(R.id.imageView6);
        imageView.setVisibility(View.GONE);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        showadds();

        serverData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationModel model=dataModels.get(position);
                String name, address,mobile,email,gender,state,city;
                name=model.getName();
                address=model.getAddress();
                mobile=model.getMobile();
                email=model.getEmail();
                gender=model.getGender();
                state=model.getState();
                city=model.getCity();
                FragmentManager fm = getSupportFragmentManager();
                UserFragment editNameDialogFragment = UserFragment.newInstance(name,address,mobile,email,gender,state,city);
                editNameDialogFragment.show(fm, "fragment_edit_name");
                editNameDialogFragment.setCancelable(false);
            }
        });
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
                        String Id=jsonObject.getString("Application_Id");
                        String name=jsonObject.optString("Name");
                        String Heading=jsonObject.optString("Heading");
                        String address,mobile,email,gender,state,city;
                        address=jsonObject.optString("Address");
                        mobile=jsonObject.optString("PhoneNo");
                        email=jsonObject.optString("Email");
                        gender=jsonObject.optString("gender");
                        state=jsonObject.optString("State");
                        city=jsonObject.optString("City");
                        ApplicationModel entriesModel=new ApplicationModel(Id,name,Heading,address,mobile,email,gender,state,city);
                        dataModels.add(entriesModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (dataModels.size()>0) {
                    adapter= new ApplicationAdapter(dataModels,getApplicationContext());
                    listView.setAdapter(adapter);
                    imageView.setVisibility(View.GONE);
                }else {
                    imageView.setVisibility(View.VISIBLE);
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
                params.put("prov_id", pref.getString("user_id", ""));
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
