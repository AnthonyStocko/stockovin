package net.stockovin;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {

    private static final String TAG = "RequestHandler";

    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(requestURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "URL=" + requestURL + " responseCode=" + responseCode);

            // On lit le bon flux selon le code HTTP
            InputStream is = (responseCode >= 200 && responseCode < 400)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Réponse HTTP non-OK (" + responseCode + ") : " + sb.toString());
            }

        } catch (Exception e) {
            // On NE masque plus l'erreur : elle apparaît clairement dans Logcat
            Log.e(TAG, "Erreur réseau sur " + requestURL, e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return sb.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
