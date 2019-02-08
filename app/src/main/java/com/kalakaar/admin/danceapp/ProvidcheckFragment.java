package com.kalakaar.admin.danceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProvidcheckFragment extends DialogFragment {

    Spinner spincat;

    String url="http://reichprinz.com/teaAndroid/danceapp/fetchcategories.php";
    String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/reguserfromprov2.php";

    Context context;
    ArrayList<HashMap> mylist;
    List<String> list,list2,listid,listnames,listsetid;
    LinearLayout linearLayout;
    ProgressBar progressBars;
    TextView textView;

    private static final String ARG_PARAM1 = "param1";
    private String userid;

    public ProvidcheckFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProvidcheckFragment newInstance(String param1) {
        ProvidcheckFragment fragment = new ProvidcheckFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userid = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_providcheck, container, false);
        context=getContext();

        spincat=(Spinner) view.findViewById(R.id.spinnercateg);
        //spinsubcat=(Spinner) view.findViewById(R.id.spinnersubcat);

        Button register = (Button) view.findViewById(R.id.button);
        Button cancel = (Button) view.findViewById(R.id.button2);
        listnames = new ArrayList<String>();
        listsetid = new ArrayList<String>();
        linearLayout = view.findViewById(R.id.rootcontainer);
        textView=(TextView)view.findViewById(R.id.textView26);
        progressBars=(ProgressBar)view.findViewById(R.id.progressBar);
        progressBars.setVisibility(View.VISIBLE);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spincat.getSelectedItem().toString().equals("No Category")){
                    Toast toast=Toast.makeText(context, "No Category available", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else if (spincat.getSelectedItem().toString().equals("Category")){
                    Toast toast=Toast.makeText(context, "Select Category", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else {
                    serverData();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        spinnerdata();

        spincat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                linearLayout.removeAllViews();
                list2 = new ArrayList<String>();
                listid = new ArrayList<String>();
                if (spincat.getSelectedItem().equals("No Category")){
                }else if (spincat.getSelectedItem().equals("Category")){
                }else if (spincat.getSelectedItem().toString().equals("All")){
                }else {
                    for (int i = 0; i < mylist.size(); i++) {
                        HashMap<String, String> hashmap = mylist.get(i);
                        String cat_id = hashmap.get("Category_Id");
                        String categ = hashmap.get("CategoryName");
                        String subcat = hashmap.get("subcategory");
                        if (spincat.getSelectedItem().equals(categ)) {
                            list2.add(subcat);
                            listid.add(cat_id);
                            addcheckbox(Integer.parseInt(cat_id),subcat);
                        }
                    }
                }
                if (list2.size() > 0) {

                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    public void serverData(){
        //Toast.makeText(context, listsetid.toString(), Toast.LENGTH_SHORT).show();
        progressBars.setVisibility(View.VISIBLE);
        StringRequest request2 = new StringRequest(Request.Method.POST,HttpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                progressBars.setVisibility(View.GONE);
                if (string.equals("0")){
                    Toast toast=Toast.makeText(context, "Something went wrong! Please try again", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else if (string.equals("1")){
                    Toast toast=Toast.makeText(context, "Registered sucessfully", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    dismiss();
                }
                else if (string.equals("2")){
                    Toast toast=Toast.makeText(context, "Your number is Already registered", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(context, "Your number is Already registered ", Toast.LENGTH_SHORT).show();
                }else {
                    Toast toast=Toast.makeText(context, "Something went wrong! Please try again", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(context, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Some error occurred!!", Toast.LENGTH_SHORT).show();
                progressBars.setVisibility(View.GONE);
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
                params.put("userid", userid);
                params.put("category_id", listsetid.toString());
                return params;
            }
        };
        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request2);
    }

    public void spinnerdata(){
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                progressBars.setVisibility(View.GONE);
                //Toast.makeText(context, "data "+string, Toast.LENGTH_SHORT).show();
                if (string.equals("No Result"))
                {
                    Toast toast=Toast.makeText(context, "Something went wrong try after some time", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    //Toast.makeText(context, "Something went wrong try after some time", Toast.LENGTH_SHORT).show();
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
                progressBars.setVisibility(View.GONE);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Something went wrong Check your Connection !");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spinnerdata();
                        //progressBar.setVisibility(View.VISIBLE);
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

    public void addcheckbox(int id,String name){
        // Create Checkbox Dynamically
        final CheckBox checkBox = new CheckBox(context);
        checkBox.setTag(id);
        checkBox.setText(name);
        checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //String msg = "You have " + (isChecked ? "checked" : "unchecked") + " this Check it Checkbox.";
                //Toast.makeText(context, msg+" "+checkBox.getTag(), Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    listnames.add(spincat.getSelectedItem().toString()+"-"+buttonView.getText().toString());
                    listsetid.add(checkBox.getTag().toString());
                }else {
                    listnames.remove(spincat.getSelectedItem().toString()+"-"+buttonView.getText().toString());
                    listsetid.remove(checkBox.getTag().toString());
                }
                textView.setText(listnames.toString());
            }
        });

        // Add Checkbox to LinearLayout
        if (linearLayout != null) {
            linearLayout.addView(checkBox);
        }

    }

}
