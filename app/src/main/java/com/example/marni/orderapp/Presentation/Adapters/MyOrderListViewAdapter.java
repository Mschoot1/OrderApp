package com.example.marni.orderapp.Presentation.Adapters;

import android.app.Activity;
import android.app.FragmentManager;
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
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.DataAccess.Orders.OrdersPutTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsDeleteTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsGetTask;
import com.example.marni.orderapp.DataAccess.Product.ProductsPutTask;
import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Activities.AllergiesActivity;
import com.example.marni.orderapp.Presentation.Activities.MyOrderActivity;
import com.example.marni.orderapp.Presentation.Fragments.CategoryFragment;
import com.example.marni.orderapp.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.USER;

public class MyOrderListViewAdapter extends BaseAdapter implements
        StickyListHeadersAdapter,
        View.OnClickListener,
        ProductsDeleteTask.SuccessListener,
        ProductsPutTask.SuccessListener,
        OrdersPutTask.PutSuccessListener {

    private final String TAG = getClass().getSimpleName();
    private final String jwt;
    private final int user;

    private Context context;
    private LayoutInflater layoutInflater;

    private ArrayList<Product> products;
    private Order order;

    private TotalFromAssortment.OnTotalChanged otc;
    private ProductsGetTask.OnEmptyList oel;
    private Activity activity;

    public MyOrderListViewAdapter(Activity activity, LayoutInflater layoutInflater, ArrayList<Product> products, Order order, String jwt, int user, MyOrderActivity listener) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.layoutInflater = layoutInflater;
        this.products = products;
        this.order = order;
        this.jwt = jwt;
        this.user = user;
        this.otc = listener;
        this.oel = listener;
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
                    if(products.size() == 0) {
                        oel.isEmpty(true);
                    }
                } else {
                    p.setQuantity(decrease(p.getQuantity()));
                    putProduct("https://mysql-test-p4.herokuapp.com/product/quantity/edit", p);
                    String amount = p.getQuantity() + "";
                    viewHolder.textViewAmount.setText(amount);
                }

                otc.onTotalChanged(TotalFromAssortment.getPriceTotal(products), TotalFromAssortment.getQuanitity(products));
                putOrderPrice("https://mysql-test-p4.herokuapp.com/order/price/edit", TotalFromAssortment.getPriceTotal(products));
            }
        });

        viewHolder.linearLayout.setOnClickListener(this);
        viewHolder.linearLayout.removeAllViews();
        for (Allergy allergy : p.getAllergies()) {
            viewHolder.linearLayout.addView(getImageView(allergy));
        }

        return convertView;
    }

    private void putOrderPrice(String apiUrl, double priceTotal) {
        String[] urls = new String[]{apiUrl, jwt, priceTotal + "", Integer.toString(order.getOrderId())};
        OrdersPutTask task = new OrdersPutTask(this);
        task.execute(urls);
    }

    private void deleteProduct(String apiUrl, Product p) {
        String[] urls = new String[]{apiUrl, jwt, Integer.toString(order.getOrderId()), p.getProductId() + "", user + ""};
        ProductsDeleteTask task = new ProductsDeleteTask(this);
        task.execute(urls);
    }

    private void putProduct(String apiUrl, Product p) {
        String[] urls = new String[]{apiUrl, jwt, Integer.toString(order.getOrderId()), p.getProductId() + "", user + "", p.getQuantity() + ""};
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

    @Override
    public void successfulDeleted(Boolean successful) {
        if (successful) {
            Log.i(TAG, "Product removed");
        } else {
            Toast.makeText(context, "Product couldn't be removed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void successful(Boolean successful) {
        if (successful) {
            Log.i(TAG, "Product amount changed");
        } else {
            Toast.makeText(context, "Product amount couldn't be changed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void putSuccessful(Boolean successful) {
        if (successful) {
            Log.i(TAG, "Totalprice succesfully edited");
        } else {
            Log.i(TAG, "Error while updating totalprice");
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
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = layoutInflater.inflate(R.layout.listview_sectionheader_products, parent, false);
            holder.textViewCategoryTitle = (TextView) convertView.findViewById(R.id.listViewOrders_categoryname);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView_filter);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        Product product = products.get(position);
        holder.textViewCategoryTitle.setText(product.getCategoryName());
        holder.imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        return convertView;
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
