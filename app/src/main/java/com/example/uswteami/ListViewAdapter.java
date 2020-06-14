package com.example.uswteami;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.uswteami.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private TextView name;
    private TextView num;
    private TextView addr;

    private ArrayList<ListViewItem> listViewItemList = new ArrayList<>();

    public ListViewAdapter(){
    }


    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }

        name = (TextView)convertView.findViewById(R.id.name);
        num = (TextView)convertView.findViewById(R.id.num);
        addr = (TextView)convertView.findViewById(R.id.address);

        ListViewItem listViewItem = listViewItemList.get(position);

        num.setText(listViewItem.getT1());
        name.setText(listViewItem.getT2());
        addr.setText(listViewItem.getT3());


        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getName(int position){
        return listViewItemList.get(position).getT2();
    }

    public void addItem(String t1, String t2, String t3){
        ListViewItem item = new ListViewItem();

        item.setT1(t1);
        item.setT2(t2);
        item.setT3(t3);

        listViewItemList.add(item);
    }

}
