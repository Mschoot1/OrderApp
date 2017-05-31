package com.example.marni.orderapp.BusinessLogic;

import android.util.Log;

/**
 * Created by marcu on 5/9/2017.
 */

public class CalculateBalance {

    private final String TAG = getClass().getSimpleName();
    private OnBalanceChanged listener;
    private OnResetBalance listener2;
    private OnCheckPayment listener3;

    private double balance;
    private double addedBalance;

    public CalculateBalance(OnBalanceChanged listener, OnResetBalance listener2, OnCheckPayment listener3) {
        this.listener = listener;
        this.listener2 = listener2;
        this.listener3 = listener3;
    }

    public void newBalance(double current_balance, double added_balance){
        double newBalance = current_balance + added_balance;

        setBalance(newBalance);
        setAddedBalance(added_balance);

        listener.onBalanceChanged(newBalance);
    }

    public double maxBalance(double current_balance){
        double newBalance = (150 - current_balance);

        return newBalance;
    }

    public void checkPayment(){
        String check;

        if(getAddedBalance() == 0){
            Log.i(TAG, "Check false, added balance " + getAddedBalance());
            check = "zero";
        } else if(getBalance() > 150){
            Log.i(TAG, "Check false, new balance " + getBalance());
            check = "max";
        } else {
            Log.i(TAG, "Check True, added balance " + getAddedBalance());
            Log.i(TAG, "Check True, new balance " + getBalance());
            check = "succes";
        }

        listener3.onCheckPayment(check);
    }

    public interface OnBalanceChanged {
        void onBalanceChanged(double newBalance);
    }

    public interface OnResetBalance {
        void onResetBalance(double balance);
    }

    public interface OnCheckPayment {
        void onCheckPayment(String check);
    }

    public void resetBalance(boolean reset){
        if(reset) {
            setBalance(0);
            setAddedBalance(0);
        } else {
            setBalance(0);
        }

        listener2.onResetBalance(balance);
    }

    public double getBalance() {
        return balance;
    }

    public double getAddedBalance() {
        return addedBalance;
    }

    public void setAddedBalance(double addedBalance) {
        this.addedBalance = addedBalance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
