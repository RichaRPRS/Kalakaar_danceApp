package com.kalakaar.admin.danceapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomAdminAdapter extends ArrayAdapter<DataModel> {

    private ArrayList<DataModel> dataSet;
    Context mContext;
    public ImageLoader imageLoader;
    ProgressDialog dialog;
    Activity activity;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView status;
        ImageView info,delimg;
    }

    public CustomAdminAdapter(ArrayList<DataModel> data, Context context,Activity activity) {
        super(context, R.layout.layout_list2, data);
        this.dataSet = data;
        this.mContext=context;
        imageLoader = new ImageLoader(context);
        this.activity=activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final DataModel dataModel = getItem(position);
        CustomAdminAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new CustomAdminAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_list2, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.status = (TextView) convertView.findViewById(R.id.status);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);
            viewHolder.delimg=(ImageView)convertView.findViewById(R.id.imageView10);
            viewHolder.delimg.setVisibility(View.VISIBLE);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CustomAdminAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }
        viewHolder.txtName.setText(dataModel.getHeading());
        viewHolder.txtType.setText(dataModel.getDescription());
        if (dataModel.getApprove().equals("1")) {
            viewHolder.status.setText("Approved ");
            viewHolder.status.setTextColor(Color.rgb(0,128,0));
        }else if (dataModel.getApprove().equals("2")){
            viewHolder.status.setText("Not Approved ");
            viewHolder.status.setTextColor(Color.RED);
        }else {
            viewHolder.status.setText("Pending ");
            viewHolder.status.setTextColor(Color.rgb(255, 153, 0));
        }

        ImageView image = viewHolder.info;

        imageLoader.DisplayImage(dataModel.getImg_path(), image);

        viewHolder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,FullScreenViewActivity.class);
                intent.putExtra("message", dataModel.getImg_path());
                mContext.startActivity(intent);
            }
        });

        viewHolder.delimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                alertDialogBuilder.setTitle("DELETE");
                alertDialogBuilder.setMessage("Are you sure you want to delete !");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setIcon(R.drawable.ic_action_del);
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        serverData(dataModel.getEvent_Id(),position);
                    }
                });
                alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "Discarded", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialogs = alertDialogBuilder.create();
                dialogs.show();
            }
        });

        return convertView;
    }

    public void serverData(final String vid ,final int position){
        String HttpUrl="http://reichprinz.com/teaAndroid/danceapp/delpost.php";
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading....");
        dialog.show();
        StringRequest request2 = new StringRequest(Request.Method.POST ,HttpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                dialog.dismiss();
                //dataModels= new ArrayList<>();
                if (string.equals("updated Successfully")) {
                    Toast.makeText(mContext, "Vendor Deleted Successfully", Toast.LENGTH_SHORT).show();
                    dataSet.remove(position);
                    notifyDataSetChanged();
                }else {
                    Toast.makeText(mContext, "Some error occured Try again", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(mContext, "Some error occurred!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setMessage("Something went wrong Check your Connection !");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        serverData(vid,position);
                    }
                });
                alertDialogBuilder.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("event_Id", vid);
                return params;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(mContext);
        rQueue.add(request2);
    }


}
