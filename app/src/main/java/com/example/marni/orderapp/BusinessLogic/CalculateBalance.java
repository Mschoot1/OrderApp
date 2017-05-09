package com.example.marni.orderapp.BusinessLogic;

/**
 * Created by marcu on 5/9/2017.
 */

public class CalculateBalance {

    public double newBalance(double current_balance, double added_balance){
        double newBalance = current_balance + added_balance;

        return newBalance;
    }
}
