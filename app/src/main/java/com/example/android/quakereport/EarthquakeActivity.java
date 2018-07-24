/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.WrapperListAdapter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=5&limit=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        EarthquakeAsyncTask task = new EarthquakeAsyncTask();

        task.execute();

    }

    private void updateUi(ArrayList<Word> extractearthquakes) {

        // Create a fake list of earthquake locations.

        final ArrayList<Word> earthquakes = extractearthquakes;

        // Find a reference to the {@link ListView} in the layout
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        final WordAdapter adapter = new WordAdapter(this, earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String textMessage = "Error! Abort!";
                // Find the current earthquake that was clicked on
                Word currentWord = adapter.getItem(i);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentWord.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);

            }
        });
    }

    private class EarthquakeAsyncTask extends AsyncTask<URL, Void, ArrayList<Word>> {
        @Override
        protected ArrayList<Word> doInBackground(URL... urls) {
            //Create Url object
            URL url = QueryUtils.createUrl(USGS_REQUEST_URL);

            //Perform Http response to the URL and recieve a JSON response
            String jsonResponse = "";

            jsonResponse = QueryUtils.makeHttpRequest(url);

            //Extract desirable fields from the JSON Response
            ArrayList<Word> earthquakes = QueryUtils.extractEarthquakes(jsonResponse);
            return earthquakes;
        }

        @Override
        protected void onPostExecute(ArrayList<Word> earthquakes) {
            if (earthquakes == null) {
                return;
            }

            updateUi(earthquakes);
        }
    }
}