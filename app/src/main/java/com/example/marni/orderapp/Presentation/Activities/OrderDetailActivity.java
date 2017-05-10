package com.example.marni.orderapp.Presentation.Activities;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.example.marni.orderapp.BusinessLogic.TotalFromAssortment;
import com.example.marni.orderapp.DataAccess.CategoriesTask;
import com.example.marni.orderapp.DataAccess.ProductsTask;
import com.example.marni.orderapp.Domain.Category;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Domain.Product;
import com.example.marni.orderapp.Presentation.Adapters.ProductsListviewAdapter;
import com.example.marni.orderapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.example.marni.orderapp.Presentation.Activities.OrderHistoryActivity.ORDER;

public class OrderDetailActivity extends AppCompatActivity implements
        TotalFromAssortment.OnTotalChanged,
        ProductsTask.OnProductAvailable, CategoriesTask.OnCategoryAvailable {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();
    private ProductsListviewAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();

        Order order = (Order) bundle.get(ORDER);

        TextView textViewOrderId = (TextView) findViewById(R.id.textViewOrderId);
        TextView textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        TextView textViewDateTime = (TextView) findViewById(R.id.textViewDateTime);

        assert order != null;
        textViewOrderId.setText(order.getOrderId() + "");
        textViewStatus.setText(order.getStatus());
        textViewDateTime.setText(order.getDateTime());

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StickyListHeadersListView stickyList = (StickyListHeadersListView) findViewById(R.id.listViewProducts);
        stickyList.setAreHeadersSticky(true);
        stickyList.setFastScrollEnabled(true);
        stickyList.setFastScrollAlwaysVisible(true);

        mAdapter = new ProductsListviewAdapter(getApplicationContext(), getLayoutInflater(), products, categories, this);

        stickyList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        getCategories("");
        getProducts("");
    }

    public void getCategories(String ApiUrl) {

        CategoriesTask task = new CategoriesTask(this);
        String[] urls = new String[]{ApiUrl};
        task.execute(urls);
    }

    public void getProducts(String ApiUrl) {

        ProductsTask task = new ProductsTask(this);
        String[] urls = new String[]{ApiUrl};
        task.execute(urls);
    }

    @Override
    public void onTotalChanged(Double priceTotal) {

        DecimalFormat formatter = new DecimalFormat("#0.00");

        TextView textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        textViewTotal.setText("Total: € " + formatter.format(priceTotal));
    }

    @Override
    public void onCategoryAvailable(Category category) {

        categories.add(category);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProductAvailable(Product product) {

        products.add(product);
        mAdapter.notifyDataSetChanged();
    }
}
