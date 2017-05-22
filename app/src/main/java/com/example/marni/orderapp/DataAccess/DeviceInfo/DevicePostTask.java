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

import static android.content.ContentValues.TAG;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class DevicePostTask extends AsyncTask<String, Void, Boolean> {

    private SuccessListener listener;

    public DevicePostTask(SuccessListener listener) {

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

            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpConnection.setRequestMethod("POST");

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("customer_id",params[1]);
            jsonParam.put("hardware", params[2]);
            jsonParam.put("type", params[3]);
            jsonParam.put("model", params[4]);
            jsonParam.put("brand", params[5]);
            jsonParam.put("device", params[6]);
            jsonParam.put("manufacturer", params[7]);
            jsonParam.put("user", params[8]);
            jsonParam.put("serial", params[9]);
            jsonParam.put("host", params[10]);
            jsonParam.put("device_id", params[11]);
            jsonParam.put("bootloader", params[12]);
            jsonParam.put("board", params[13]);
            jsonParam.put("display", params[14]);

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

        listener.successfulPost(response);
    }

    public interface SuccessListener {
        void successfulPost(Boolean successful);
    }
}
