package com.example.marni.orderapp.Presentation.Adapters;

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

import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrdersListViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Order> orders;

    public OrdersListViewAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Order> orders) {

        this.context = context;
        this.layoutInflater = layoutInflater;
        this.orders = orders;
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

        final ViewHolder viewHolder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.listview_item_order, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.cardView = (CardView) convertView.findViewById(R.id.cardView);
            viewHolder.textViewDateTime = (TextView) convertView.findViewById(R.id.textViewDateTime);
            viewHolder.textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatus);
            viewHolder.textViewTotalPrice = (TextView) convertView.findViewById(R.id.textViewTotalPrice);
            viewHolder.imageView_check = (ImageView)convertView.findViewById(R.id.imageView_check);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        Order order = orders.get(position);

        if (order.getStatus() == 0) {
        }

        DecimalFormat formatter = new DecimalFormat("#0.00");

         if (order.getStatus() == 0) {
             viewHolder.textViewDateTime.setTextColor(ContextCompat.getColor(context, R.color.textprimarycolor));
             viewHolder.textViewStatus.setTextColor(ContextCompat.getColor(context,  R.color.colorPrimary));
             viewHolder.textViewTotalPrice.setTextColor(ContextCompat.getColor(context, R.color.textprimarycolor));

             viewHolder.textViewStatus.setText(context.getResources().getString(R.string.open));
             viewHolder.imageView_check.setVisibility(View.INVISIBLE);
             viewHolder.cardView.setElevation(8);
         } else {
             viewHolder.textViewDateTime.setTextColor(ContextCompat.getColor(context, R.color.colorGrey));
             viewHolder.textViewStatus.setTextColor(ContextCompat.getColor(context,  R.color.colorGrey));
             viewHolder.textViewTotalPrice.setTextColor(ContextCompat.getColor(context, R.color.colorGrey));

             viewHolder.textViewStatus.setText("");
             viewHolder.imageView_check.setVisibility(View.VISIBLE);
             viewHolder.cardView.setElevation(5);
         }

        viewHolder.textViewTotalPrice.setText("€ " + formatter.format(order.getPriceTotal()));

        viewHolder.textViewDateTime.setText(order.getTimestamp());

        return convertView;
    }

    private static class ViewHolder {
        CardView cardView;

        TextView textViewStatus;
        TextView textViewDateTime;
        TextView textViewTotalPrice;

        ImageView imageView_check;
    }
}
