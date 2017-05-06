package com.example.marni.orderapp.Presentation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.R;


import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by MarcdenUil on 5-5-2017.
 */

public class ProductsListviewAdapter extends BaseAdapter {

    private static final int TYPE_PRODUCT = 0;
    private static final int TYPE_HEADER = 1;

    private LayoutInflater mInflater;

    private ArrayList<Object> list = new ArrayList<>();

    public ProductsListviewAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position) instanceof Product){
            return TYPE_PRODUCT;
        } else {
            return TYPE_HEADER;
        }
    }

    public void addItem(final Product item) {
        list.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        list.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            switch (getItemViewType(position)) {
                case TYPE_PRODUCT:
                    convertView = mInflater.inflate(R.layout.listview_item_products, null);
                    break;
                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.listview_sectionheader_products, null);
                    break;
            }

        }
        switch (getItemViewType(position)){
            case TYPE_PRODUCT:
                TextView name = (TextView)convertView.findViewById(R.id.listViewProducts_productname);
                TextView price = (TextView)convertView.findViewById(R.id.listViewProducts_productprice);
                TextView size = (TextView)convertView.findViewById(R.id.listViewProducts_productsize);
                TextView alcohol_percentage = (TextView)convertView.findViewById(R.id.listViewProducts_product_alcoholpercentage);

                name.setText(((Product)list.get(position)).getName());
                price.setText("€ " + ((Product)list.get(position)).getPrice());
                size.setText(((Product)list.get(position)).getSize() + " ml");
                alcohol_percentage.setText(((Product)list.get(position)).getAlcohol_percentage() + "%");
                break;
            case TYPE_HEADER:
                TextView categoryname = (TextView)convertView.findViewById(R.id.listViewOrders_categoryname);

                categoryname.setText(((String)list.get(position)));
                break;
        }
        return convertView;
    }
}



