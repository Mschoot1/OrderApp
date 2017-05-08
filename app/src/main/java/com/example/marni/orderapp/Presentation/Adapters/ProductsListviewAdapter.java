package com.example.marni.orderapp.Presentation.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by MarcdenUil on 5-5-2017.
 */

public class ProductsListviewAdapter extends BaseAdapter implements
        StickyListHeadersAdapter,
        SectionIndexer {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<Product> products;
    private int[] sectionIndices;
    private String[] sectionTitles;

    private ArrayList<ArrayList<Product>> allProducts = new ArrayList<>();

    private TotalFromAssortment.OnTotalChanged listener;

    private SparseIntArray sparseIntArray = new SparseIntArray();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ProductsListviewAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Product> products, TotalFromAssortment.OnTotalChanged listener) {

        this.context = context;
        this.layoutInflater = layoutInflater;
        this.products = products;
        sectionIndices = getSectionIndices();
        sectionTitles = getSectionTitles();

        this.listener = listener;

        for (int i = 0; i < products.size(); i++) {

            //
            allProducts.add(new ArrayList<Product>());

            // random icon generation
            ArrayList<Integer> allergies = new ArrayList<>();

            int iconCount = (int) (Math.random() * 3 + 0);

            for (int j = 0; j < iconCount; j++) {

                int iconId = getRandomIconId();

                for (int k = 0; k < allergies.size(); k++) {

                    if (allergies.get(k) == iconId) {

                        iconId = getRandomIconId();
                    }
                }

                Log.i(TAG, iconId + "");

                allergies.add(iconId);
            }

            products.get(i).setAllergies(allergies);
            //
        }

        Log.i(TAG, allProducts.size() + "");
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

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {

            Log.i(TAG, "Geen viewHolder meegekregen. Nieuwe maken. " + position);

            convertView = layoutInflater.inflate(R.layout.new_listview_item_product, null);

            viewHolder = new ViewHolder();

            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.listViewProducts_productname);
            viewHolder.textViewPrice = (TextView) convertView.findViewById(R.id.listViewProducts_productprice);
            viewHolder.textViewSize = (TextView) convertView.findViewById(R.id.listViewProducts_productsize);
            viewHolder.textViewAlcohol = (TextView) convertView.findViewById(R.id.listViewProducts_product_alcoholpercentage);

            viewHolder.spinnerAmount = (Spinner) convertView.findViewById(R.id.listViewProducts_spinner);

            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.iconHolder);

            convertView.setTag(viewHolder);
        } else {

            Log.i(TAG, "ViewHolder hergebruiken. " + position);

            viewHolder = (ViewHolder) convertView.getTag();
        }

        DecimalFormat formatter = new DecimalFormat("#0.00");
        double d = 4.0;

        final Product product = products.get(position);

        viewHolder.textViewName.setText(product.getName());
        viewHolder.textViewPrice.setText("€ " + formatter.format(product.getPrice()));
        viewHolder.textViewSize.setText(product.getSize() + " ml");
        viewHolder.textViewAlcohol.setText(product.getAlcohol_percentage() + "% Alc.");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.product_quantity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.spinnerAmount.setAdapter(adapter);

        viewHolder.spinnerAmount.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position2, long id) {

                Log.i(TAG, "Spinner value: " + Integer.parseInt(viewHolder.spinnerAmount.getSelectedItem().toString()));

                sparseIntArray.put(position, Integer.parseInt(viewHolder.spinnerAmount.getSelectedItem().toString()));

                ArrayList<Product> specificProducts = new ArrayList<>();

                int spinnerValue = Integer.parseInt(viewHolder.spinnerAmount.getSelectedItem().toString());

                for (int i = 0; i < spinnerValue; i++) {

                    specificProducts.add(product);
                }

                Log.i(TAG, specificProducts.size() + "");

                allProducts.set(position, specificProducts);

                TotalFromAssortment tfa = new TotalFromAssortment(allProducts);

                listener.onTotalChanged(tfa.getPriceTotal());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Log.i(TAG, "Size = " + sparseIntArray.size());

        //loop a sparseIntArray
        for (int i = 0; i < sparseIntArray.size(); i++) {

            if (position == sparseIntArray.keyAt(i)) {

                viewHolder.spinnerAmount.setSelection(sparseIntArray.valueAt(i));
            }
        }

        viewHolder.linearLayout.removeAllViews();

        for (Object iconId : product.getAllergies()) {

            ImageView imageView = new ImageView(context);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(63, 63);

            imageView.setLayoutParams(lp);
            imageView.setImageResource((int) iconId);

            viewHolder.linearLayout.addView(imageView);
        }

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<>();
        int categoryId = products.get(0).getCategoryId();
        sectionIndices.add(0);
        for (int i = 1; i < products.size(); i++) {
            if (products.get(i).getCategoryId() != categoryId) {
                categoryId = products.get(i).getCategoryId();
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

        Spinner spinnerAmount;

        LinearLayout linearLayout;
    }

    private class HeaderViewHolder {
        TextView textViewCategoryTitle;
    }

    private int getRandomIconId() {

        int i = (int) (Math.random() * 13 + 1);

        switch (i) {

            case 1:
                return R.mipmap.celery_icon;
            case 2:
                return R.mipmap.cereals_containing_gluten_icon;
            case 3:
                return R.mipmap.crustaceans_icon;
            case 4:
                return R.mipmap.eggs_icon;
            case 5:
                return R.mipmap.fish_icon;
            case 6:
                return R.mipmap.milk_icon;
            case 7:
                return R.mipmap.lupin_icon;
            case 8:
                return R.mipmap.molluscs_icon;
            case 9:
                return R.mipmap.mustard_icon;
            case 10:
                return R.mipmap.nuts_icon;
            case 11:
                return R.mipmap.peanuts_icon;
            case 12:
                return R.mipmap.soya_icon;
            case 13:
                return R.mipmap.sulphur_dioxide_icon;
            default:
                return 0;
        }
    }
}



