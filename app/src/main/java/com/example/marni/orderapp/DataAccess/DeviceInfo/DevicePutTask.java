package com.example.marni.orderapp.DataAccess.DeviceInfo;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class DevicePutTask extends AsyncTask<String, Void, Boolean> {

    private final String TAG = getClass().getSimpleName();

    private SuccessListener listener;

    public DevicePutTask(SuccessListener listener) {

        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        int responseCode;
        String balanceUrl = params[0];

        Boolean response = null;

        Log.i(TAG, "doInBackground - " + balanceUrl);
        try {
            URL url = new URL(balanceUrl);
            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                return null;
            }

            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;

            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestMethod("PUT");
            httpConnection.setRequestProperty("Authorization", "Bearer " + params[1]);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("customer_id",params[2]);
            jsonParam.put("hardware", params[3]);
            jsonParam.put("type", params[4]);
            jsonParam.put("model", params[5]);
            jsonParam.put("brand", params[6]);
            jsonParam.put("device", params[7]);
            jsonParam.put("manufacturer", params[8]);
            jsonParam.put("user", params[9]);
            jsonParam.put("serial", params[10]);
            jsonParam.put("host", params[11]);
            jsonParam.put("device_id", params[12]);
            jsonParam.put("bootloader", params[13]);
            jsonParam.put("board", params[14]);
            jsonParam.put("display", params[15]);

            Log.i(TAG, String.valueOf(jsonParam));

            DataOutputStream localDataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
            localDataOutputStream.writeBytes(jsonParam.toString());
            localDataOutputStream.flush();
            localDataOutputStream.close();
            httpConnection.connect();

            responseCode = httpConnection.getResponseCode();
            response = (responseCode == HttpURLConnection.HTTP_OK);
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground MalformedURLEx " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "doInBackground IOException " + e.getLocalizedMessage());
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    protected void onPostExecute(Boolean response) {

        listener.successfulPut(response);
    }

    public interface SuccessListener {
        void successfulPut(Boolean successful);
    }
}
