package com.kalakaar.admin.danceapp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CategoryDetailsActivity extends AppCompatActivity {

    TextView heading,desc,contact,category,subcat,city,state,country,providor,venue;
    Button submit;
    ImageView imageView;

    public ImageLoader imageLoader;
    Context context;
    DataModel dataModel;
    boolean check = true;

    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/saveevents.php";
    ProgressDialog dialog;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);


        submit=(Button)findViewById(R.id.button);
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

        imageView=(ImageView)findViewById(R.id.imageView);
        imageLoader = new ImageLoader(context);

        dataModel = (DataModel) getIntent().getSerializableExtra("position");
        heading.setText(dataModel.getHeading());
        imageView.setImageResource(R.drawable.image2);

        ImageView image = imageView;
        imageLoader.DisplayImage(dataModel.getImg_path(), image);

        desc.setText(dataModel.getDescription());
        String temvenue="<b>Venue : </b>"+dataModel.getVenue();
        String temcont="<b>Contact NO : </b>"+dataModel.getContact();
        String temcat="<b>Category : </b>"+dataModel.getCategory();
        String temsubc="<b>Sub_Category : </b>"+dataModel.getSubcateg();
        String temcit="<b>City : </b>"+dataModel.getCity();
        String temsta="<b>State : </b>"+dataModel.getState();
        String temcountry="<b>Country : </b>"+dataModel.getCountry();
        String temprov="<b>Provider Name : </b>"+dataModel.getProvider_name();
        venue.setText(Html.fromHtml(temvenue));
        contact.setText(Html.fromHtml(temcont));
        category.setText(Html.fromHtml(temcat));
        subcat.setText(Html.fromHtml(temsubc));
        city.setText(Html.fromHtml(temcit));
        state.setText(Html.fromHtml(temsta));
        country.setText(Html.fromHtml(temcountry));
        providor.setText(Html.fromHtml(temprov));

        showinterstadd();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(PostDetailsActivity.this, "Applied for this ", Toast.LENGTH_SHORT).show();
                if (DataFields.role.equals(DataFields.user) || DataFields.role.equals(DataFields.post)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("You want to apply for this post !");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            applypost();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "Not applied", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialogBuilder.show();
                }else if (DataFields.role.equals(DataFields.guest)){
                    //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Please log_in or sign_up for apply this post !");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(context,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialogBuilder.show();
                }
            }
        });
    }

    public void applypost(){
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait....");
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        dialog.dismiss();
                        if (ServerResponse.equals("1")) {
                            Toast.makeText(context, "Applied successfully", Toast.LENGTH_SHORT).show();
                            sendFCMmsg(dataModel.getProv_Id());
                        } else if (ServerResponse.equals("2")){
                            Toast.makeText(context, "You have already applied for this!", Toast.LENGTH_SHORT).show();
                        }else {
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
                params.put("provid", dataModel.getProv_Id());
                params.put("studid", DataFields.user_id);
                params.put("eventid", dataModel.getEvent_Id());
                params.put("category_id", dataModel.category_id);
                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void sendFCMmsg(final String cate_id){
        final String url="http://reichprinz.com/teaAndroid/danceapp/fcmsenduseregid2.php";
        class AsyncTaskUploadClasses extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressDialog = ProgressDialog.show(AddpostActivity.this,"Post is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                //progressDialog.dismiss();
                // Printing uploading success message coming from server on android app.
                //Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                CategoryDetailsActivity.this.finish();
            }
            @Override
            protected String doInBackground(Void... params) {
                CategoryDetailsActivity.ImageProcessClass imageProcessClass = new CategoryDetailsActivity.ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put("title", heading.getText().toString());
                HashMapParams.put("message", DataFields.user_name+" have applied");
                HashMapParams.put("prov_id", cate_id);
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

}
