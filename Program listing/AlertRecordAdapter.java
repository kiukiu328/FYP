package com.fyp.evhelper.stream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fyp.evhelper.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AlertRecordAdapter extends ArrayAdapter<AlertRecord> {
    List<AlertRecord> setData;
    int resource;
    Context context;
    public AlertRecordAdapter(Context context, int resource, List<AlertRecord> setData){
        super(context,resource,setData);
        this.context=context;
        this.resource=resource;
        this.setData=setData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(resource,null,false);
//        TextView date= (TextView)view.findViewById(R.id.alert_item_date);
//        ImageView img_view = view.findViewById(R.id.record_alert_icon);
//        date.setText(setData.get(position).getDate());
//        Picasso.with(context).load(setData.get(position).getIconPath()).into(img_view);
        return view;
    }
}
