package com.kalakaar.admin.danceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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


public class ProfileCategoryFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Spinner category;
    Context context;
    List<String> list,list2,listid,listnames,listsetid,templist;
    TextView textView;
    LinearLayout linearLayout;
    Button button,btncancel;
    ProgressBar progressBars;
    String url="http://reichprinz.com/teaAndroid/danceapp/fetchcategories2.php";
    String HttpUrl2="http://reichprinz.com/teaAndroid/danceapp/updateusercategory.php";
    ArrayList<HashMap> mylist;


    public ProfileCategoryFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ProfileCategoryFragment newInstance() {
        ProfileCategoryFragment fragment = new ProfileCategoryFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile_category, container, false);

        context=getActivity();
        category=(Spinner)view.findViewById(R.id.spinnercat);
        textView=(TextView)view.findViewById(R.id.textView26);
        linearLayout = view.findViewById(R.id.rootcontainer);
        button=view.findViewById(R.id.button6);
        btncancel=view.findViewById(R.id.button7);
        progressBars=(ProgressBar)view.findViewById(R.id.progressBar);
        progressBars.setVisibility(View.VISIBLE);
        listnames = new ArrayList<String>();
        listsetid = new ArrayList<String>();
        templist=new ArrayList<String>();

        datafromServer();

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                linearLayout.removeAllViews();
                list2 = new ArrayList<String>();
                listid = new ArrayList<String>();
                if (category.getSelectedItem().equals("No Category")){
                }else if (category.getSelectedItem().equals("Category")){
                }else if (category.getSelectedItem().toString().equals("All")){
                }else {
                    for (int i = 0; i < mylist.size(); i++) {
                        HashMap<String, String> hashmap = mylist.get(i);
                        String cat_id = hashmap.get("Category_Id");
                        String categ = hashmap.get("CategoryName");
                        String subcat = hashmap.get("subcategory");
                        if (category.getSelectedItem().equals(categ)) {
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, listnames+" "+listsetid, Toast.LENGTH_SHORT).show();
                if (templist.equals(listsetid)){
                    Toast.makeText(context, "You dont change anything", Toast.LENGTH_SHORT).show();
                }else if (category.getSelectedItem().toString().equals("All")){
                    listnames.clear();
                    listsetid.clear();
                    HashMap<String, String> hashmap = mylist.get((category.getSelectedItemPosition()-1));
                    listsetid.add(hashmap.get("Category_Id"));
                    listnames.add(hashmap.get("CategoryName"));
                    //textView.setText("All Categories selected");
                    updateCategory();
                }else {
                    updateCategory();
                }
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
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
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                progressBars.setVisibility(View.INVISIBLE);
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
                            if (jsonObject.isNull("Stud_Id")){

                            }else {
                                listnames.add(categoryn+"-"+subcate);
                                listsetid.add(id);
                                templist.add(id);
                            }
                            if (!list.contains(categoryn)) {
                                list.add(categoryn);
                            }
                            hashMap.put("Category_Id", id);
                            hashMap.put("CategoryName", categoryn);
                            hashMap.put("subcategory", subcate);
                            mylist.add(hashMap);
                        }
                        textView.setText(listnames.toString());
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
                progressBars.setVisibility(View.INVISIBLE);

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
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", DataFields.user_id);
                return params;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
    }

    public void addcheckbox(int id,String name){
        // Create Checkbox Dynamically
        final CheckBox checkBox = new CheckBox(context);
        checkBox.setTag(id);
        checkBox.setText(name);
        if (listsetid.contains(String.valueOf(id))){
            //checkBox.setSelected(true);
            checkBox.setChecked(true);
        }
        checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //String msg = "You have " + (isChecked ? "checked" : "unchecked") + " this Check it Checkbox.";
                //Toast.makeText(context, msg+" "+checkBox.getTag(), Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    listnames.add(category.getSelectedItem().toString()+"-"+buttonView.getText().toString());
                    listsetid.add(checkBox.getTag().toString());
                }else {
                    listnames.remove(category.getSelectedItem().toString()+"-"+buttonView.getText().toString());
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

    public void updateCategory(){

        progressBars.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        progressBars.setVisibility(View.INVISIBLE);
                        if (ServerResponse.equals("1")) {
                            dismiss();
                            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "Some error Occured ! Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressBars.setVisibility(View.INVISIBLE);
                        // Showing error message if something goes wrong.volleyError.toString()
                        Toast.makeText(context, "Server error please try again ", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", DataFields.user_id);
                params.put("category_id", listsetid.toString());
                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}