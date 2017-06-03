package com.tecknic.nehal.flowercatalog;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpManager {

    public static String getData(String uri, String username, String password){

        BufferedReader reader = null;
        HttpURLConnection con = null;

        byte[] loginBytes = (username + ":" + password).getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        try {
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();

            con.addRequestProperty("Authorization", loginBuilder.toString());

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();

            int responseCode = 0;
            try {
                responseCode = con.getResponseCode();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            if(reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
        }

    }

    public static String getData(String uri){

        BufferedReader reader = null;

        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
        }

    }

}
