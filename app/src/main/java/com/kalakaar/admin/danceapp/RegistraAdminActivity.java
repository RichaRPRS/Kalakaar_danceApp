package com.kalakaar.admin.danceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistraAdminActivity extends AppCompatActivity {

    Button submit;
    EditText names,contact,email,address,password,repasswd;
    RadioGroup gender;
    Spinner state,city;

    Context context;
    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/registerprovider.php";

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_admin);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        context=this;
        state=(Spinner)findViewById(R.id.spinnerstate);
        city=(Spinner)findViewById(R.id.spinnercity);
        city.setVisibility(View.GONE);

        submit=(Button)findViewById(R.id.button);

        names=(EditText)findViewById(R.id.editnames);
        contact=(EditText)findViewById(R.id.editcont);
        email=(EditText)findViewById(R.id.editemail);
        address=(EditText)findViewById(R.id.editaddress);
        password=(EditText)findViewById(R.id.editpass);
        repasswd=(EditText)findViewById(R.id.editrepass);

        contact.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        //Toast.makeText(context, DataFields.Firebase_Token, Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> adpstate = new ArrayAdapter<String>(this,R.layout.spinner_item, StateClass.states);
        state.setAdapter(adpstate);


        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city.setVisibility(View.VISIBLE);
                CitiesClass.addCities(context,city,state.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (names.getText().toString().trim().length()<=0 ){
                    names.setError("Please Enter Name !");
                    names.requestFocus();
                }else if (address.getText().toString().trim().length()<=0){
                    address.setError("Please Enter Address !");
                    address.requestFocus();
                }else if (contact.getText().toString().trim().length()<=0){
                    contact.setError("Please Enter Mobile Number !");
                    contact.requestFocus();
                }else if (contact.getText().toString().trim().length()!=10){
                    contact.setError("Please Enter Correct 10 digit Number !");
                    contact.requestFocus();
                }else if (email.getText().toString().trim().length() <=0){
                    email.setError("Please Enter Email address");
                    email.requestFocus();
                } else if (!email.getText().toString().matches(emailPattern)){
                    email.setError("Please Enter valid Email address");
                    email.requestFocus();
                }else if (state.getSelectedItem().toString().equals("State")){
                    Toast.makeText(context, "Please select any state", Toast.LENGTH_SHORT).show();
                }else if (city.getSelectedItem().toString().equals("City")){
                    Toast.makeText(context, "Please select any City", Toast.LENGTH_SHORT).show();
                }else if (password.getText().toString().trim().length()<=0){
                    password.setError("Please Enter password");
                    password.requestFocus();
                }else if (repasswd.getText().toString().trim().length()<=0){
                    repasswd.setError("Please Enter password Again");
                    repasswd.requestFocus();
                }else if (!password.getText().toString().equals(repasswd.getText().toString())){
                    repasswd.setError("Password not match");
                    repasswd.requestFocus();
                }else {
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {

                    }

                    dialog = new ProgressDialog(context);
                    dialog.setMessage("Loading....");
                    dialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String ServerResponse) {
                                    dialog.dismiss();
                                    if (ServerResponse.equals("1")) {
                                        Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(context,LoginActivity.class);
                                        intent.putExtra("bd","POS");
                                        startActivity(intent);
                                        RegistraAdminActivity.this.finish();
                                    } else if (ServerResponse.equals("2")){
                                        Toast.makeText(context, "Mobile number is already Registered!", Toast.LENGTH_SHORT).show();
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
                            params.put("name", names.getText().toString());
                            params.put("address", address.getText().toString());
                            params.put("mobile", contact.getText().toString());
                            params.put("email", email.getText().toString());
                            params.put("state", state.getSelectedItem().toString());
                            params.put("city", city.getSelectedItem().toString());
                            params.put("password", password.getText().toString());
                            params.put("fcm_id", DataFields.Firebase_Token);
                            return params;
                        }
                    };
                    // Creating RequestQueue.
                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    requestQueue.add(stringRequest);
                }
            }
        });
    }
}
