package com.example.marni.orderapp.businesslogic;

public class CalculateBalance {

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

    public Double newBalance(double currentBalance, double addedBalance){
        double newBalance = currentBalance + addedBalance;

        setBalance(newBalance);
        setAddedBalance(addedBalance);

        listener.onBalanceChanged(newBalance);
        return newBalance;
    }

    public double maxBalance(double currentBalance){
        double maxBalance = (150 - currentBalance);

        return maxBalance;
    }

    public String checkPayment(){
        String check;

        if(Double.compare(getAddedBalance(), 0.0) == 0) {
            check = "zero";
        } else if(getBalance() > 150){
            check = "max";
        } else {
            check = "succes";
        }

        listener3.onCheckPayment(check);
        return check;
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

    public double resetBalance(boolean reset){
        if(reset) {
            setBalance(0);
            setAddedBalance(0);
        } else {
            setBalance(0);
        }

        listener2.onResetBalance(balance);
        return balance;
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
