package com.example.marni.orderapp.presentation.adapters;

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

import com.example.marni.orderapp.domain.Allergy;
import com.example.marni.orderapp.domain.Product;
import com.example.marni.orderapp.presentation.activities.AllergiesActivity;
import com.example.marni.orderapp.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class OrderDetailListViewAdapter extends BaseAdapter implements
        StickyListHeadersAdapter,
        View.OnClickListener {

    private final String tag = getClass().getSimpleName();

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<Product> products;


    public OrderDetailListViewAdapter(Context context, LayoutInflater layoutInflater, List<Product> products) {
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.products = (ArrayList<Product>) products;
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

        View view = convertView;
        final ViewHolder viewHolder;

        final Product p = products.get(position);

        if (view == null) {

            Log.i(tag, "ViewHolder maken. Position: " + position);

            viewHolder = new ViewHolder();

            view = layoutInflater.inflate(R.layout.listview_item_product, null);

            viewHolder.textViewName = (TextView) view.findViewById(R.id.listViewProducts_productname);
            viewHolder.textViewPrice = (TextView) view.findViewById(R.id.listViewProducts_productprice);
            viewHolder.textViewSize = (TextView) view.findViewById(R.id.listViewProducts_productsize);
            viewHolder.textViewAlcohol = (TextView) view.findViewById(R.id.listViewProducts_product_alcoholpercentage);
            viewHolder.textViewAmount = (TextView) view.findViewById(R.id.listViewProducts_amount);
            viewHolder.imageViewRemove = (ImageView) view.findViewById(R.id.listViewProduct_remove);
            viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.iconHolder);

            view.setTag(viewHolder);
        } else {

            Log.i(tag, "ViewHolder meegekregen. Position: " + position);

            viewHolder = (ViewHolder) view.getTag();
        }

        DecimalFormat formatter = new DecimalFormat("#0.00");

        String name = p.getName();
        String price = "€" + formatter.format(p.getPrice());
        String size = p.getSize() + " ml";
        String alcohol = "";
        if(Double.compare(p.getAlcoholPercentage(), 0.0) == 0) {
            alcohol = p.getAlcoholPercentage() + "% Alc.";
        }
        String amount = Integer.toString(p.getQuantity());

        Product product = products.get(position);
        Picasso.with(context).load(product.getImagesrc()).into((ImageView) view.findViewById(R.id.imageView_productimage));

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

        return view;
    }

    private ImageView getImageView(Allergy allergy) {
        ImageView imageView = new ImageView(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(35, 35);
        lp.setMargins(5, 0, 0, 0);
        imageView.setLayoutParams(lp);

        int id = context.getResources().getIdentifier(allergy.getImageUrl(), "mipmap", context.getPackageName());
        Log.i(tag, "id: " + id);
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
        View view = convertView;
        HeaderViewHolder holder;
        if (view == null) {
            holder = new HeaderViewHolder();
            view = layoutInflater.inflate(R.layout.listview_sectionheader_products, parent, false);
            holder.textViewCategoryTitle = (TextView) view.findViewById(R.id.listViewOrders_categoryname);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }
        Product product = products.get(position);
        holder.textViewCategoryTitle.setText(product.getCategoryName());

        return view;
    }

    private class HeaderViewHolder {
        TextView textViewCategoryTitle;
    }

    @Override
    public long getHeaderId(int position) {
        return products.get(position).getCategoryId();
    }
}
