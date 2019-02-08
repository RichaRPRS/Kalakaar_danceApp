package com.kalakaar.admin.danceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class PostDetailsActivity extends AppCompatActivity {


    TextView heading,desc,contact,category,subcat,city,state,country,providor,venue;
    Button submit;
    ImageView imageView;

    public ImageLoader imageLoader;
    Context context;
    boolean check = true;

    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/saveevents.php";
    ProgressDialog dialog;

    String provid, eventid, category_id;

    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        submit=(Button)findViewById(R.id.button);
        context=this;

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

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

        //handle data coming from firebase notification
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //bundle must contain all info sent in "data" field of the notification
            String headingt= bundle.getString("heading");
            String description= bundle.getString("description");
            String contacts= bundle.getString("contact");
            String venuet= bundle.getString("venue");
            String categoryt= bundle.getString("category");
            String subcategory= bundle.getString("subcategory");
            String cityt= bundle.getString("city");
            String statet= bundle.getString("state");
            String countryt= bundle.getString("country");
            String proname= bundle.getString("proname");
            String imaged= bundle.getString("image");
            provid= bundle.getString("provid");
            eventid= bundle.getString("eventid");
            category_id= bundle.getString("category_id");
            //Toast.makeText(context, provid+" "+eventid+" "+category_id, Toast.LENGTH_SHORT).show();
            heading.setText(headingt);
            imageView.setImageResource(R.drawable.image2);

            ImageView image = imageView;
            imageLoader.DisplayImage(imaged, image);

            desc.setText(description);
            contact.setText("Contact NO : " + contacts);
            venue.setText("Venue : "+venuet);
            category.setText("Category : " + categoryt);
            subcat.setText("Sub_Category : " + subcategory);
            city.setText("City : " + cityt);
            state.setText("State : " + statet);
            country.setText("Country : "+countryt);
            providor.setText("Provider Name : " + proname);
            DataFields.user_id = pref.getString("user_id1", "");
            DataFields.user_name = pref.getString("user_name1", "");
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(PostDetailsActivity.this, "Applied for this ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("You want to apply for this post !");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendtodb();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Not applied", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    public void sendtodb(){
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
                            sendFCMmsg(provid);
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
                params.put("provid", provid);
                params.put("studid", DataFields.user_id);
                params.put("eventid", eventid);
                params.put("category_id", category_id);
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
                dialog.show();
                //progressDialog = ProgressDialog.show(AddpostActivity.this,"Post is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                dialog.dismiss();
                finish();
                moveTaskToBack(true);
            }
            @Override
            protected String doInBackground(Void... params) {
                PostDetailsActivity.ImageProcessClass imageProcessClass = new PostDetailsActivity.ImageProcessClass();
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
}
