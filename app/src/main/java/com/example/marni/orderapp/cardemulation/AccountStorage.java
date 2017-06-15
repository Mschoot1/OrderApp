/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.marni.orderapp.cardemulation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Utility class for persisting account numbers to disk.
 * <p>
 * <p>The default SharedPreferences instance is used as the backing storage. Values are cached
 * in memory for performance.
 * <p>
 * <p>This class is thread-safe.
 */
public class AccountStorage {
    private static final String PREF_ACCOUNT_NUMBER = "account_number";
    private static final String DEFAULT_ACCOUNT_NUMBER = "00000000";
    private static final String ERROR_CODE = "0000";
    private static String sAccount = null;
    private static final Object sAccountLock = new Object();

    private AccountStorage() {

    }

    public static void setAccount(Context c, String s, double balance, double orderPriceTotal) {
        synchronized (sAccountLock) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            if (balance >= orderPriceTotal) {
                prefs.edit().putString(PREF_ACCOUNT_NUMBER, s).apply();
                sAccount = s;
            } else {
                prefs.edit().putString(PREF_ACCOUNT_NUMBER, DEFAULT_ACCOUNT_NUMBER).apply();
                sAccount = DEFAULT_ACCOUNT_NUMBER;
            }
        }
    }

    public static void resetAccount(Context c) {
        synchronized (sAccountLock) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putString(PREF_ACCOUNT_NUMBER, ERROR_CODE).apply();
            sAccount = ERROR_CODE;
        }
    }

    public static String getAccount(Context c) {
        synchronized (sAccountLock) {
            if (sAccount == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                sAccount = prefs.getString(PREF_ACCOUNT_NUMBER, DEFAULT_ACCOUNT_NUMBER);
            }
            return sAccount;
        }
    }
}
