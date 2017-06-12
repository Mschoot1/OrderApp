package com.example.marni.orderapp.presentation.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marni.orderapp.domain.Order;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrdersListViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Order> orders;

    public OrdersListViewAdapter(Context context, LayoutInflater layoutInflater, List<Order> orders) {

        this.context = context;
        this.layoutInflater = layoutInflater;
        this.orders = (ArrayList<Order>) orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder viewHolder;

        if (view == null) {

            view = layoutInflater.inflate(R.layout.listview_item_order, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.cardView = (CardView) view.findViewById(R.id.cardView);
            viewHolder.textViewDateTime = (TextView) view.findViewById(R.id.textViewDateTime);
            viewHolder.textViewTotalPrice = (TextView) view.findViewById(R.id.textViewTotalPrice);
            viewHolder.imageViewCheck = (ImageView)view.findViewById(R.id.imageView_check);

            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }

        Order order = orders.get(position);

        DecimalFormat formatter = new DecimalFormat("#0.00");

        viewHolder.textViewDateTime.setTextColor(ContextCompat.getColor(context, R.color.colorGrey));
        viewHolder.textViewTotalPrice.setTextColor(ContextCompat.getColor(context, R.color.colorGrey));

        viewHolder.cardView.setElevation(5);


        viewHolder.textViewTotalPrice.setText("€ " + formatter.format(order.getPriceTotal()));

        viewHolder.textViewDateTime.setText(order.getTimestamp());

        return view;
    }

    private static class ViewHolder {
        CardView cardView;

        TextView textViewDateTime;
        TextView textViewTotalPrice;

        ImageView imageViewCheck;
    }
}
