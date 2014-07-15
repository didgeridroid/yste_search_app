package com.example.etsy.myetsy;

import java.util.ArrayList;

public class SearchResponse {
    public SearchResponse setCount(int count) {
        this.count = count;
        return this;
    }

    public int getCount() {
        return count;
    }

    private int count;

    public int getNextPage() {
        return nextPage;
    }

    public SearchResponse setNextPage(int nextPage) {
        this.nextPage = nextPage;
        return this;
    }

    private int nextPage;

    public String getError() {
        return error;
    }

    public SearchResponse setError(String error) {
        this.error = error;
        return this;
    }

    private String error;
}
