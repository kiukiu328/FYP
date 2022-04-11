package com.fyp.evhelper.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fyp.evhelper.R;

import java.util.ArrayList;

public class evmListAdapter extends ArrayAdapter<evmList> {
    private Context mContext;
    private int mResource;
    public evmListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<evmList> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        convertView = layoutInflater.inflate(mResource,parent,false);
        TextView txt_evListTitle = convertView.findViewById(R.id.txt_evListTitle);
        TextView txt_evListSubtitle = convertView.findViewById(R.id.txt_evListSubtitle);
        txt_evListTitle.setText(getItem(position).getEvmService());
        txt_evListSubtitle.setText(getItem(position).getEvmDate());

        return convertView;
    }

}
