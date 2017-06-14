package com.example.marni.orderapp;

import com.example.marni.orderapp.businesslogic.CalculateBalance;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by marcu on 6/14/2017.
 */

public class CalculateBalanceTest implements CalculateBalance.OnBalanceChanged, CalculateBalance.OnCheckPayment, CalculateBalance.OnResetBalance{
    CalculateBalance calculateBalance;
    Double addedbalance;

    public CalculateBalanceTest(){
        calculateBalance = new CalculateBalance(this, this, this);
        calculateBalance.setAddedBalance(0);
        calculateBalance.setBalance(160);

        addedbalance = calculateBalance.getAddedBalance();
    }

    @Test
    public void calculateBalance_newBalance_isCorrect() throws Exception{
        assertEquals(15, calculateBalance.newBalance(10,5), 0.01);
    }

    @Test
    public void calculateBalance_newBalance_isNotCorrect() throws Exception{
        assertNotEquals(16, calculateBalance.newBalance(10 , 5), 0.01);
    }

    @Test
    public void calculateBalance_maxBalance_isCorrect() throws Exception{
        assertEquals(100, calculateBalance.maxBalance(50), 0.01);
    }

    @Test
    public void calculateBalance_maxBalance_isNotCorrect() throws Exception{
        assertNotEquals(50, calculateBalance.maxBalance(50), 0.01);
    }

    @Test
    public void calculateBalance_checkPayment_isCorrect1() throws Exception{
        calculateBalance.setAddedBalance(0);
        calculateBalance.setBalance(140);
        assertEquals("zero", calculateBalance.checkPayment());
    }

    @Test
    public void calculateBalance_checkPayment_isCorrect2() throws Exception{
        calculateBalance.setAddedBalance(14);
        calculateBalance.setBalance(158);
        assertEquals("max", calculateBalance.checkPayment());
    }

    @Test
    public void calculateBalance_checkPayment_isCorrect3() throws Exception{
        calculateBalance.setAddedBalance(14);
        calculateBalance.setBalance(149);
        assertEquals("succes", calculateBalance.checkPayment());
    }

    @Test
    public void calculateBalance_resetBalance_isCorrect() throws Exception{
        calculateBalance.setAddedBalance(12);
        calculateBalance.setBalance(130);
        assertEquals(0, calculateBalance.resetBalance(true), 0.01);
    }

    @Test
    public void calculateBalance_resetBalance_isNotCorrect() throws Exception{
        calculateBalance.setAddedBalance(12);
        calculateBalance.setBalance(130);
        assertNotEquals(5, calculateBalance.resetBalance(true), 0.01);
    }

    @Override
    public void onBalanceChanged(double newBalance) {

    }

    @Override
    public void onResetBalance(double balance) {

    }

    @Override
    public void onCheckPayment(String check) {

    }
}
