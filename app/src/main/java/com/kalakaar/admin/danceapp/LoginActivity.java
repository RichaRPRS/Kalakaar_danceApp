package com.kalakaar.admin.danceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button login,register;
    EditText username,pass;
    CheckBox showp;
    TextView signuptext;
    ProgressDialog dialog;

    String url;

    Context context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final String sessionId= getIntent().getStringExtra("bd");
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        context=this;

        signuptext=(TextView)findViewById(R.id.textView15);
        if (sessionId.equals("PAR")){
            url="http://reichprinz.com/teaAndroid/danceapp/checkusers.php";
            DataFields.role=DataFields.user;
            signuptext.setVisibility(View.GONE);
        }else {
            url="http://reichprinz.com/teaAndroid/danceapp/checkpostuser.php";
            DataFields.role=DataFields.post;
        }

        login=(Button)findViewById(R.id.button3);
        register=(Button)findViewById(R.id.buttonregister);

        username=(EditText) findViewById(R.id.textuname);
        pass=(EditText) findViewById(R.id.textpass);

        username.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        showp=(CheckBox)findViewById(R.id.checkBox);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().trim().length()<=0){
                    username.setError("Please Enter Mobile Number !");
                    username.requestFocus();
                }else if (username.getText().toString().trim().length()!=10){
                    username.setError("Please Enter Correct 10 digit Number !");
                    username.requestFocus();
                }else if (pass.getText().toString().trim().length()<=0){
                    pass.setError("Please Enter password");
                    pass.requestFocus();
                }else {
                    serverdata(username.getText().toString(), pass.getText().toString());
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataFields.role.equals(DataFields.user)) {
                    if (pref.contains("role")&&pref.getString("role", "").equals(DataFields.post)){
                        FragmentManager fm = getSupportFragmentManager();
                        ProvidcheckFragment fragment = ProvidcheckFragment.newInstance(pref.getString("user_id", ""));
                        fragment.show(fm, "fragment_edit_name");
                        fragment.setCancelable(false);
                    }else {
                        Intent it = new Intent(getApplicationContext(), RegistrationActivity.class);
                        startActivity(it);
                    }
                }else {
                    Intent it = new Intent(getApplicationContext(), RegistraAdminActivity.class);
                    startActivity(it);
                }
            }
        });

        showp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pass.setSelection(pass.getText().length());
                } else {
                    // hide password
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass.setSelection(pass.getText().length());
                }
            }
        });
    }

    public void serverdata(final String mobil, final String passwd){
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading....");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                dialog.dismiss();
                if (string.equals("not")){
                    Toast.makeText(context, "Username and password Not match", Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                }else {
                    //Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                    try {
                        JSONArray fruitsArray = new JSONArray(string);
                        for (int i = 0; i < fruitsArray.length(); ++i) {
                            JSONObject jsonObject = fruitsArray.getJSONObject(i);
                            String name = jsonObject.getString("Name");
                            String mobile = jsonObject.optString("PhoneNo");
                            String iduser ;
                            Intent it;
                            if (DataFields.role.equals(DataFields.user)) {
                                iduser = jsonObject.getString("Stud_Id");
                                editor.putString("user_name1", name);
                                editor.putString("user_id1", iduser);
                                editor.putString("mobile1", mobile);
                                it = new Intent(getApplicationContext(), CategoryActivity.class);
                            }else {
                                iduser = jsonObject.getString("Prov_Id");
                                editor.putString("user_name", name);
                                editor.putString("user_id", iduser);
                                editor.putString("mobile", mobile);
                                it = new Intent(getApplicationContext(), PostsdetailsActivity.class);
                            }
                            String notification_id = jsonObject.optString("notification_id");
                            if (pref.contains("role")) {
                                //editor.putString("role", DataFields.role);
                                //Toast.makeText(context, "Role", Toast.LENGTH_SHORT).show();
                            }else {
                                editor.putString("role", DataFields.role);
                            }
                            editor.commit();
                            DataFields.user_id = iduser;
                            DataFields.user_mobile = mobile;
                            DataFields.user_name=name;
                            if (!DataFields.Firebase_Token.equals(notification_id)) {
                                if (DataFields.role.equals(DataFields.user)) {
                                    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/updatefcmiduser.php";
                                    updateRegid(DataFields.Firebase_Token,HttpUrl,it,iduser);
                                }else {
                                    String HttpUrl2="http://reichprinz.com/teaAndroid/danceapp/updatefcmidprovd.php";
                                    updateRegid(DataFields.Firebase_Token,HttpUrl2,it,iduser);
                                }
                            }else {
                                LoginActivity.this.finish();
                                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(it);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Something went wrong Check your Connection !");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        serverdata(mobil,passwd);
                    }
                });
                alertDialogBuilder.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();
                // Adding All values to Params.
                params.put("mobile", mobil);
                params.put("password", passwd);

                return params;
            }
        };
        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    public void updateRegid(final String regid, String httpUrl, final Intent it, final String userid){

        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, httpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        dialog.dismiss();
                        if (ServerResponse.equals("1")) {
                            //Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                            LoginActivity.this.finish();
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(it);
                        } else {
                            Toast.makeText(context, "Some error Occurred ! Please try again", Toast.LENGTH_SHORT).show();
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
                params.put("userid", userid);
                params.put("firbaseid", regid);
                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
