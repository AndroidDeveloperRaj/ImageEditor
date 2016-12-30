package com.app.imagecreator.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amapps.imagecreator.R;

public class CustomListTextAdapter extends BaseAdapter {

    private Context context;
    private String[] arrayListItems;
    private String[] arrayListItemNames;
    private int clickedChildPosition = -1;

    public CustomListTextAdapter(Context context, String[] arrayListItems, String[] arrayListItemNames) {
        this.context = context;
        this.arrayListItems = arrayListItems;
        this.arrayListItemNames = arrayListItemNames;

    }

    public void setClickedChildPosition(int newClickedChildPosition) {
        this.clickedChildPosition = newClickedChildPosition;
    }

    @Override
    public int getCount() {
        return arrayListItems.length;
    }

    @Override
    public Object getItem(int position) {
        return arrayListItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Holder holder;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_list_text, null);
            holder.txtType = (TextView) convertView
                    .findViewById(R.id.txtType);
            holder.txtTypeName = (TextView) convertView.findViewById(R.id.txtTypeName);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        if (position == 0)
            convertView.setBackgroundColor(Color.parseColor("#313431"));
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), arrayListItems[position]);
        holder.txtType.setTypeface(typeface);
        holder.txtTypeName.setText(arrayListItemNames[position]);

        if (position == clickedChildPosition)
            convertView.setBackgroundColor(Color.parseColor("#313431"));
        else if (clickedChildPosition == -1 && position == 0)
            convertView.setBackgroundColor(Color.parseColor("#313431"));
        else
            convertView.setBackgroundColor(0);

        return convertView;
    }

    private static class Holder {
        TextView txtType = null;
        TextView txtTypeName = null;
    }

}
