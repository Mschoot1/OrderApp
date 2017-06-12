package com.example.marni.orderapp.presentation.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marni.orderapp.businesslogic.TotalFromAssortment;
import com.example.marni.orderapp.dataaccess.orders.OrdersPutTask;
import com.example.marni.orderapp.dataaccess.product.ProductsDeleteTask;
import com.example.marni.orderapp.dataaccess.product.ProductsGetTask;
import com.example.marni.orderapp.dataaccess.product.ProductsPutTask;
import com.example.marni.orderapp.domain.Allergy;
import com.example.marni.orderapp.domain.Order;
import com.example.marni.orderapp.domain.Product;
import com.example.marni.orderapp.presentation.activities.MyOrderActivity;
import com.example.marni.orderapp.presentation.fragments.CategoryFragment;
import com.example.marni.orderapp.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import static com.example.marni.orderapp.presentation.activities.LoginActivity.JWT_STR;
import static com.example.marni.orderapp.presentation.activities.LoginActivity.USER;

public class MyOrderListViewAdapter extends BaseAdapter implements
        StickyListHeadersAdapter,
        ProductsDeleteTask.SuccessListener,
        ProductsPutTask.SuccessListener,
        OrdersPutTask.PutSuccessListener {

    private final String tag = getClass().getSimpleName();

    private final String jwt;
    private final int user;

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<Product> products;
    private Order order;

    private TotalFromAssortment.OnTotalChanged otc;
    private ProductsGetTask.OnEmptyList oel;
    private Activity activity;

    public MyOrderListViewAdapter(Activity activity, Context context, LayoutInflater layoutInflater, List<Product> products, Order order, MyOrderActivity listener) {
        this.activity = activity;
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.products = (ArrayList<Product>) products;
        this.order = order;
        this.otc = listener;
        this.oel = listener;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.jwt = prefs.getString(JWT_STR, "");
        this.user = prefs.getInt(USER, 0);
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

        if (p.getQuantity() == 0) {
            viewHolder.imageViewRemove.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.imageViewRemove.setVisibility(View.VISIBLE);
        }

        viewHolder.imageViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (p.getQuantity() == 1) {
                    p.setQuantity(decrease(p.getQuantity()));
                    viewHolder.imageViewRemove.setVisibility(View.INVISIBLE);
                    products.remove(position);
                    notifyDataSetChanged();
                    deleteProduct("https://mysql-test-p4.herokuapp.com/product/quantity/delete", p);
                    if(products.isEmpty()) {
                        oel.isEmpty(true);
                    }
                } else {
                    p.setQuantity(decrease(p.getQuantity()));
                    putProduct("https://mysql-test-p4.herokuapp.com/product/quantity/edit", p);
                    String amount = Integer.toString(p.getQuantity());
                    viewHolder.textViewAmount.setText(amount);
                }

                otc.onTotalChanged(TotalFromAssortment.getPriceTotal(products), TotalFromAssortment.getQuanitity(products));
                putOrderPrice("https://mysql-test-p4.herokuapp.com/order/price/edit", TotalFromAssortment.getPriceTotal(products));
            }
        });

        viewHolder.linearLayout.removeAllViews();
        for (Allergy allergy : p.getAllergies()) {
            viewHolder.linearLayout.addView(getImageView(allergy));
        }

        return view;
    }

    private void putOrderPrice(String apiUrl, double priceTotal) {
        String[] urls = new String[]{apiUrl, jwt, Double.toString(priceTotal), Integer.toString(order.getOrderId())};
        OrdersPutTask task = new OrdersPutTask(this);
        task.execute(urls);
    }

    private void deleteProduct(String apiUrl, Product p) {
        String[] urls = new String[]{apiUrl, jwt, Integer.toString(order.getOrderId()), Integer.toString(p.getProductId()), Integer.toString(user)};
        ProductsDeleteTask task = new ProductsDeleteTask(this);
        task.execute(urls);
    }

    private void putProduct(String apiUrl, Product p) {
        String[] urls = new String[]{apiUrl, jwt, Integer.toString(order.getOrderId()), Integer.toString(p.getProductId()), Integer.toString(user), Integer.toString(p.getQuantity())};
        ProductsPutTask task = new ProductsPutTask(this);
        task.execute(urls);
    }

    private int decrease(int quantity) {
        return quantity - 1;
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
    public void successfulDeleted(Boolean successful) {
        if (successful) {
            Log.i(tag, "Product removed");
        } else {
            Toast.makeText(context, "Product couldn't be removed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void successful(Boolean successful) {
        if (successful) {
            Log.i(tag, "Product amount changed");
        } else {
            Toast.makeText(context, "Product amount couldn't be changed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void putSuccessful(Boolean successful) {
        if (successful) {
            Log.i(tag, "Totalprice succesfully edited");
        } else {
            Log.i(tag, "Error while updating totalprice");
        }
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
            holder.imageView = (ImageView) view.findViewById(R.id.imageView_filter);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }
        Product product = products.get(position);
        holder.textViewCategoryTitle.setText(product.getCategoryName());
        holder.imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        return view;
    }

    private class HeaderViewHolder {
        TextView textViewCategoryTitle;
        ImageView imageView;
    }

    @Override
    public long getHeaderId(int position) {
        return products.get(position).getCategoryId();
    }

    private void showEditDialog() {
        FragmentManager fm = activity.getFragmentManager();
        CategoryFragment alertDialog = CategoryFragment.newInstance(jwt);
        alertDialog.show(fm, "fragment_alert");
    }
}
