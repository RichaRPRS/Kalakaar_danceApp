package com.kalakaar.admin.danceapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminpostActivity extends AppCompatActivity {

    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/fetcheventsallad.php";
    ProgressDialog dialog;
    ArrayList<DataModel> dataModels;
    private static CustomAdminAdapter adapter;
    Context context;
    ListView listView;
    ImageView imageView;

    SharedPreferences.Editor editor;
    SharedPreferences pref;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminpost);

        context=this;
        activity=this;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        listView=(ListView)findViewById(R.id.list);
        imageView=(ImageView)findViewById(R.id.imageView6);
        imageView.setVisibility(View.GONE);

        showadds();

        serverData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel= dataModels.get(position);
                DataFields.arrayList=dataModels;
                DataFields.arrayposit=position;
                //Toast.makeText(CategoryActivity.this, " :"+dataModel.getHeading(), Toast.LENGTH_SHORT).show();
                Intent it=new Intent(getApplicationContext(),AdminpostdetailActivity.class);
                it.putExtra("position",dataModel);
                startActivity(it);
            }
        });
    }

    @Override
    protected void onResume() {
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    public void serverData(){
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading....");
        dialog.show();
        StringRequest request2 = new StringRequest(HttpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                dialog.dismiss();
                dataModels= new ArrayList<>();

                //Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
                try {
                    JSONArray fruitsArray =  new  JSONArray(string);
                    for(int i = 0; i < fruitsArray.length(); ++i) {
                        JSONObject jsonObject=fruitsArray.getJSONObject(i);
                        String Event_Id=jsonObject.getString("Event_Id");
                        String Heading=jsonObject.optString("Heading");
                        String desc=jsonObject.getString("description");
                        String venue=jsonObject.getString("venue");
                        String Image=jsonObject.getString("image_path");
                        String country=jsonObject.optString("Country");
                        String State=jsonObject.optString("State");
                        String city=jsonObject.optString("City");
                        String EndDate=jsonObject.optString("EndDate");
                        String mobile=jsonObject.optString("mobile");
                        String category_id=jsonObject.optString("Category_Id");
                        String prov_id=jsonObject.optString("Prov_Id");
                        String CategoryName=jsonObject.optString("CategoryName");
                        String SubcategoryName=jsonObject.optString("SubcategoryName");
                        String prov_name=jsonObject.optString("Name");
                        String approved=jsonObject.optString("approved");
                        String reason;
                        if (jsonObject.isNull("reason")){
                            reason=null;
                        }else {
                            reason=jsonObject.optString("reason");
                        }
                        Image=Image.replaceAll("\\/", "/");
                        DataModel entriesModel=new DataModel(Heading,desc,venue,mobile,CategoryName,SubcategoryName,city,State,country,Image,category_id,prov_name,Event_Id,prov_id,EndDate,approved,reason);
                        dataModels.add(entriesModel);
                        DataFields.datafio.add(entriesModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (dataModels.size()>0) {
                    adapter= new CustomAdminAdapter(dataModels,getApplicationContext(),activity);
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
        });
        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.logout:
                //Toast.makeText(context, "Item logout clicked", Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.commit();
                Intent it2=new Intent(getApplicationContext(),MainActivity.class);
                AdminpostActivity.this.finish();
                startActivity(it2);
                break;
            case R.id.profile:
                Intent intent=new Intent(getApplicationContext(),AdmineditActivity.class);
                startActivity(intent);
                break;
        }
        return true;
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
