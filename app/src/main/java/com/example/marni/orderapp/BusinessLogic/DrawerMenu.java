package com.example.marni.orderapp.BusinessLogic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.marni.orderapp.DataAccess.Orders.OrdersGetTask;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Presentation.Activities.AllergiesActivity;
import com.example.marni.orderapp.Presentation.Activities.OrderDetailActivity;
import com.example.marni.orderapp.Presentation.Activities.OrderHistoryActivity;
import com.example.marni.orderapp.Presentation.Activities.ProductsActivity;
import com.example.marni.orderapp.Presentation.Activities.TopUpActivity;
import com.example.marni.orderapp.R;

import static com.example.marni.orderapp.Presentation.Activities.OrderHistoryActivity.ORDER;

/**
 * Created by marni on 11-5-2017.
 */

public class DrawerMenu implements OrdersGetTask.OnOrderAvailable {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private Intent intent;

    public DrawerMenu(Context context, int id) {

        this.context = context;

        switch (id) {
            case R.id.nav_assortment:
                intent = new Intent(context, ProductsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case R.id.nav_my_order:

                getCurrent("https://mysql-test-p4.herokuapp.com/order/current/284");
                break;
            case R.id.nav_order_history:
                intent = new Intent(context, OrderHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case R.id.nav_top_up:
                intent = new Intent(context, TopUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case R.id.nav_allergy_information:
                intent = new Intent(context, AllergiesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
        }
    }

    private void getCurrent(String apiUrl) {

        Log.i(TAG, "getCurrent called.");

        String[] urls = new String[]{apiUrl};
        OrdersGetTask task = new OrdersGetTask(this);
        task.execute(urls);
    }

    @Override
    public void onOrderAvailable(Order order) {

        intent = new Intent(context, OrderDetailActivity.class);

        intent.putExtra(ORDER, order);

        context.startActivity(intent);
    }
}
