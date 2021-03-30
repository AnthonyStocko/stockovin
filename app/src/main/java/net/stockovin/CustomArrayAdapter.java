package net.stockovin;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class CustomArrayAdapter extends ArrayAdapter
{
    List<String> gObjects;

    public CustomArrayAdapter(Context ctx, int textViewResourceID, List<String> objects)
    {
        super(ctx, android.R.layout.simple_spinner_item,  objects);
        gObjects = objects;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent){

        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spinner_time2, parent, false);

        TextView text1 = (TextView)layout.findViewById(R.id.tvCountry);

        text1.setText(gObjects.get(position));

        return layout;
    }
    //other constructors

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        // View view = super.getView(position, convertView, parent);

        return getCustomView (position, convertView, parent);


    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView (position, convertView, parent);
    }
}
