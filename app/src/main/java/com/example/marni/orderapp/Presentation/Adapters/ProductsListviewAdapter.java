package com.example.marni.orderapp.Presentation.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.marni.orderapp.BusinessLogic.CalculateQuantity;
import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.DataAccess.Orders.OrdersTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsPutTask;
import com.example.marni.orderapp.Domain.Category;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Activities.AllergiesActivity;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by MarcdenUil on 5-5-2017.
 */

public class ProductsListviewAdapter extends BaseAdapter implements
        StickyListHeadersAdapter {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private LayoutInflater layoutInflater;
    private CalculateQuantity calculateQuantity;

    private Boolean currentOrder;
    private ArrayList<Product> products;
    private Order order;

    private TotalFromAssortment.OnTotalChanged listener;
    OnMethodAvailable listener2;

    public ProductsListviewAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Product> products, Order order, Boolean currentOrder, TotalFromAssortment.OnTotalChanged listener, OnMethodAvailable listener2) {
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.products = products;
        this.currentOrder = currentOrder;
        this.listener = listener;
        this.listener2 = listener2;
        this.order = order;
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

        final Product product = products.get(position);

        if (convertView == null) {

            Log.i(TAG, "ViewHolder maken. Position: " + position);

            viewHolder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.listview_item_product, null);

            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.listViewProducts_productname);
            viewHolder.textViewPrice = (TextView) convertView.findViewById(R.id.listViewProducts_productprice);
            viewHolder.textViewSize = (TextView) convertView.findViewById(R.id.listViewProducts_productsize);
            viewHolder.textViewAlcohol = (TextView) convertView.findViewById(R.id.listViewProducts_product_alcoholpercentage);

            viewHolder.spinnerAmount = (Spinner) convertView.findViewById(R.id.listViewProducts_spinner);

            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.iconHolder);

            convertView.setTag(viewHolder);
        } else {

            Log.i(TAG, "ViewHolder meegekregen. Position: " + position);

            viewHolder = (ViewHolder) convertView.getTag();
        }

        DecimalFormat formatter = new DecimalFormat("#0.00");

        viewHolder.textViewName.setText(product.getName());
        viewHolder.textViewPrice.setText("€ " + formatter.format(product.getPrice()));
        viewHolder.textViewSize.setText(product.getSize() + " ml");
        viewHolder.textViewAlcohol.setText(product.getAlcohol_percentage() + "% Alc.");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.product_quantity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.spinnerAmount.setAdapter(adapter);
        viewHolder.spinnerAmount.setSelection(product.getQuantity());

        viewHolder.spinnerAmount.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Integer new_quantity = Integer.parseInt(viewHolder.spinnerAmount.getSelectedItem().toString());

                calculateQuantity = new CalculateQuantity();

                Log.i(TAG, "Spinner clicked. Value: " + viewHolder.spinnerAmount.getSelectedItem().toString());

                String result = calculateQuantity.getmethod(product.getQuantity(), new_quantity);

                Log.i(TAG, "Methode: " + result);

                product.setQuantity(Integer.parseInt(viewHolder.spinnerAmount.getSelectedItem().toString()));

                if (!result.equals("")) {
                    listener2.onMethodAvailable(result, product, order);
                }

                TotalFromAssortment tfa = new TotalFromAssortment(products);

                listener.onTotalChanged(tfa.getPriceTotal());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (!currentOrder) {

            viewHolder.spinnerAmount.setEnabled(false);
        }

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AllergiesActivity.class);
                context.startActivity(intent);
            }
        });
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

    private class ViewHolder {
        TextView textViewName;
        TextView textViewPrice;
        TextView textViewSize;
        TextView textViewAlcohol;

        Spinner spinnerAmount;

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

    public void getAllergyIcons(Product product) {

        for (int i = 0; i < products.size(); i++) {

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

                allergies.add(iconId);
            }

            product.setAllergies(allergies);
            //
        }
    }

    public interface OnMethodAvailable {
        void onMethodAvailable(String method, Product product, Order order);
    }

}
