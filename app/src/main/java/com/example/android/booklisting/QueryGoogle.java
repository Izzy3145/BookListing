package com.example.android.booklisting;

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

/**
 * Created by izzystannett on 04/06/2017.
 */

/**
 * helper methods for requesting httpconneection and parsing jsonResponse
 */

public final class QueryGoogle {

    /**
     * Tag for the log messages that returns simple class name
     */
    private static final String LOG_TAG = QueryGoogle.class.getSimpleName();

    /**
     * add the fetchSearchResults method to tie all helper methods together;
     * create a URL, send a http request and parse the JSON response
     * this the only metho=d the ASync method will call, and is therfore the only public method
     */
    public static List<BookItem> fetchSearchResults(URL providedUrl) {

        //perform HTTP request to the queiried URL and receive JSONresponse back
        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(providedUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        List<BookItem> bookItems = extractFeatureFromJson(jsonResponse);
        return bookItems;
    }

    /**
     * perform a network request to the given URL and return String as a response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        //the output string starts as an empty string, ready to be 'built'
        String jsonResponse = "";

        //if the string is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        //initialise objects
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            //try to connect
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //if connection successful, then read input stream
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Problem with connection. Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                //call close() method, which may cause IOExcpetion
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * convert the inputstream, which is currently in byte-form
     * into a readable string
     */

    private static String readFromStream(InputStream inputStream) throws IOException {
        //create String builder so the buffered reader can continually add characters until end of inputStream
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            //call the readLine() method, which may cause an IOException
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a Book object by parsing the JSON response
     */
    private static List<BookItem> extractFeatureFromJson(String jsonResponse) {
        List<BookItem> bookItems = new ArrayList<>();

        //if the jsonString is empty, return early
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

            String bookImage;
            String bookTitle;
            String bookAuthors;

            // If there are results in the features array
            for (int i = 0; i < itemsArray.length(); i++) {
                //Extract book items from the JSON response (parse the JSON)
                JSONObject bookObject = itemsArray.getJSONObject(i);
                JSONObject bookDetails = bookObject.getJSONObject("volumeInfo");
                //if the "volume info" object has an entry for title, return it
                if (bookDetails.has("title")) {
                    bookTitle = bookDetails.getString("title");
                } else {
                    bookTitle = null;
                }
                //return the book's author/s
                JSONArray authorsArray = bookDetails.getJSONArray("authors");
                bookAuthors = formatAuthorsList(authorsArray);

                JSONObject bookImagesLinks = bookDetails.getJSONObject("imageLinks");
                //if the "imageLinks" object has an entry for thumbnail image Url, return it
                if (bookImagesLinks.has("thumbnail")) {
                    bookImage = bookImagesLinks.getString("thumbnail");
                } else {
                    bookImage = null;
                }

                //create new BookItem object and add to the Array List
                BookItem foundBook = new BookItem(bookImage, bookTitle, bookAuthors);
                bookItems.add(foundBook);
            }
            return bookItems;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON results", e);
        }
        return null;
    }

    /**
     * extract any number of authors from the authors array
     */

    private static String formatAuthorsList(JSONArray authorsArray) throws JSONException {
        String authorsArrayInString = null;
        if (authorsArray.length() == 0) {
            return null;
        }
        for (int i = 0; i < authorsArray.length(); i++) {
            if (i == 0) {
                authorsArrayInString = authorsArray.getString(0);
            } else {
                authorsArrayInString += authorsArray.getString(i);
            }
        }
        return authorsArrayInString;
    }
}

