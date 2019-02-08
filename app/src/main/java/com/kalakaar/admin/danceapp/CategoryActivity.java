package com.kalakaar.admin.danceapp;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;
    ImageView imageView;
    Button prompt;
    Context context;

    SharedPreferences.Editor editor;
    SharedPreferences pref;

    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/fetcheventsall.php";

    String url="http://reichprinz.com/teaAndroid/danceapp/fetchcategories.php";
    ProgressDialog dialog;

    Spinner spincat,spinsubcat;
    List<String> list,list2,listid;
    ArrayList<HashMap> mylist;

    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        listView=(ListView)findViewById(R.id.list);
        prompt=(Button)findViewById(R.id.button4);
        imageView=(ImageView)findViewById(R.id.imageView5);
        imageView.setVisibility(View.GONE);
        context=this;
        activity = this;

        if (!checkPermission()) {
            requestPermission();
        }

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        DataFields.user_id = pref.getString("user_id1", "");
        DataFields.user_name = pref.getString("user_name1", "");
        DataFields.user_mobile = pref.getString("mobile1", "");
        DataFields.role = pref.getString("role", "");

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        showadds();

        serverData();

        prompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                //final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                spincat=(Spinner) promptsView.findViewById(R.id.spinnercateg);
                spinsubcat=(Spinner) promptsView.findViewById(R.id.spinnersubcat);
                ProgressBar progressBars=(ProgressBar)promptsView.findViewById(R.id.progressBar);
                final Spinner  spcountry=(Spinner)promptsView.findViewById(R.id.spinnercountry);
                final Spinner state=(Spinner) promptsView.findViewById(R.id.spinnerstat);
                //final Spinner city=(Spinner) promptsView.findViewById(R.id.spinnercity);
                spinsubcat.setVisibility(View.GONE);

                progressBars.setVisibility(View.VISIBLE);

                spinnerdata(progressBars);

                ArrayAdapter<String> adpcountry = new ArrayAdapter<String>(context,R.layout.spinner_item, CountryNames.Countryar);
                spcountry.setAdapter(adpcountry);

                spincat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        list2 = new ArrayList<String>();
                        listid = new ArrayList<String>();
                        if (spincat.getSelectedItem().equals("No Category")){
                            spinsubcat.setVisibility(View.GONE);
                        }else if (spincat.getSelectedItem().equals("Category")){
                            spinsubcat.setVisibility(View.GONE);
                        }else if (spincat.getSelectedItem().toString().equals("All")){
                            spinsubcat.setVisibility(View.GONE);
                        }else {
                            spinsubcat.setVisibility(View.VISIBLE);
                            for (int i = 0; i < mylist.size(); i++) {
                                HashMap<String, String> hashmap = mylist.get(i);
                                String cat_id = hashmap.get("Category_Id");
                                String categ = hashmap.get("CategoryName");
                                String subcat = hashmap.get("subcategory");
                                if (spincat.getSelectedItem().equals(categ)) {
                                    list2.add(subcat);
                                    listid.add(cat_id);
                                }
                            }
                        }
                        if (list2.size() > 0) {
                            ArrayAdapter<String> adp1 = new ArrayAdapter<String>(context, R.layout.spinner_item, list2);
                            spinsubcat.setAdapter(adp1);
                        } else {
                            list2.add("No items");
                            ArrayAdapter<String> adp1 = new ArrayAdapter<String>(context, R.layout.spinner_item, list2);
                            spinsubcat.setAdapter(adp1);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spcountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position!=0){
                            state.setVisibility(View.VISIBLE);
                            StateNames.addStates(context,state,spcountry.getSelectedItem().toString());
                        }else {
                            state.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("search",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        //Toast.makeText(context, categoryids, Toast.LENGTH_SHORT).show();
                                        if (spincat.getSelectedItem().toString().equals("No Category")){
                                            copyarray();
                                        }else if (spincat.getSelectedItem().toString().equals("Category")){
                                            if (!spcountry.getSelectedItem().toString().equals("Country")){
                                                if (!state.getSelectedItem().toString().equals("State")){
                                                    sortcitywise(spcountry.getSelectedItem().toString(),state.getSelectedItem().toString());
                                                }else {
                                                    sortstatewise(spcountry.getSelectedItem().toString());
                                                }
                                            }else {
                                                copyarray();
                                            }
                                        }else if (spincat.getSelectedItem().toString().equals("All")){
                                            if (!spcountry.getSelectedItem().toString().equals("Country")){
                                                if (!state.getSelectedItem().toString().equals("State")){
                                                    sortcitywise(spcountry.getSelectedItem().toString(),state.getSelectedItem().toString());
                                                }else {
                                                    sortstatewise(spcountry.getSelectedItem().toString());
                                                }
                                            }else {
                                                sortSpinall();
                                            }
                                        }
                                        else {
                                            int ppps=spinsubcat.getSelectedItemPosition();
                                            int cppps=spincat.getSelectedItemPosition();
                                            String categoryids;
                                            if (cppps >= 1) {
                                                categoryids = listid.get(ppps);
                                            } else {
                                                categoryids = "0";
                                            }
                                            if (!spcountry.getSelectedItem().toString().equals("Country")){
                                                if (!state.getSelectedItem().toString().equals("State")){
                                                    sortBoth(categoryids,spcountry.getSelectedItem().toString(),state.getSelectedItem().toString());
                                                }else {
                                                    sortCatStat(categoryids,spcountry.getSelectedItem().toString());
                                                }
                                            }else {
                                                sortSpin(categoryids);
                                            }
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel= dataModels.get(position);
                //Toast.makeText(CategoryActivity.this, " :"+dataModel.getHeading(), Toast.LENGTH_SHORT).show();
                Intent it=new Intent(getApplicationContext(),CategoryDetailsActivity.class);
                it.putExtra("position",dataModel);
                startActivity(it);
            }
        });
    }

    public void spinnerdata(final ProgressBar progressBar){
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                progressBar.setVisibility(View.GONE);
                //Toast.makeText(context, "data "+string, Toast.LENGTH_SHORT).show();
                if (string.equals("No Result"))
                {
                    Toast.makeText(context, "Something went wrong try after some time", Toast.LENGTH_SHORT).show();
                }else {
                    mylist = new ArrayList<HashMap>();
                    list = new ArrayList<String>();
                    try {
                        JSONArray fruitsArray = new JSONArray(string);
                        list.add("Category");
                        for (int i = 0; i < fruitsArray.length(); ++i) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            JSONObject jsonObject = fruitsArray.getJSONObject(i);

                            String id = jsonObject.optString("Category_Id");
                            String categoryn = jsonObject.getString("CategoryName");
                            String subcate = jsonObject.optString("SubcategoryName");
                            if (!list.contains(categoryn)) {
                                list.add(categoryn);
                            }
                            hashMap.put("Category_Id", id);
                            hashMap.put("CategoryName", categoryn);
                            hashMap.put("subcategory", subcate);
                            mylist.add(hashMap);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                vendorspinner();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Some error occurred!!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Something went wrong Check your Connection !");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spinnerdata(progressBar);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                alertDialogBuilder.show();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    public void vendorspinner(){
        if (list.size()>0) {
            ArrayAdapter<String> adp = new ArrayAdapter<String>(context,R.layout.spinner_item, list);
            spincat.setAdapter(adp);
        }else {
            list.add("No Category");
            ArrayAdapter<String> adp = new ArrayAdapter<String>(context,R.layout.spinner_item, list);
            spincat.setAdapter(adp);
        }
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
                        Image=Image.replaceAll("\\/", "/");
                        DataModel entriesModel=new DataModel(Heading,desc,venue,mobile,CategoryName,SubcategoryName,city,State,country,Image,category_id,prov_name,Event_Id,prov_id,EndDate);
                        dataModels.add(entriesModel);
                        DataFields.datafio.add(entriesModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (dataModels.size()>0) {
                    adapter= new CustomAdapter(dataModels,getApplicationContext());
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
        if (DataFields.role.equals(DataFields.user)) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }else if (DataFields.role.equals(DataFields.post)) {
            getMenuInflater().inflate(R.menu.menu_main2, menu);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.kalakaar.admin.danceapp");
                startActivity(Intent.createChooser(shareIntent, "Share apps link using"));
                break;
            case R.id.aboutus:
                Intent it=new Intent(getApplicationContext(),AboutusActivity.class);
                startActivity(it);
                break;
            case R.id.contactus:
                //Toast.makeText(context, "Item Three clicked", Toast.LENGTH_SHORT).show();
                Intent it1=new Intent(getApplicationContext(),ContactusActivity.class);
                startActivity(it1);
                break;
            case R.id.logout:
                //Toast.makeText(context, "Item logout clicked", Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.commit();
                Intent it2=new Intent(getApplicationContext(),MainActivity.class);
                CategoryActivity.this.finish();
                startActivity(it2);
                break;
            case R.id.profile:
                Intent intent=new Intent(getApplicationContext(),UserEditActivity.class);
                startActivity(intent);
                break;

            case R.id.provd:
                Intent intent1=new Intent(getApplicationContext(),PostsdetailsActivity.class);
                CategoryActivity.this.finish();
                startActivity(intent1);
        }
        return true;
    }

    public void copyarray(){
        dataModels.clear();
        int a=DataFields.datafio.size();
        for (int i = 0 ; i<a;i++){
            dataModels.add(DataFields.datafio.get(i)) ;
            adapter.notifyDataSetChanged();
        }
    }

    public void sortSpinall(){
        int a=DataFields.datafio.size();
        dataModels.clear();
        for (int i = 0 ; i<a;i++){
            dataModels.add(DataFields.datafio.get(i)) ;
        }
        if (dataModels.size()>0){
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }else {
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void sortSpin(String str){
        int a=DataFields.datafio.size();
        ArrayList<DataModel> temp=new ArrayList<DataModel>();
        for (int i = 0 ; i<a;i++){
            temp.add(DataFields.datafio.get(i)) ;
        }
        dataModels.clear();
        for (int i = 0; i < a; i++) {
            if (temp.get(i).getCategory_id().equals(str)) {
                dataModels.add(temp.get(i));
            }
        }
        if (dataModels.size()>0){
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }else {
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public  void sortBoth(String str,String strst,String strct){
        int a=DataFields.datafio.size();
        ArrayList<DataModel> temp=new ArrayList<DataModel>();
        for (int i = 0 ; i<a;i++){
            temp.add(DataFields.datafio.get(i)) ;
        }
        dataModels.clear();
        for (int i = 0; i < a; i++) {
            if (temp.get(i).getCategory_id().equals(str) && temp.get(i).getCountry().equals(strst) && temp.get(i).getState().equals(strct)) {
                dataModels.add(temp.get(i));
            }
        }
        if (dataModels.size()>0){
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }else {
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public  void sortCatStat(String str,String strst){
        int a=DataFields.datafio.size();
        ArrayList<DataModel> temp=new ArrayList<DataModel>();
        for (int i = 0 ; i<a;i++){
            temp.add(DataFields.datafio.get(i)) ;
        }
        dataModels.clear();
        for (int i = 0; i < a; i++) {
            if (temp.get(i).getCategory_id().equals(str) && temp.get(i).getCountry().equals(strst)) {
                dataModels.add(temp.get(i));
            }
        }
        if (dataModels.size()>0){
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }else {
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void sortstatewise(String str){
        int a=DataFields.datafio.size();
        ArrayList<DataModel> temp=new ArrayList<DataModel>();
        for (int i = 0 ; i<a;i++){
            temp.add(DataFields.datafio.get(i)) ;
        }
        dataModels.clear();
        for (int i = 0; i < a; i++) {
            if (temp.get(i).getCountry().equals(str)) {
                dataModels.add(temp.get(i));
            }
        }
        if (dataModels.size()>0){
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }else {
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void sortcitywise(String strst,String strct){
        int a=DataFields.datafio.size();
        ArrayList<DataModel> temp=new ArrayList<DataModel>();
        for (int i = 0 ; i<a;i++){
            temp.add(DataFields.datafio.get(i)) ;
        }
        dataModels.clear();
        for (int i = 0; i < a; i++) {
            if (temp.get(i).getCountry().equals(strst) && temp.get(i).getState().equals(strct)) {
                dataModels.add(temp.get(i));
            }
        }
        if (dataModels.size()>0){
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }else {
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
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
