package com.example.marni.orderapp.Presentation.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Activities.AllergiesActivity;
import com.example.marni.orderapp.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class OrderDetailListViewAdapter extends BaseAdapter implements
        StickyListHeadersAdapter,
        View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<Product> products;


    public OrderDetailListViewAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Product> products) {
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        final Product p = products.get(position);

        if (convertView == null) {

            Log.i(TAG, "ViewHolder maken. Position: " + position);

            viewHolder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.listview_item_product, null);

            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.listViewProducts_productname);
            viewHolder.textViewPrice = (TextView) convertView.findViewById(R.id.listViewProducts_productprice);
            viewHolder.textViewSize = (TextView) convertView.findViewById(R.id.listViewProducts_productsize);
            viewHolder.textViewAlcohol = (TextView) convertView.findViewById(R.id.listViewProducts_product_alcoholpercentage);
            viewHolder.textViewAmount = (TextView) convertView.findViewById(R.id.listViewProducts_amount);
            viewHolder.imageViewRemove = (ImageView) convertView.findViewById(R.id.listViewProduct_remove);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.iconHolder);

            convertView.setTag(viewHolder);
        } else {

            Log.i(TAG, "ViewHolder meegekregen. Position: " + position);

            viewHolder = (ViewHolder) convertView.getTag();
        }

        DecimalFormat formatter = new DecimalFormat("#0.00");

        String name = p.getName();
        String price = "€" + formatter.format(p.getPrice());
        String size = p.getSize() + " ml";
        String alcohol = "";
        if (p.getAlcohol_percentage() != 0) {
            alcohol = p.getAlcohol_percentage() + "% Alc.";
        }
        String amount = p.getQuantity() + "";

        Product product = products.get(position);
        Picasso.with(context).load(product.getImagesrc()).into((ImageView) convertView.findViewById(R.id.imageView_productimage));

        viewHolder.textViewName.setText(name);
        viewHolder.textViewPrice.setText(price);
        viewHolder.textViewSize.setText(size);
        viewHolder.textViewAlcohol.setText(alcohol);
        viewHolder.textViewAmount.setText(amount);
        viewHolder.imageViewRemove.setVisibility(View.INVISIBLE);

        viewHolder.linearLayout.setOnClickListener(this);
        viewHolder.linearLayout.removeAllViews();
        for (Allergy allergy : p.getAllergies()) {
            viewHolder.linearLayout.addView(getImageView(allergy));
        }

        return convertView;
    }

    private ImageView getImageView(Allergy allergy) {
        ImageView imageView = new ImageView(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(35, 35);
        lp.setMargins(5, 0, 0, 0);
        imageView.setLayoutParams(lp);

        int id = context.getResources().getIdentifier(allergy.getImage_url(), "mipmap", context.getPackageName());
        Log.i(TAG, "id: " + id);
        imageView.setImageResource(id);

        return imageView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, AllergiesActivity.class);
        context.startActivity(intent);
    }

    private class ViewHolder {
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewSize;
        TextView textViewAlcohol;
        TextView textViewAmount;

        ImageView imageViewRemove;

        LinearLayout linearLayout;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = layoutInflater.inflate(R.layout.listview_sectionheader_products, parent, false);
            holder.textViewCategoryTitle = (TextView) convertView.findViewById(R.id.listViewOrders_categoryname);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        Product product = products.get(position);
        holder.textViewCategoryTitle.setText(product.getCategoryName());

        return convertView;
    }

    private class HeaderViewHolder {
        TextView textViewCategoryTitle;
    }

    @Override
    public long getHeaderId(int position) {
        return products.get(position).getCategoryId();
    }
}
