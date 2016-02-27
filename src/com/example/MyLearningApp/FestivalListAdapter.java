package com.example.MyLearningApp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Raja.Chirala on 28/02/2016.
 */
public class FestivalListAdapter extends BaseAdapter
{
    private static ArrayList<FestivalDetails> searchArrayList;
    private LayoutInflater mInflater;
    private Context mContext;
    Cursor cursor;

    public FestivalListAdapter(Context context,ArrayList<FestivalDetails> results)
    {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);

    }

    public int getCount()
    {
        // return the number of records in cursor
        return searchArrayList.size();
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.festival_each_item, null);
            holder = new ViewHolder();
            holder.txtEventDate = (TextView) convertView.findViewById(R.id.date);

            holder.txtEventName = (TextView) convertView.findViewById(R.id.event);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtEventDate.setText(searchArrayList.get(position).getEventDate());
        holder.txtEventName.setText(searchArrayList.get(position).getEventName());

        return convertView;
    }

    static class ViewHolder {
        TextView txtEventDate;
        TextView txtEventName;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
