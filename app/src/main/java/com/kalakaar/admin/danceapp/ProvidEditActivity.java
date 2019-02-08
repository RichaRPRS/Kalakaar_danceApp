package com.kalakaar.admin.danceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;

public class ProvidEditActivity extends AppCompatActivity implements View.OnClickListener{

    Spinner state,city;
    Button submit;
    EditText names,contact,email,address,pass;

    Context context;
    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/fetchprovbyid.php";
    String HttpUrl2="http://reichprinz.com/teaAndroid/danceapp/updateprovidor.php";

    ProgressDialog dialog;
    ArrayAdapter<String> adpstate;

    CheckBox showp;

    SharedPreferences.Editor editor;
    SharedPreferences pref;
    int checks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provid_edit);

        context=this;
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        state=(Spinner)findViewById(R.id.spinnerstate);
        city=(Spinner)findViewById(R.id.spinnercity);
        city.setVisibility(View.GONE);

        submit=(Button)findViewById(R.id.button);

        names=(EditText)findViewById(R.id.editnames);
        contact=(EditText)findViewById(R.id.editcont);
        email=(EditText)findViewById(R.id.editemail);
        address=(EditText)findViewById(R.id.editaddress);
        pass=(EditText)findViewById(R.id.editpass);

        showp=(CheckBox)findViewById(R.id.checkBox);

        contact.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Waite....");
        dialog.show();

        adpstate = new ArrayAdapter<String>(this,R.layout.spinner_item, StateClass.states);
        state.setAdapter(adpstate);

        submit.setOnClickListener(this);

        showp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pass.setSelection(pass.getText().length());
                    pass.requestFocus();
                } else {
                    // hide password
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pass.setSelection(pass.getText().length());
                    pass.requestFocus();
                }
            }
        });

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(++checks > 2) {
                    CitiesClass.addCities(context, city, state.getSelectedItem().toString());
                    //Toast.makeText(context, "selected", Toast.LENGTH_SHORT).show();
                }else {
                    //Toast.makeText(context, "first time", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        serverData();
    }

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
        }else if (state.getSelectedItem().toString().equals("No Category")){
            Toast.makeText(context, "No State available", Toast.LENGTH_SHORT).show();
        }else if (state.getSelectedItem().toString().equals("State")){
            Toast.makeText(context, "Please select any state", Toast.LENGTH_SHORT).show();
        }else if (city.getSelectedItem().toString().equals("City")){
            Toast.makeText(context, "Please select any City", Toast.LENGTH_SHORT).show();
        }else if (pass.getText().toString().trim().length() <=0){
            pass.setError("Password not blank");
            pass.requestFocus();
        }else {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {

            }
            dialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String ServerResponse) {
                            dialog.dismiss();
                            if (ServerResponse.equals("1")) {
                                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                                ProvidEditActivity.this.finish();
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
                    params.put("userid", pref.getString("user_id", ""));
                    params.put("name", names.getText().toString());
                    params.put("address", address.getText().toString());
                    params.put("mobile", contact.getText().toString());
                    params.put("email", email.getText().toString());
                    params.put("state", state.getSelectedItem().toString());
                    params.put("city", city.getSelectedItem().toString());
                    params.put("password", pass.getText().toString());
                    return params;
                }
            };
            // Creating RequestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);

        }
    }

    public void serverData(){
        dialog.show();
        StringRequest request2 = new StringRequest(Request.Method.POST,HttpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                dialog.dismiss();
                //dataModels= new ArrayList<>();

                //Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
                try {
                    JSONArray fruitsArray =  new  JSONArray(string);
                    for(int i = 0; i < fruitsArray.length(); ++i) {
                        JSONObject jsonObject=fruitsArray.getJSONObject(i);
                        String Name=jsonObject.getString("Name");
                        String Address=jsonObject.optString("Address");
                        String PhoneNo=jsonObject.getString("PhoneNo");
                        String Email=jsonObject.getString("Email");
                        String State=jsonObject.optString("State");
                        String cities=jsonObject.optString("City");
                        String password=jsonObject.optString("password");
                        names.setText(Name);
                        address.setText(Address);
                        contact.setText(PhoneNo);
                        email.setText(Email);
                        setspinnerPos(State,state);
                        city.setVisibility(View.VISIBLE);
                        CitiesClass.addCities(context,city,State);
                        city.setSelection(getspinnerPosit(cities,CitiesClass.cities));
                        pass.setText(password);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                params.put("userid", pref.getString("user_id", ""));
                return params;
            }
        };
        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request2);
    }

    public int getspinnerPosit(String str,List<String> lists){
        int a=lists.size();
        for (int i = 0; i < a; i++) {
            if (lists.get(i).equals(str)) {
                return i;
            }
        }
        return 0;
    }

    public void setspinnerPos(String compareValue,Spinner mSpinner){
        if (compareValue != null) {
            int spinnerPosition = adpstate.getPosition(compareValue);
            mSpinner.setSelection(spinnerPosition);
        }
    }

}
