package com.kalakaar.admin.danceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ApplicationAdapter extends ArrayAdapter<ApplicationModel> {
    private ArrayList<ApplicationModel> dataSet;
    Context mContext;

    public ApplicationAdapter( ArrayList<ApplicationModel> dataSet, Context mContext) {
        super(mContext, R.layout.application_layout, dataSet);
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        ImageView imageView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ApplicationModel dataModel = getItem(position);
        ApplicationAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ApplicationAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.application_layout, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.imageView=(ImageView)convertView.findViewById(R.id.imagevie);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ApplicationAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }
        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtType.setText(dataModel.getHeading());

        return convertView;
    }
}
