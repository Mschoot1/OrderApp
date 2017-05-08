package com.example.marni.orderapp.Presentation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by marni on 8-5-2017.
 */

public class OrdersListviewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Order> orders;

    public OrdersListviewAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Order> orders) {

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.listview_item_order, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewDateTime = (TextView) convertView.findViewById(R.id.textViewDateTime);
            viewHolder.textViewOrderId = (TextView) convertView.findViewById(R.id.textViewOrderId);
            viewHolder.textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatus);
            viewHolder.textViewTotalPrice = (TextView) convertView.findViewById(R.id.textViewTotalPrice);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        Order order = orders.get(position);

        DecimalFormat formatter = new DecimalFormat("#0.00");

        viewHolder.textViewDateTime.setText(order.getDateTime());
        viewHolder.textViewOrderId.setText(order.getOrderId() + "");
        viewHolder.textViewStatus.setText(order.getStatus());
        viewHolder.textViewTotalPrice.setText("$" + formatter.format(order.getTotalPrice()));

        return convertView;
    }

    private static class ViewHolder {

        TextView textViewOrderId;
        TextView textViewStatus;
        TextView textViewDateTime;
        TextView textViewTotalPrice;
    }
}


