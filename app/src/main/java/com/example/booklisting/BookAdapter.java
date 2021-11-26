package com.example.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    private Context context;

    public BookAdapter(@NonNull Context context, @NonNull List<Book> Books) {
        super(context, 0, Books);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView titleTextview = listItemView.findViewById(R.id.title);
        titleTextview.setText(currentBook.getTitle());

        TextView authorTextview = listItemView.findViewById(R.id.authors);
        if (currentBook.getAuthors().isEmpty()) {
            authorTextview.setVisibility(View.GONE);
        }
        authorTextview.setText(currentBook.getAuthors());

        TextView publisherTextview = listItemView.findViewById(R.id.publisher);
        if (currentBook.getPublisher().isEmpty()) {
            publisherTextview.setVisibility(View.GONE);
        }
        publisherTextview.setText(currentBook.getPublisher());

        TextView publishDateTextview = listItemView.findViewById(R.id.publish_date);
        if (currentBook.getPublishDate().isEmpty()) {
            publishDateTextview.setVisibility(View.GONE);
        }
        publishDateTextview.setText(currentBook.getPublishDate());

        if (currentBook.getBuyLink() != "") {
            listItemView.setOnClickListener(view -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentBook.getBuyLink()));
                context.startActivity(i);
            });
        }

        return listItemView;
    }
}
