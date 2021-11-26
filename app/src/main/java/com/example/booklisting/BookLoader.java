package com.example.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    private static final String LOG_TAG = BookLoader.class.getName();
    private String url;
    /**
     * @param context
     * @param url
     */
    public BookLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (this.url == null) {
            return null;
        }
        return BookUtils.ExtractBooks(url);
    }
}
