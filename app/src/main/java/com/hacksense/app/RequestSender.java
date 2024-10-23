package com.hacksense.app;

import static com.hacksense.app.HACKSense.TAG;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestSender {

    public static @Nullable String execute() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://vsza.hu/hacksense/status.json");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } else {
                Log.e(TAG, "Error: Server returned response code " + responseCode);
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error making GET request", e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing BufferedReader", e);
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
