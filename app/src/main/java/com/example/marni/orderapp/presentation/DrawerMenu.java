package com.example.marni.orderapp.presentation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.marni.orderapp.dataaccess.orders.OrdersGetCurrentTask;
import com.example.marni.orderapp.domain.Order;
import com.example.marni.orderapp.presentation.activities.AllergiesActivity;
import com.example.marni.orderapp.presentation.activities.LoginActivity;
import com.example.marni.orderapp.presentation.activities.MyOrderActivity;
import com.example.marni.orderapp.presentation.activities.OrderHistoryActivity;
import com.example.marni.orderapp.presentation.activities.TopUpActivity;
import com.example.marni.orderapp.R;

import static com.example.marni.orderapp.presentation.activities.OrderHistoryActivity.ORDER;

public class DrawerMenu implements OrdersGetCurrentTask.OnCurrentOrderAvailable {

    private final String tag = getClass().getSimpleName();

    private Context context;
    private Intent intent;

    private String jwt;

    public DrawerMenu(Context context, int id, String jwt, int user) {

        Log.i(tag, "user: " + user);

        this.context = context;
        this.jwt = jwt;

        switch (id) {
            case R.id.nav_my_order:
                intent = new Intent(context, MyOrderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getCurrent("https://mysql-test-p4.herokuapp.com/order/current/" + user);
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
            case R.id.nav_logout:
                intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            default :
                break;
        }
    }

    private void getCurrent(String apiUrl) {

        Log.i(tag, "getCurrent called.");

        String[] urls = new String[]{apiUrl, jwt};
        OrdersGetCurrentTask task = new OrdersGetCurrentTask(this);
        task.execute(urls);
    }

    @Override
    public void onCurrentOrderAvailable(Order order) {
        intent.putExtra(ORDER, order);
        context.startActivity(intent);
    }
}
