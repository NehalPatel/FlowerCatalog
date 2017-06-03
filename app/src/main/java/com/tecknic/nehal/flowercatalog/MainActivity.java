package com.tecknic.nehal.flowercatalog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tecknic.nehal.flowercatalog.model.Flower;
import com.tecknic.nehal.flowercatalog.parsers.FlowerJSONParser;
import com.tecknic.nehal.flowercatalog.request.OkHTTPManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://services.hanselandpetal.com/";
    public static final String FEED_URL = BASE_URL + "feeds/flowers.json";
    public static final String PHOTOS_BASE_URL = BASE_URL + "photos/";
    public static Boolean isCallInProgress = false;

    TextView output;
    ProgressBar pb;
    List<MyTask> tasks;
    List<Flower> flowerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                requestData();
            }
        });

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_do_task) {

            requestData();
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestData() {
        if( isCallInProgress )
        {
            Toast.makeText(this, "A request already in progress...", Toast.LENGTH_LONG).show();
            return;
        }
        if (isOnline()) {
            //requestData("http://services.hanselandpetal.com/feeds/flowers.xml");
            //requestData("http://services.hanselandpetal.com/feeds/flowers.json");
            //requestData("http://services.hanselandpetal.com/secure/flowers.json");
            MyTask task = new MyTask();
            //task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Param 1", "Param 2", "Param 3");
            task.execute(FEED_URL);
            isCallInProgress = true;
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }


    }

    protected void updateDisplay() {

        FlowerAdapter adapter = new FlowerAdapter(this, R.layout.item_flower, flowerList);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(adapter);

        isCallInProgress = false;
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    private class MyTask extends AsyncTask<String, String, List<Flower>>{

        @Override
        protected void onPreExecute() {
            //updateDisplay("Starting task");
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Flower> doInBackground(String... params) {

            //String content = HttpManager.getData(params[0]);
            //String content = HttpManager.getData(params[0], "feeduser", "feedpassword");

            OkHTTPManager httpManager = new OkHTTPManager();
            String content = null;
            flowerList = null;
            try {
                content = httpManager.run(params[0]);
                flowerList = FlowerJSONParser.parseFeed(content);
            } catch (IOException e) {
                Log.d("FLOWER", e.getMessage());
                e.printStackTrace();
            }

            return flowerList;
        }

        @Override
        protected void onPostExecute(List<Flower> flowerList) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (flowerList.isEmpty()) {
                Toast.makeText(MainActivity.this, "Error while fetching data from feed", Toast.LENGTH_LONG).show();
                return;
            }

            //flowerList = FlowerXMLParser.parseFeed(s);
            //flowerList = FlowerJSONParser.parseFeed(s);
            updateDisplay();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //updateDisplay(values[0]);
        }
    }
}
