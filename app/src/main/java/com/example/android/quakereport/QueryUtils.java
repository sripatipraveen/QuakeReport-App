package com.example.android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /** Sample JSON response for a USGS query */
    private static final String USGS_Request_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("QueryUtils", "Error with creating URL");
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) {

        String jsonResponse = "";
        //if the URL is null, return early
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000/*in milli seconds*/);
            urlConnection.setConnectTimeout(150000/*in milli seconds*/);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("QueryUtils", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("QueryUtils", "Error establishing the http connection");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e("QueryUtils", "inputStream Error");
                }
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     */

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Word} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Word> extractEarthquakes(String jsonResponse) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Word> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // TODO: Parse the response given by the jsonResponse string and
            // build up a list of Earthquake objects with the corresponding data.

            // Create a @root json object that we can extract details present in this json object
            JSONObject root = new JSONObject(jsonResponse);

            // Create a @featuresArray json array that we can extract details present in this json array
            JSONArray featuresArray = root.getJSONArray("features");

            // loop through all the features
            for (int i = 0; i < featuresArray.length(); i++) {

                // Create a @arrayObjects to create json objects at ith position
                JSONObject arrayObjects = featuresArray.getJSONObject(i);

                // Create a @properties json object to retrieve the properties in this json object
                JSONObject properties = arrayObjects.getJSONObject("properties");

                // Create a string to retrieve the magnitude key value in the properties json object
                double magnitude = properties.getDouble("mag");

                // Create a string to retrieve the place key value in the properties json object
                String place = properties.getString("place");

                String webUrl = properties.getString("url");

                // Create a string to retrieve the time key value in the properties json object
                long time = properties.getLong("time");

                // Create Earthquake java object from magnitude, location, and time
                Word earthquake = new Word(magnitude,place,time,webUrl);

                // Add earthquake to list of earthquakes
                earthquakes.add(earthquake);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }


}