package com.example.booklisting;

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
import java.util.ArrayList;
import java.util.List;

public final class BookUtils {
    private static final String LOG_TAG = BookUtils.class.getName();

    private static Long totalBooks = 0l;

    private BookUtils() {}

    public static List<Book> ExtractBooks(String requestUrl) {
        List<Book> bookList;

        URL url = createUrl(requestUrl);

        String JSONResponse = null;
        try {
            JSONResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in making http request ", e);
        }
        Log.i(LOG_TAG, "ExtractBooks: " + JSONResponse);

        bookList = extractDetailsFromJSON(JSONResponse);

        return bookList;
    }

    private static List<Book> extractDetailsFromJSON(String jsonResponse) {
        List<Book> bookList = new ArrayList<>();
        try {
            JSONObject rootObject = new JSONObject(jsonResponse);
            totalBooks = rootObject.optLong("totalItems");
            Log.i(LOG_TAG, "extractDetailsFromJSON: TotalItems = " + totalBooks);
            JSONArray itemsArray = rootObject.optJSONArray("items");
            if (itemsArray != null) {
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject item = itemsArray.optJSONObject(i);
                    JSONObject volumeInfo = item.optJSONObject("volumeInfo");
                    String title = volumeInfo.optString("title");
                    String subtitle = volumeInfo.optString("subtitle");
                    JSONArray authorsArray = volumeInfo.optJSONArray("authors");
                    String authors = "";
                    if (authorsArray != null) {
                        StringBuilder authorString = new StringBuilder();
                        authorString.append(authorsArray.optString(0));
                        if (authorsArray.length() > 1) {
                            for (int j = 1; j < authorsArray.length(); j++) {
                                authorString.append(", ");
                                authorString.append(authorsArray.optString(j));
                            }
                        }
                        authors = authorString.toString();
                    }
                    String publisher = volumeInfo.optString("publisher");
                    String publishDate = volumeInfo.optString("publishedDate");
                    String buyLink = volumeInfo.optString("infoLink");
                    if (subtitle.isEmpty()) {
                        bookList.add(new Book(title, authors, publisher, publishDate, buyLink));
                    } else {
                        bookList.add(new Book(title + " : " + subtitle, authors, publisher, publishDate, buyLink));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        return bookList;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        Log.i(LOG_TAG, "makeHttpRequest: " + url);
        String JSONResponse = "";
        if (url == null) {
            return JSONResponse;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                JSONResponse = readFromInputStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JSONResponse;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();

    }

    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    public static Long getTotalBooks() {
        return totalBooks;
    }
}
