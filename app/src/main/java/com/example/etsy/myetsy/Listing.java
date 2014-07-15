package com.example.etsy.myetsy;

public class Listing {
    public String getImageUrl() {
        return mImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public Listing(String title, String imageUrl) {
        mImageUrl = imageUrl;
        mTitle = title;
    }

    private String mTitle;
    private String mImageUrl;

}
