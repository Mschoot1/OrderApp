package com.example.marni.orderapp.BusinessLogic;

/**
 * Created by marcu on 5/9/2017.
 */

public class CalculateBalance {

    OnBalanceChanged listener;
    OnResetBalance listener2;
    private double balance;
    private int added_balance;

    public CalculateBalance(OnBalanceChanged listener, OnResetBalance listener2) {
        this.listener = listener;
        this.listener2 = listener2;
    }

    public void newBalance(double current_balance,int added_balance){
        double newBalance = current_balance + added_balance;

        if(added_balance != 0){
            balance = newBalance;
            listener.onBalanceChanged(newBalance);
        }
    }

    public interface OnBalanceChanged {
        void onBalanceChanged(double newBalance);
    }

    public interface OnResetBalance {
        void onResetBalance(double balance);
    }

    public void resetBalance(){
        balance = 0;

        listener2.onResetBalance(balance);
    }

    public double getBalance() {
        return balance;
    }

    public double getAdded_balance() {
        return added_balance;
    }

    public void setAdded_balance(int added_balance) {
        this.added_balance = added_balance;
    }

    public void resetAddedBalance(){
        added_balance = 0;
    }
}
