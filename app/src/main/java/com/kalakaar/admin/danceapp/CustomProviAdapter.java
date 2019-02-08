package com.kalakaar.admin.danceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomProviAdapter extends ArrayAdapter<DataModel>{

    private ArrayList<DataModel> dataSet;
    Context mContext;
    public ImageLoader imageLoader;


    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView status;
        ImageView info,imageButton;
    }

    public CustomProviAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.layout_list2, data);
        this.dataSet = data;
        this.mContext=context;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final DataModel dataModel = getItem(position);
        CustomProviAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new CustomProviAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.layout_list2, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.status = (TextView) convertView.findViewById(R.id.status);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);
            viewHolder.imageButton=(ImageView) convertView.findViewById(R.id.imagevie);
            viewHolder.imageButton.setVisibility(View.VISIBLE);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CustomProviAdapter.ViewHolder) convertView.getTag();
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

        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar snackbar ;
                if (dataModel.getReason().equals("")){
                    Toast.makeText(mContext, "No Reason : ", Toast.LENGTH_SHORT).show();
                    //snackbar = Snackbar.make(result, "No Reason : " , Snackbar.LENGTH_LONG);
                }else {
                    Toast.makeText(mContext, dataModel.getReason(), Toast.LENGTH_SHORT).show();
                    //snackbar = Snackbar.make(result, "Reason : " + dataModel.getReason(), Snackbar.LENGTH_LONG);
                }
                //snackbar.show();
            }
        });
        return convertView;
    }
}
