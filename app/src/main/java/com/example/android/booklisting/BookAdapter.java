package com.example.android.booklisting;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.R.attr.resource;

/**
 * Created by izzystannett on 07/06/2017.
 */

public class BookAdapter extends ArrayAdapter<BookItem> {

    public BookAdapter(Activity context, ArrayList<BookItem> books) {
        super(context, 0, books);
    }

    //override the getView method so we are not limited to passing just one TextView in
    //to the ListView

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        BookItem book = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        //set up view objects
        TextView bookTitle = (TextView) view.findViewById(R.id.book_title);
        TextView bookAuthor = (TextView) view.findViewById(R.id.book_author);
        ImageView bookImage = (ImageView) view.findViewById(R.id.book_image);

        //test for presence of attributes, and show in list view if present
        if (book.hasImage()) {

            //convert image url into actual image
            Picasso.with(getContext())
                    .load(book.getmImageUrl())
                    .into(bookImage);

            bookImage.setVisibility(View.VISIBLE);
        } else {
            bookImage.setVisibility(View.GONE);
        }

        if (book.hasTitle()) {
            bookTitle.setText(book.getmTitle());
            bookTitle.setVisibility(View.VISIBLE);
        } else {
            bookTitle.setVisibility(View.GONE);
        }

        if (book.hasAuthor()) {
            bookAuthor.setText(book.getmAuthor());
            bookAuthor.setVisibility(View.VISIBLE);
        } else {
            bookAuthor.setVisibility(View.GONE);
        }

        return view;
    }
}
