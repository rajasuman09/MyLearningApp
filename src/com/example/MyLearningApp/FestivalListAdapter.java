package com.example.MyLearningApp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Raja.Chirala on 28/02/2016.
 */
public class FestivalListAdapter extends BaseAdapter
{
    private static ArrayList<FestivalDetails> searchArrayList;
    private LayoutInflater mInflater;
    private ArrayList<FestivalDetails> array_list;

    public FestivalListAdapter(Context context,ArrayList<FestivalDetails> results)
    {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
        this.array_list = new ArrayList<FestivalDetails>();
        this.array_list.addAll(searchArrayList);

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
            holder.txtSerialNum = (TextView) convertView.findViewById(R.id.serial_no);
            holder.txtEventDate = (TextView) convertView.findViewById(R.id.date);
            holder.txtEventName = (TextView) convertView.findViewById(R.id.event);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtSerialNum.setText(searchArrayList.get(position).getSerialNum());
        holder.txtEventDate.setText(searchArrayList.get(position).getEventDate());
        holder.txtEventName.setText(searchArrayList.get(position).getEventName());

        return convertView;
    }

    static class ViewHolder {
        TextView txtSerialNum;
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

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        searchArrayList.clear();
        if (charText.length() == 0) {
            searchArrayList.addAll(array_list);
        }
        else
        {
            for (FestivalDetails wp : array_list)
            {
                if (wp.getEventName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    searchArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filter(int year) {

        searchArrayList.clear();
        searchArrayList.addAll(array_list);
       // notifyDataSetChanged();

    }



    }
