package com.example.marni.orderapp.Presentation.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

import com.auth0.android.jwt.JWT;
import com.example.marni.orderapp.BusinessLogic.CalculateQuantity;
import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Activities.AllergiesActivity;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.USER;

public class ProductsListviewAdapter extends BaseAdapter implements
        StickyListHeadersAdapter {

    private final String TAG = getClass().getSimpleName();
    private final JWT jwt;
    private final int user;

    private Context context;
    private LayoutInflater layoutInflater;
    private CalculateQuantity calculateQuantity;

    private Boolean currentOrder;
    private ArrayList<Product> products;
    private Order order;

    private TotalFromAssortment.OnTotalChanged listener;
    private OnMethodAvailable listener2;

    public ProductsListviewAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Product> products, Order order, Boolean currentOrder, TotalFromAssortment.OnTotalChanged listener, OnMethodAvailable listener2, JWT jwt, int user) {
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.products = products;
        this.currentOrder = currentOrder;
        this.listener = listener;
        this.listener2 = listener2;
        this.order = order;
        this.jwt = jwt;
        this.user = user;
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
        if(product.getAlcohol_percentage()==0) {
            viewHolder.textViewAlcohol.setText("");
        } else {
            viewHolder.textViewAlcohol.setText(product.getAlcohol_percentage() + "% Alc.");
        }

        ArrayAdapter<CharSequence> adapter;
        if (!currentOrder) {
            adapter = ArrayAdapter.createFromResource(context,
                    R.array.product_quantity, R.layout.my_spinner_item_disabled);
            viewHolder.spinnerAmount.setEnabled(false);
        } else {
            adapter = ArrayAdapter.createFromResource(context,
                    R.array.product_quantity, R.layout.my_spinner_item);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        viewHolder.spinnerAmount.setAdapter(adapter);
        viewHolder.spinnerAmount.setSelection(product.getQuantity());
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
                TotalFromAssortment tfa = new TotalFromAssortment(products);
                listener.onTotalChanged(tfa.getPriceTotal(), tfa.getQuanitity());
                if (!result.equals("")) {
                    listener2.onMethodAvailable(result, product, order);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, AllergiesActivity.class);
                intent.putExtra(JWT_STR, jwt);
                intent.putExtra(USER, user);
                context.startActivity(intent);
            }
        });
        viewHolder.linearLayout.removeAllViews();

        for (Allergy allergy : product.getAllergies()) {

            ImageView imageView = new ImageView(context);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(35, 35);
            lp.setMargins(5, 0, 0, 0);

            imageView.setLayoutParams(lp);

            int id = context.getResources().getIdentifier(allergy.getImage_url(), "mipmap", context.getPackageName());
            Log.i(TAG, "id: " + id);
            imageView.setImageResource(id);

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

    public interface OnMethodAvailable {
        void onMethodAvailable(String method, Product product, Order order);
    }

}
