package com.example.grumpybunny.newsapp;

/** custom class Event used to hold values parsed from
 * the JSON stream
 */

public class Event {

    private String title;

    private String pubDate;

    private String section;

    private String url;

    private String type;

    private String author;

    public Event(String Title, String PubDate, String Section, String Url, String Type, String Author) {
        title = Title;
        pubDate = PubDate;
        section = Section;
        url = Url;
        type = Type;
        author = Author;
    }

    public String getTitle() { return title; }

    public String getPubDate() { return pubDate; }

    public String getSection() { return section; }

    public String getUrl() { return url; }

    public String getType() { return type; }

    public String getAuthor() { return author; }
}
