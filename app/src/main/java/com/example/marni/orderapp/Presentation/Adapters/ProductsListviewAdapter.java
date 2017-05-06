package com.example.marni.orderapp.Presentation.Adapters;

import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.R;

import java.util.ArrayList;
import java.util.Objects;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by MarcdenUil on 5-5-2017.
 */

public class ProductsListviewAdapter extends BaseAdapter implements
        StickyListHeadersAdapter,
        SectionIndexer {

    private LayoutInflater layoutInflater;

    private ArrayList<Product> products;
    private int[] sectionIndices;
    private String[] sectionTitles;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ProductsListviewAdapter(LayoutInflater layoutInflater, ArrayList<Product> products) {

        this.layoutInflater = layoutInflater;
        this.products = products;
        sectionIndices = getSectionIndices();
        sectionTitles = getSectionTitles();
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

    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.listview_item_products, null);

            viewHolder = new ViewHolder();

            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.listViewProducts_productname);
            viewHolder.textViewPrice = (TextView) convertView.findViewById(R.id.listViewProducts_productprice);
            viewHolder.textViewSize = (TextView) convertView.findViewById(R.id.listViewProducts_productsize);
            viewHolder.textViewAlcohol = (TextView) convertView.findViewById(R.id.listViewProducts_product_alcoholpercentage);


            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);

        viewHolder.textViewName.setText(product.getName());
        viewHolder.textViewPrice.setText("€ " + product.getPrice());
        viewHolder.textViewSize.setText(product.getSize() + " ml");
        viewHolder.textViewAlcohol.setText(product.getAlcohol_percentage() + "%");

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<>();
        String category = products.get(0).getCategory();
        sectionIndices.add(0);
        for (int i = 1; i < products.size(); i++) {
            if (!Objects.equals(products.get(i).getCategory(), category)) {
                category = products.get(i).getCategory();
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    private String[] getSectionTitles() {
        String[] titles = new String[sectionIndices.length];
        for (int i = 0; i < sectionIndices.length; i++) {
            titles[i] = products.get(sectionIndices[i]).getCategory();
        }
        return titles;
    }

    @Override
    public Object[] getSections() {
        return sectionTitles;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (sectionIndices.length == 0) {
            return 0;
        }

        if (sectionIndex >= sectionIndices.length) {
            sectionIndex = sectionIndices.length - 1;
        } else if (sectionIndex < 0) {
            sectionIndex = 0;
        }
        return sectionIndices[sectionIndex];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < sectionIndices.length; i++) {
            if (position < sectionIndices[i]) {
                return i - 1;
            }
        }
        return sectionIndices.length - 1;
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

        String headerTitle = products.get(position).getCategory();
        holder.textViewCategoryTitle.setText(headerTitle);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return products.get(position).getCategory().charAt(0);
    }

    private static class ViewHolder {
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewSize;
        TextView textViewAlcohol;
    }

    private class HeaderViewHolder {
        TextView textViewCategoryTitle;
    }
}



