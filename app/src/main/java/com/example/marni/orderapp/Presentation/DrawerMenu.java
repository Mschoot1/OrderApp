package com.example.marni.orderapp.Presentation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.example.marni.orderapp.DataAccess.Orders.OrdersGetCurrentTask;
import com.example.marni.orderapp.DataAccess.Orders.OrdersGetTask;
import com.example.marni.orderapp.Domain.Order;
import com.example.marni.orderapp.Presentation.Activities.AllergiesActivity;
import com.example.marni.orderapp.Presentation.Activities.LogInActivity;
import com.example.marni.orderapp.Presentation.Activities.MyOrderActivity;
import com.example.marni.orderapp.Presentation.Activities.OrderHistoryActivity;
import com.example.marni.orderapp.Presentation.Activities.ProductsActivity;
import com.example.marni.orderapp.Presentation.Activities.TopUpActivity;
import com.example.marni.orderapp.R;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.USER;
import static com.example.marni.orderapp.Presentation.Activities.OrderHistoryActivity.ORDER;

public class DrawerMenu implements OrdersGetCurrentTask.OnCurrentOrderAvailable {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private Intent intent;

    private String jwt;

    public DrawerMenu(Context context, int id, String jwt, int user) {

        Log.i(TAG, "user: " + user);

        this.context = context;
        this.jwt = jwt;

        switch (id) {
            case R.id.nav_my_order:
                intent = new Intent(context, MyOrderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(JWT_STR, jwt);
                intent.putExtra(USER, user);
                getCurrent("https://mysql-test-p4.herokuapp.com/order/current/" + user);
                break;
            case R.id.nav_order_history:
                intent = new Intent(context, OrderHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(JWT_STR, jwt);
                intent.putExtra(USER, user);
                context.startActivity(intent);
                break;
            case R.id.nav_top_up:
                intent = new Intent(context, TopUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(JWT_STR, jwt);
                intent.putExtra(USER, user);
                context.startActivity(intent);
                break;
            case R.id.nav_allergy_information:
                intent = new Intent(context, AllergiesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(JWT_STR, jwt);
                intent.putExtra(USER, user);
                context.startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(context, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
        }
    }

    private void getCurrent(String apiUrl) {

        Log.i(TAG, "getCurrent called.");

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
