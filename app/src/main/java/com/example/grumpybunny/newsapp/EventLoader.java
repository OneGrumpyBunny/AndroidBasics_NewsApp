package com.example.grumpybunny.newsapp;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class EventLoader  extends AsyncTaskLoader {
    private String Url;

    public EventLoader(Context context, String url) {
        super(context);
        Url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Event> loadInBackground() {
        if (Url == null) {
            return null;
        }
        List<Event> news = QueryUtils.fetchEventData(Url);
        return news;
    }
}