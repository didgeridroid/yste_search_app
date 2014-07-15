package com.example.etsy.myetsy;

import java.util.ArrayList;

public final class ListingCache {

    private static ListingCache _instance;
    private ArrayList<Listing> listings = new ArrayList<Listing>();

    private ListingCache() {
    }

    public static ListingCache getInstance() {
        if (_instance == null) {
            _instance = new ListingCache();
        }
        return _instance;
    }

    public void addListing(Listing listing) {
        listings.add(listing);
    }

    public int getListingCount() {
        return listings.size();
    }

    public Listing getListing(int i) {
        if (i < listings.size()) {
            return listings.get(i);
        }
        return null;
    }

    public void clearResults() {
        listings.clear();
    }
}
