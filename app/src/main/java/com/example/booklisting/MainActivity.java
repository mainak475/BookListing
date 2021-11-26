package com.example.booklisting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static final String LOG_TAG = MainActivity.class.getName();
    private final String BASEURL = "https://www.googleapis.com/books/v1/volumes?q=";
    // title, authors, publisher, publishedDate, buyLink.
    //search&startIndex=0&maxResults=10

    private static final int BOOK_LOADER_ID = 1;

    private static final int MAX_RESULTS = 10;

    private BookAdapter bookAdapter;

    private int startIndex;

    private TextView emptyTextView;

    private ListView listView;

    private ProgressBar progressBar;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);


        emptyTextView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyTextView);

        Button nextButton = findViewById(R.id.next_button);

        EditText searchEditText = findViewById(R.id.search_bar);

          LoaderManager loaderManager = getLoaderManager();
        if (loaderManager.getLoader(BOOK_LOADER_ID) != null) {
            startIndex = 0;
            nextButton.setVisibility(View.VISIBLE);
            String searchText = searchEditText.getText().toString();
            url = BASEURL + searchText +getString(R.string.startindex)
                    + startIndex + getString(R.string.max_results) + MAX_RESULTS;
            loaderManager.initLoader(BOOK_LOADER_ID,null,this);
        }

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(view -> {
            String searchText = searchEditText.getText().toString();
            if (searchText == null || searchText.isEmpty()) {
                Toast.makeText(MainActivity.this, "Write some topic to search", Toast.LENGTH_SHORT).show();
                return;
            }
            List<Book> books = new ArrayList<>();
            UpdateUI(books);

            nextButton.setVisibility(View.VISIBLE);

            startIndex = 0;

            utilUI(loaderManager, startIndex, searchText);
        });

        Button prevButton = findViewById(R.id.prev_button);
        prevButton.setOnClickListener(view -> {
            String searchText = searchEditText.getText().toString();
            startIndex -= MAX_RESULTS;
            utilUI(loaderManager, startIndex, searchText);
            if (startIndex <= 0) {
                prevButton.setVisibility(View.GONE);
            }
        });

        nextButton.setOnClickListener(view -> {
            String searchText = searchEditText.getText().toString();
            startIndex += MAX_RESULTS;
            utilUI(loaderManager, startIndex, searchText);
            if (startIndex >= MAX_RESULTS) {
                prevButton.setVisibility(View.VISIBLE);
            }
            if (BookUtils.getTotalBooks() - startIndex - 10 < 10 || startIndex >= 190) {
                nextButton.setVisibility(View.GONE);
            }
        });

    }

    private void utilUI(LoaderManager loaderManager, int startIndex, String searchText) {

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            progressBar = findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);
            url = BASEURL + searchText + MainActivity.this.getString(R.string.startindex)
                    + startIndex + MainActivity.this.getString(R.string.max_results) + MAX_RESULTS;
            loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);

        } else {
            Log.i(LOG_TAG, "onCreate: no internet");
            ProgressBar progressBar = findViewById(R.id.progressbar);
            progressBar.setVisibility(View.GONE);
            emptyTextView.setText(R.string.no_internet_connection);
        }
    }

    private void UpdateUI(List<Book> books) {
        bookAdapter = new BookAdapter(this, books);
        listView.setAdapter(bookAdapter);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        emptyTextView.setText(R.string.no_books_found);
        if (books != null && !books.isEmpty()) {
            UpdateUI(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        bookAdapter.clear();
    }
}