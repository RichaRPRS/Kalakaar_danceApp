package com.kalakaar.admin.danceapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.net.URLEncoder;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.UnsupportedEncodingException;
import android.util.Base64;


public class AddpostActivity extends AppCompatActivity implements View.OnClickListener{

    EditText heading,desc,contact,venue;
    Spinner category,subcateg,state,city,spcountry;
    ImageView imageView;
    Button chooseimg,submit,datepick;
    CheckBox checkBox;
    TextView datetextv;

    List<String> list,list2,listid;
    Bitmap bitmap;
    Context context;
    ProgressDialog dialog;
    ArrayList<HashMap> mylist;
    private int mYear, mMonth, mDay;

    String url="http://reichprinz.com/teaAndroid/danceapp/fetchcategories.php";

    String ServerUploadPath ="http://reichprinz.com/teaAndroid/danceapp/img_upload_to_server.php" ;
    boolean check = true;
    ProgressDialog progressDialog ;
    String ImagePath = "image_path" ,fDate;

    private InterstitialAd mInterstitialAd;
    String headingstr,descriptionstr,venuestr,mobilestr,statestr,categorystr,subcategorystr,cityname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        context=this;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        heading=(EditText) findViewById(R.id.textvhead);
        desc=(EditText) findViewById(R.id.textvdesc);
        venue=(EditText) findViewById(R.id.textvenue);
        //desc.setHint(R.string.post_description);
        contact=(EditText) findViewById(R.id.textvcontac);

        contact.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        imageView=(ImageView)findViewById(R.id.imageView2);

        chooseimg=(Button)findViewById(R.id.buttonimg);
        submit=(Button)findViewById(R.id.button);
        datepick=(Button)findViewById(R.id.buttondatepic);
        datetextv=(TextView)findViewById(R.id.textdate);

        int tempd,tempm,tempy;
        c.add(Calendar.DATE, 7);

        tempy = c.get(Calendar.YEAR);
        tempm = c.get(Calendar.MONTH);
        tempd = c.get(Calendar.DAY_OF_MONTH);
        datetextv.setText(tempd+ "-" + (tempm + 1) + "-" + tempy);
        fDate=tempy+"-"+(tempm+1)+"-"+tempd;

        checkBox=(CheckBox)findViewById(R.id.checkBox2);

        category=(Spinner)findViewById(R.id.spinner7);
        subcateg=(Spinner)findViewById(R.id.spinner8);
        spcountry=(Spinner)findViewById(R.id.spinnercountry);
        state=(Spinner)findViewById(R.id.spinner9);
        city=(Spinner)findViewById(R.id.spinner10);
        subcateg.setVisibility(View.GONE);
        city.setVisibility(View.GONE);

        showinterstadd();

        datafromServer();

        ArrayAdapter<String> adpcountry = new ArrayAdapter<String>(this,R.layout.spinner_item, CountryNames.Countryar);
        spcountry.setAdapter(adpcountry);
        //Toast.makeText(context, DataFields.user_id+" "+DataFields.user_mobile, Toast.LENGTH_SHORT).show();


        chooseimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    contact.setText(DataFields.user_mobile);
                    contact.requestFocus();
                    contact.setSelection(contact.getText().length());
                }else {
                    contact.setText("");
                }
            }
        });

        datepick.setOnClickListener(this);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                subcateg.setVisibility(View.VISIBLE);
                list2 = new ArrayList<String>();
                listid = new ArrayList<String>();
                if (category.getSelectedItem().equals("No Category")){

                }else {
                    for (int i = 0; i < mylist.size(); i++) {
                        HashMap<String, String> hashmap = mylist.get(i);
                        String cat_id = hashmap.get("Category_Id");
                        String categ = hashmap.get("CategoryName");
                        String subcat = hashmap.get("subcategory");
                        if (category.getSelectedItem().equals(categ)) {
                            list2.add(subcat);
                            listid.add(cat_id);
                        }
                    }
                }
                if (list2.size() > 0) {
                    ArrayAdapter<String> adp1 = new ArrayAdapter<String>(context, R.layout.spinner_item, list2);
                    subcateg.setAdapter(adp1);
                } else {
                    list2.add("No items");
                    ArrayAdapter<String> adp1 = new ArrayAdapter<String>(context, R.layout.spinner_item, list2);
                    subcateg.setAdapter(adp1);
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

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spcountry.getSelectedItem().toString().equals("India")&& !state.getSelectedItem().toString().equals("State")) {
                    city.setVisibility(View.VISIBLE);
                    CitiesClass.addCities(context, city, state.getSelectedItem().toString());
                }else {
                    city.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap=getResizedBitmap(bitmap,500);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onClick(View v) {
        if (v==submit) {
            if (heading.getText().toString().trim().length() <= 0) {
                heading.setError("Please Enter Heading !");
                heading.requestFocus();
            } else if (desc.getText().toString().trim().length() <= 0) {
                desc.setError("Please Enter Description !");
                desc.requestFocus();
            }else if (venue.getText().toString().trim().length() <= 0) {
                venue.setError("Please Enter Venue !");
                venue.requestFocus();
            } else if (contact.getText().toString().trim().length() <= 0) {
                contact.setError("Please Enter Mobile Number !");
                contact.requestFocus();
            } else if (contact.getText().toString().trim().length() != 10) {
                contact.setError("Please Enter Correct 10 digit Number !");
                contact.requestFocus();
            } else if (spcountry.getSelectedItem().toString().equals("Country")){
                Toast.makeText(context, "Please select any Country", Toast.LENGTH_SHORT).show();
            }else if (state.getSelectedItem().toString().equals("State")){
                Toast.makeText(context, "Please select any state", Toast.LENGTH_SHORT).show();
            }else if (spcountry.getSelectedItem().toString().equals("India")&& city.getSelectedItem().toString().equals("City")){
                Toast.makeText(context, "Please select any City", Toast.LENGTH_SHORT).show();
            } else if (bitmap == null) {
                Toast.makeText(context, "Please Select image", Toast.LENGTH_SHORT).show();
            } else {
                if (spcountry.getSelectedItem().toString().equals("India")&& !city.getSelectedItem().toString().equals("City")){
                    cityname=city.getSelectedItem().toString();
                }
                else {
                    cityname="";
                }
                ImageUploadToServerFunction();
            }
        }else if (v==datepick){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            datetextv.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            fDate=year+"-"+(monthOfYear + 1)+"-"+dayOfMonth;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }


    public void ImageUploadToServerFunction(){
        int ppps=subcateg.getSelectedItemPosition();
        final String categoryids=listid.get(ppps);

        headingstr=heading.getText().toString();
        descriptionstr=desc.getText().toString();
        venuestr=venue.getText().toString();
        mobilestr=contact.getText().toString();
        statestr=state.getSelectedItem().toString();
        categorystr=category.getSelectedItem().toString();
        subcategorystr=subcateg.getSelectedItem().toString();

        ByteArrayOutputStream byteArrayOutputStreamObject ;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(AddpostActivity.this,"Post is Uploading..","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();
                // Printing uploading success message coming from server on android app.
                if (string1.equals("0"))
                {
                }else {
                    Toast.makeText(context,"Post is under Approval",Toast.LENGTH_LONG).show();
                    String[] data=string1.split(",");
                    String eventid=data[0];
                    String imgpath=data[1];
                    //Toast.makeText(context, eventid+" "+imgpath, Toast.LENGTH_SHORT).show();
                    sendFCMmsg(categoryids,imgpath,eventid);
                }
            }
            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put("heading", headingstr);
                HashMapParams.put("description", descriptionstr);
                HashMapParams.put("venue", venuestr);
                HashMapParams.put("mobile", mobilestr);
                HashMapParams.put("country",spcountry.getSelectedItem().toString());
                HashMapParams.put("state", statestr);
                HashMapParams.put("city", cityname);
                HashMapParams.put("prov_id", DataFields.user_id);
                HashMapParams.put("category_id", categoryids);
                HashMapParams.put(ImagePath, ConvertImage);
                HashMapParams.put("enddate", fDate);
                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public void sendFCMmsg(final String cate_id, final String imgpath,final String eventid){
        //final String url="http://reichprinz.com/teaAndroid/danceapp/sendfcmusingdbkeys.php";
        final String url="http://reichprinz.com/teaAndroid/danceapp/sendfcmtoadmin.php";
        class AsyncTaskUploadClasses extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
                //progressDialog = ProgressDialog.show(AddpostActivity.this,"Post is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();
                /*heading.setText("");
                desc.setText("");
                contact.setText("");
                imageView.setImageResource(android.R.color.transparent);
                bitmap=null;*/
                Intent it=new Intent(context,PostsdetailsActivity.class);
                AddpostActivity.this.finish();
                startActivity(it);
                // Printing uploading success message coming from server on android app.
                //Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
            }
            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put("title", "New Post Added "+headingstr);
                HashMapParams.put("message", descriptionstr);
                HashMapParams.put("category_id", cate_id);
                HashMapParams.put("heading", headingstr);
                HashMapParams.put("description", descriptionstr);
                HashMapParams.put("contact", mobilestr);
                HashMapParams.put("category", categorystr);
                HashMapParams.put("subcategory", subcategorystr);
                HashMapParams.put("city", cityname);
                HashMapParams.put("state", statestr);
                HashMapParams.put("proname", DataFields.user_name);
                HashMapParams.put("image", imgpath);
                HashMapParams.put("provid", DataFields.user_id);
                HashMapParams.put("eventid", eventid);
                String FinalData = imageProcessClass.ImageHttpRequest(url, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClasses AsyncTaskUploadClassOBJ = new AsyncTaskUploadClasses();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;
                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);
                OutPutStream = httpURLConnectionObject.getOutputStream();
                bufferedWriterObject = new BufferedWriter(
                        new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();
                RC = httpURLConnectionObject.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReaderObject.readLine()) != null){
                        stringBuilder.append(RC2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }
        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilderObject.append("&");
                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilderObject.toString();
        }
    }


    public void vendorspinner(){
        if (list.size()>0) {
            ArrayAdapter<String> adp = new ArrayAdapter<String>(context,R.layout.spinner_item, list);
            category.setAdapter(adp);
        }else {
            list.add("No Category");
            ArrayAdapter<String> adp = new ArrayAdapter<String>(context,R.layout.spinner_item, list);
            category.setAdapter(adp);
        }
    }

    public void datafromServer(){
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Waite....");
        dialog.show();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                dialog.dismiss();
                //Toast.makeText(context, "data "+string, Toast.LENGTH_SHORT).show();
                if (string.equals("No Result"))
                {
                    Toast.makeText(context, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                }else {
                    mylist = new ArrayList<HashMap>();
                    list = new ArrayList<String>();
                    try {
                        JSONArray fruitsArray = new JSONArray(string);
                        for (int i = 0; i < fruitsArray.length(); ++i) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            JSONObject jsonObject = fruitsArray.getJSONObject(i);

                            String id = jsonObject.optString("Category_Id");
                            String categoryn = jsonObject.getString("CategoryName");
                            String subcate = jsonObject.optString("SubcategoryName");
                            if (!list.contains(categoryn) && !categoryn.contains("All")) {
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
                dialog.dismiss();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Something went wrong Check your Connection !");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        datafromServer();
                    }
                });
                alertDialogBuilder.show();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            //Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        super.onBackPressed();
    }

    public void showinterstadd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build());
    }

}
