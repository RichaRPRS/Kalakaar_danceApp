package com.kalakaar.admin.danceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener,CategoryFragment.OnHeadlineSelectedListener{

    Button submit,choosecategory;
    EditText names,contact,email,address,password,repasswd;
    RadioGroup radioGroup;
    RadioButton radioButton;
    List<String> listnames,listsetid;
    Spinner state,city,spcountry;
    TextView textView;

    Context context;

    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/registeruser2.php";
    ProgressDialog dialog;

    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        context=this;
        spcountry=(Spinner)findViewById(R.id.spinnercountry);
        state=(Spinner)findViewById(R.id.spinnerstate);
        city=(Spinner)findViewById(R.id.spinnercity);
        city.setVisibility(View.GONE);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        submit=(Button)findViewById(R.id.button);
        choosecategory=(Button)findViewById(R.id.btncheck);
        radioGroup=(RadioGroup)findViewById(R.id.radiogroup);

        names=(EditText)findViewById(R.id.editnames);
        contact=(EditText)findViewById(R.id.editcont);
        email=(EditText)findViewById(R.id.editemail);
        address=(EditText)findViewById(R.id.editaddress);
        password=(EditText)findViewById(R.id.editpass);
        repasswd=(EditText)findViewById(R.id.editrepass);
        textView=(TextView)findViewById(R.id.textView27);
        //textView.setMovementMethod(new ScrollingMovementMethod());
        contact.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        listnames = new ArrayList<String>();
        listsetid = new ArrayList<String>();

        ArrayAdapter<String> adpcountry = new ArrayAdapter<String>(this,R.layout.spinner_item, CountryNames.Countryar);
        spcountry.setAdapter(adpcountry);

        /*ArrayAdapter<String> adpstate = new ArrayAdapter<String>(context,R.layout.spinner_item, StateClass.states);
        state.setAdapter(adpstate);*/

        submit.setOnClickListener(this);

        choosecategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // promptView();
                FragmentManager fm = getSupportFragmentManager();
                CategoryFragment editNameDialogFragment = CategoryFragment.newInstance();
                editNameDialogFragment.show(fm, "fragment_edit_name");
                editNameDialogFragment.setCancelable(false);
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

    }

    @Override
    public void onClick(View v) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
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
        }else if (listsetid.size()==0){
            Toast.makeText(context, "Please select Categories First", Toast.LENGTH_SHORT).show();
        }else if (spcountry.getSelectedItem().toString().equals("Country")){
            Toast.makeText(context, "Please select any Country", Toast.LENGTH_SHORT).show();
        }else if (state.getSelectedItem().toString().equals("State")){
            Toast.makeText(context, "Please select any state", Toast.LENGTH_SHORT).show();
        }else if (spcountry.getSelectedItem().toString().equals("India")&& city.getSelectedItem().toString().equals("City")){
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
            final String cityname;
            if (spcountry.getSelectedItem().toString().equals("India")&& !city.getSelectedItem().toString().equals("City")){
                cityname=city.getSelectedItem().toString();
            }
            else {
                cityname="";
            }

            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {

            }
            dialog = new ProgressDialog(context);
            dialog.setMessage("Please Wait....");
            dialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String ServerResponse) {
                            dialog.dismiss();
                            if (ServerResponse.equals("1")) {
                                Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(context,LoginActivity.class);
                                intent.putExtra("bd","PAR");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                RegistrationActivity.this.finish();
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
                    params.put("country",spcountry.getSelectedItem().toString());
                    params.put("state", state.getSelectedItem().toString());
                    params.put("city", cityname);
                    params.put("gender", radioButton.getText().toString());
                    params.put("category_id", listsetid.toString());
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


    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof CategoryFragment) {
            CategoryFragment headlinesFragment = (CategoryFragment) fragment;
            headlinesFragment.setOnHeadlineSelectedListener(this);
        }
    }

    public void onArticleSelected(List<String> names,List<String> ids) {
        textView.setText("Selected Categories: "+names.toString());
        textView.setMovementMethod(new ScrollingMovementMethod());
        listnames=names;
        listsetid=ids;
    }

}
