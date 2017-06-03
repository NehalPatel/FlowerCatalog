package com.tecknic.nehal.flowercatalog.request;

import android.net.Credentials;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

public class OkHTTPManager {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    private int responseCount(Response response){
        int result = 1;
        while ((response = response.priorResponse()) != null){
            result++;
        }
        return result;
    }

    public String run(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();


        Response response = client.newCall(request).execute();
        return response.body().string();
    }

/*    public String run(String url, final String username, final String password) throws IOException{

        client.setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) throws IOException {
                if(responseCount(response) >= 3){
                    return null;
                }

                String credentials = Credentials.basic(username, password);
                return null;
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                return null;
            }
        })

        Request request = new Request.Builder()
                .url(url)
                .build();


        Response response = client.newCall(request).execute();
        return response.body().string();
    }*/

    public String post(String url, String json) throws IOException{
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return  response.body().string();
    }

}
