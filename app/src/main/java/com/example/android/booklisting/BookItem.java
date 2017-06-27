package com.example.android.booklisting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by izzystannett on 05/06/2017.
 */

public class BookItem implements Parcelable {

    //implement the required CREATOR field that implements the Parcelable.Creator interface
    public static final Parcelable.Creator<BookItem> CREATOR
            = new Parcelable.Creator<BookItem>() {
        public BookItem createFromParcel(Parcel in) {
            return new BookItem(in);
        }

        public BookItem[] newArray(int size) {
            return new BookItem[size];
        }
    };
    //set up some constants
    private static final String NO_IMAGE = "none";
    private static final String NO_TITLE = "none";
    private static final String NO_AUTHORS = "none";
    //intialise variables
    private String mTitle;
    private String mAuthor;
    private String mImageUrl;

    //set up constructor
    public BookItem(String imageUrl, String title, String author) {
        mImageUrl = imageUrl;
        mTitle = title;
        mAuthor = author;
    }

    //set up Parcelable constructor
    private BookItem(Parcel in) {
        mImageUrl = in.readString();
        mTitle = in.readString();
        mAuthor = in.readString();
    }

    //set getter methods
    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    //set up methods for testing presence of variables
    public boolean hasImage() {
        return mImageUrl != NO_IMAGE;
    }

    public boolean hasTitle() {
        return mTitle != NO_TITLE;
    }

    public boolean hasAuthor() {
        return mAuthor != NO_AUTHORS;
    }

    //set up parcel methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mImageUrl);
        parcel.writeString(mTitle);
        parcel.writeString(mAuthor);
    }
}
