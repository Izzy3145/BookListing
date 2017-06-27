package com.example.android.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.empty;
import static com.example.android.booklisting.R.id.progress_bar;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static String API_URL = "https://www.googleapis.com/books/v1/volumes?maxResults=10&q=";
    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private String SEARCH_RESULTS = "searchedBooksResults";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find a reference to the listView in the layout
        ListView bookListView = (ListView) findViewById(R.id.book_list_view);

        //create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<BookItem>());

        //set the adapter to the list view so it can be populated
        bookListView.setAdapter(mAdapter);

        //set up the empty view for when no results are returned
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ask Connectivity Manager to check the status of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                //get status of default network connection
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                //if there is a network connection, initialise loader
                if (networkInfo != null && networkInfo.isConnected()) {

                    //call the Async method to make connection to given API in background
                    SearchAsyncTask task = new SearchAsyncTask();
                    task.execute(queriedURLString());

                } else {

                    //otherwise set the EmptyView to display an appropriate message
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });

        if (savedInstanceState != null) {
            BookItem[] bookItems = (BookItem[]) savedInstanceState.getParcelableArray(SEARCH_RESULTS);
            mAdapter.addAll(bookItems);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BookItem[] bookListings = new BookItem[mAdapter.getCount()];
        for (int i = 0; i < bookListings.length; i++) {
            bookListings[i] = mAdapter.getItem(i);
        }
        outState.putParcelableArray(SEARCH_RESULTS, bookListings);
    }

    /**
     * create queried URL string from search input
     */

    private String queriedURLString() {
        //make the searched term a String
        EditText searchEditText = (EditText) findViewById(R.id.searchText);
        String query = searchEditText.getText().toString();
        //add the searched term to the end of the Google API URL with additional formatting
        String queriedURLString = query.trim().replaceAll("\\s+", "+");
        return API_URL + queriedURLString;
    }

    /**
     * create URL object from string
     */
    private URL createUrl(String stringUrl) {
        //handle null case
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    private void updateUI(List<BookItem> bookItems) {
        mAdapter.clear();
        mAdapter.addAll(bookItems);
    }

    /**
     * set up ASync method to request network connection on background thread
     */
    private class SearchAsyncTask extends AsyncTask<String, Void, List<BookItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<BookItem> doInBackground(String... urls) {
            URL url = createUrl(queriedURLString());

            List<BookItem> bookItems = QueryGoogle.fetchSearchResults(url);
            return bookItems;
        }

        @Override
        protected void onPostExecute(List<BookItem> bookItems) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            //if no books found then setEmptyView
            if (bookItems == null) {
                mEmptyStateTextView.setText(R.string.no_results);
                return;
            }
            //otherwise update the UI with the list of books
            updateUI(bookItems);

        }
    }
}
