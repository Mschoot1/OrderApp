package com.example.marni.orderapp.BusinessLogic;


import android.content.Context;
import android.widget.Toast;

import com.example.marni.orderapp.DataAccess.Balance.BalanceGetTask;
import com.example.marni.orderapp.Domain.Balance;

/**
 * Created by marcu on 5/11/2017.
 */

public class CalculateQuantity{
    private String method;

    public String getmethod(int current_quantity, int new_quantity){
        if(current_quantity == new_quantity){
            method = "";
        } else if (new_quantity == 0){
            method = "delete";
        } else if ((new_quantity > current_quantity || new_quantity < current_quantity) && current_quantity != 0){
            method = "put";
        } else if (current_quantity == 0 && new_quantity > 0){
            method = "post";
        }

        return method;
    }
}
