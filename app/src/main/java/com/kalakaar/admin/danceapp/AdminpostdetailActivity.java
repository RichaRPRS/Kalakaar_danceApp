package com.kalakaar.admin.danceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AdminpostdetailActivity extends AppCompatActivity implements View.OnClickListener{

    TextView heading,desc,contact,category,subcat,city,state,country,providor,venue;
    Button submit;
    ImageView imageView;
    Spinner spinner;
    EditText reasonbox;
    TextInputLayout textInputLayout;

    public ImageLoader imageLoader;
    Context context;
    DataModel dataModel;
    ProgressDialog dialog;

    public static List<String> lists;
    String HttpUrl2="http://reichprinz.com/teaAndroid/danceapp/updateventadmin.php";

    String event_id;
    int origposit;

    boolean check = true;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminpostdetail);

        submit=(Button)findViewById(R.id.button);
        submit.setOnClickListener(this);
        context=this;

        heading=(TextView)findViewById(R.id.textheading);
        desc=(TextView)findViewById(R.id.textdesc);
        venue=(TextView)findViewById(R.id.textvenue);
        contact=(TextView)findViewById(R.id.textcontact);
        category=(TextView)findViewById(R.id.textcateg);
        subcat=(TextView)findViewById(R.id.textsubcat);
        city=(TextView)findViewById(R.id.textcity);
        state=(TextView)findViewById(R.id.textstate);
        country=(TextView)findViewById(R.id.textcountry);
        providor=(TextView)findViewById(R.id.textprovdr);
        spinner=(Spinner)findViewById(R.id.spinner);
        reasonbox=(EditText)findViewById(R.id.editreason);
        textInputLayout=(TextInputLayout)findViewById(R.id.textinputlayout);
        textInputLayout.setVisibility(View.GONE);

        imageView=(ImageView)findViewById(R.id.imageView);
        imageLoader = new ImageLoader(context);

        dataModel = (DataModel) getIntent().getSerializableExtra("position");
        event_id=dataModel.getEvent_Id();
        origposit=Integer.parseInt(dataModel.getApprove());
        heading.setText(dataModel.getHeading());
        imageView.setImageResource(R.drawable.image2);

        ImageView image = imageView;
        imageLoader.DisplayImage(dataModel.getImg_path(), image);
        String temvenue="<b>Venue : </b>"+dataModel.getVenue();
        String descr="<b>Description : </b>"+dataModel.getDescription();
        String temcont="<b>Contact NO : </b>"+dataModel.getContact();
        String temcat="<b>Category : </b>"+dataModel.getCategory();
        String temsubc="<b>Sub_Category : </b>"+dataModel.getSubcateg();
        String temcit="<b>City : </b>"+dataModel.getCity();
        String temsta="<b>State : </b>"+dataModel.getState();
        String temcountry="<b>Country : </b>"+dataModel.getCountry();
        String temprov="<b>Provider Name : </b>"+dataModel.getProvider_name();
        desc.setText(Html.fromHtml(descr));
        venue.setText(Html.fromHtml(temvenue));
        contact.setText(Html.fromHtml(temcont));
        category.setText(Html.fromHtml(temcat));
        subcat.setText(Html.fromHtml(temsubc));
        city.setText(Html.fromHtml(temcit));
        state.setText(Html.fromHtml(temsta));
        country.setText(Html.fromHtml(temcountry));
        providor.setText(Html.fromHtml(temprov));

        showinterstadd();

        lists = new ArrayList<String>();
        lists.add("Pending");
        lists.add("Approved");
        lists.add("Not Approved");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, lists);
        spinner.setAdapter(adapter);
        spinner.setSelection(Integer.parseInt(dataModel.getApprove()));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (origposit==position){
                    submit.setVisibility(View.GONE);
                    if (position==2){
                        textInputLayout.setVisibility(View.VISIBLE);
                        if (dataModel.getReason()==(null)){

                        }else {
                            reasonbox.setText(dataModel.getReason());
                        }
                    }else {
                        textInputLayout.setVisibility(View.GONE);
                    }
                }else {
                    submit.setVisibility(View.VISIBLE);
                    if (position==2){
                        textInputLayout.setVisibility(View.VISIBLE);
                    }else {
                        textInputLayout.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            //Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        super.onDestroy();
    }

    public void showinterstadd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build());
    }

    @Override
    public void onClick(View v) {
        if (spinner.getSelectedItemPosition()==2){
            if (reasonbox.getText().toString().trim().length()<=0 ){
                reasonbox.setError("Please Enter Reason !");
                reasonbox.requestFocus();
            }else {
                updateData();
            }
        }else {
            updateData();
        }
    }

    public void updateData(){

        final String approvid=String.valueOf(spinner.getSelectedItemPosition());
        final String reasonstr;
        if (spinner.getSelectedItemPosition()==2){
            reasonstr=reasonbox.getText().toString();
        }else {
            reasonstr="";
        }
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait....");
        dialog.show();
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        dialog.dismiss();
                        if (ServerResponse.equals("1")) {
                            dataModel.setApprove(approvid);
                            dataModel.setReason(reasonstr);
                            DataFields.arrayList.set(DataFields.arrayposit,dataModel);
                            if (approvid.equals("1")){
                                sendFCMmsg();
                            }else if (approvid.equals("2")){
                                sendFCMmsg(dataModel.getProv_Id(),"Not Approved");
                            }else {
                                sendFCMmsg(dataModel.getProv_Id(),"Pending");
                            }
                            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Some error Occured ! Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        dialog.dismiss();
                        // Showing error message if something goes wrong.volleyError.toString()
                        Toast.makeText(context, "Server error please try again ", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("eventid", event_id);
                params.put("approvid", approvid);
                params.put("reason", reasonstr);
                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void sendFCMmsg(){
        final String url="http://reichprinz.com/teaAndroid/danceapp/sendfcmusingdbkeys.php";
        class AsyncTaskUploadClasses extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.show();
                //progressDialog = ProgressDialog.show(AddpostActivity.this,"Post is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                dialog.dismiss();
                AdminpostdetailActivity.this.finish();
                // Printing uploading success message coming from server on android app.
                //Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
            }
            @Override
            protected String doInBackground(Void... params) {
                AdminpostdetailActivity.ImageProcessClass imageProcessClass = new AdminpostdetailActivity.ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put("title", dataModel.getHeading());
                HashMapParams.put("message", dataModel.getDescription());
                HashMapParams.put("category_id", dataModel.category_id);
                HashMapParams.put("heading", dataModel.getHeading());
                HashMapParams.put("description", dataModel.getDescription());
                HashMapParams.put("contact", dataModel.getContact());
                HashMapParams.put("venue", dataModel.getVenue());
                HashMapParams.put("category", dataModel.getCategory());
                HashMapParams.put("subcategory", dataModel.getSubcateg());
                HashMapParams.put("city", dataModel.getCity());
                HashMapParams.put("state", dataModel.getState());
                HashMapParams.put("country", dataModel.getCountry());
                HashMapParams.put("proname", dataModel.getProvider_name());
                HashMapParams.put("image", dataModel.getImg_path());//
                HashMapParams.put("provid", dataModel.getProv_Id());
                HashMapParams.put("eventid", dataModel.getEvent_Id());
                String FinalData = imageProcessClass.ImageHttpRequest(url, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClasses AsyncTaskUploadClassOBJ = new AsyncTaskUploadClasses();
        AsyncTaskUploadClassOBJ.execute();
    }

    //for sending when admin is not approve or pending post
    public void sendFCMmsg(final String cate_id,final String status){
        final String url="http://reichprinz.com/teaAndroid/danceapp/fcmsenduseregid.php";
        class AsyncTaskUploadClasses extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.show();
                //progressDialog = ProgressDialog.show(AddpostActivity.this,"Post is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                //progressDialog.dismiss();
                // Printing uploading success message coming from server on android app.
                //Toast.makeText(context, string1, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                AdminpostdetailActivity.this.finish();
            }
            @Override
            protected String doInBackground(Void... params) {
                AdminpostdetailActivity.ImageProcessClass imageProcessClass = new AdminpostdetailActivity.ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put("title", "Your post is "+status);
                HashMapParams.put("message", dataModel.getHeading());
                HashMapParams.put("prov_id", cate_id);
                HashMapParams.put("image", dataModel.getImg_path());
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
}
