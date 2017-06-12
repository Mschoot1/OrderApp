package com.example.marni.orderapp.businesslogic;

import android.util.Log;

public class CalculateBalance {

    private final String tag = getClass().getSimpleName();
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

    public void newBalance(double currentBalance, double addedBalance){
        double newBalance = currentBalance + addedBalance;

        setBalance(newBalance);
        setAddedBalance(addedBalance);

        listener.onBalanceChanged(newBalance);
    }

    public double maxBalance(double currentBalance){
        return (150 - currentBalance);
    }

    public void checkPayment(){
        String check;

        if(Double.compare(getAddedBalance(), 0.0) == 0) {
            Log.i(tag, "Check false, added balance " + getAddedBalance());
            check = "zero";
        } else if(getBalance() > 150){
            Log.i(tag, "Check false, new balance " + getBalance());
            check = "max";
        } else {
            Log.i(tag, "Check True, added balance " + getAddedBalance());
            Log.i(tag, "Check True, new balance " + getBalance());
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
