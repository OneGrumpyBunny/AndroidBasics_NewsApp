package com.example.grumpybunny.newsapp;

import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.List;

/** QueryUtils class defines parameters for making an http connection,
 * retrieving JSON data and parsing it into individual values
 * to be passed to the EventAdapter.
 */

public class QueryUtils {

    private QueryUtils() {

    }
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<Event> fetchEventData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem in HTTP request");
        }
        List<Event> event = extractEventsFromJson(jsonResponse);
        return event;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem in building the URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
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

    private static List<Event> extractEventsFromJson(String eventsJSON) {
        // initialize key values to empty strings in case key is not found
        String authorFName = "";
        String authorLName = "";
        String title = "";
        String pubDate = "";
        String section = "";
        String url = "";
        String type = "";

        if (TextUtils.isEmpty(eventsJSON)) {
            return null;
        }

        List<Event> events = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(eventsJSON);
            JSONObject response = root.optJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            Log.d("EventAdapter","There are " + results.length() + " results");
            // grab keys in the results array
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentResults = results.getJSONObject(i);

                JSONArray tags = currentResults.getJSONArray("tags");

                Log.d("EventAdapter", "There are " + tags.length() + " tags");


                if (tags.length() > 0) {

                    JSONObject tagsresults = tags.getJSONObject(0);

                    if (tagsresults.has("firstName")) {
                        authorFName = tagsresults.getString("firstName");
                        if (authorLName.length() > 0) {
                            authorFName = authorFName.substring(0, 1).toUpperCase() + authorFName.substring(1);
                        }
                    }

                    if (tagsresults.has("lastName")) {
                        authorLName = tagsresults.getString("lastName");
                        if (authorLName.length() > 0) {
                            authorLName = authorLName.substring(0, 1).toUpperCase() + authorLName.substring(1);
                        }
                    }

                    String author = authorFName + " " + authorLName;

                    if (currentResults.has("webTitle")) {
                        title = currentResults.getString("webTitle");
                    }

                    if (currentResults.has("webPublicationDate")) {
                        pubDate = currentResults.getString("webPublicationDate");

                        // we only want the date portion of the time stamp.
                        // It is a String (not milliseconds) and thus we use substring to extract the date portion
                        if (pubDate.length() > 0) {
                            pubDate = pubDate.substring(0, 10);
                        }
                    }

                    if (currentResults.has("sectionName")) {
                        section = currentResults.getString("sectionName");
                    }

                    if (currentResults.has("webUrl")) {
                        url = currentResults.getString("webUrl");
                    }

                    if (currentResults.has("type")) {
                        type = currentResults.getString("type");
                    }

                    Event event = new Event(title, pubDate, section, url, type, author);
                    events.add(event);
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing News JSON results", e);
        }
        return events;
    }
}
