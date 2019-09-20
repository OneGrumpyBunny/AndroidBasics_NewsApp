package com.example.grumpybunny.newsapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter<BaseViewHolder>{

        private List<Event> EventList;

    public EventAdapter(List<Event> eventList)    {
        EventList = eventList;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.eventlist_item, parent, false));
    }

    // get the site of the arraylist
    @Override
    public int getItemCount() { return EventList.size(); }

    public void addItems(List<Event> events) {
        EventList.addAll(events);
    }

    public void notifyDatasetchanged() {
    }


    public class ViewHolder extends BaseViewHolder {

        // Bind views within the view holder
        @BindView(R.id.title) TextView eventTitle;
        @BindView(R.id.eventPubDate) TextView eventPubDate;
        @BindView(R.id.eventSection) TextView eventSection;
        @BindView(R.id.eventType) TextView eventType;
        @BindView(R.id.eventAuthor) TextView eventAuthor;

        public ViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            eventTitle.setText("");
            eventPubDate.setText("");
            eventSection.setText("");
            eventType.setText("");
            eventAuthor.setText("");
//            sectionHead.setText("");
        }

        /**  populate the views using methods in the custom class */
        public void onBind(int position) {
            super.onBind(position);

            final Event event = EventList.get(position);

            if (event.getTitle() != null) {
                eventTitle.setText(event.getTitle());
                openUrl(eventTitle, event.getUrl());
            }

            if (event.getPubDate() != null) {
                eventPubDate.setText(event.getPubDate());
            }

            if (event.getSection() != null) {

                String sectionColor = event.getSection();
                eventSection.setText(event.getSection());
                String titleColor = getSectionColor(sectionColor);
                eventSection.setBackgroundColor(Color.parseColor(titleColor));
            }

            if (event.getType() != null) {
                eventType.setText(event.getType());
            }

            // some of the news articles had "editorial" as the contributor name.
            // this function does not set the text for the author in this case.
            if (event.getAuthor() != null && event.getAuthor() != "Editorial") {
                eventAuthor.setText(event.getAuthor());
            }
        }

        /** set background color of the article band based on what type of article it is */

        private String getSectionColor(String sectionName) {
        String sectionColor;
        switch (sectionName) {
            case "":
            case "Sport":
                sectionColor = "#b71c1c";
                break;
            case "US news":
                sectionColor = "#311b92";
                break;
            case "Music":
                sectionColor = "#1a237e";
                break;
            case "Fashion":
                sectionColor = "#004d40";
                break;
            case "Society":
                sectionColor = "#006064";
                break;
            case "Environment":
                sectionColor = "#ff8f00";
                break;
            case "Football":
                sectionColor = "#4e342e";
                break;
            case "Stage":
                sectionColor = "#1b5e20";
                break;
            case "Business":
                sectionColor = "#0097a7";
                break;
            case "UK news":
                sectionColor = "#4a148c";
                break;
            case "Australian news":
                sectionColor = "#263238";
                break;
            default:
                sectionColor = "#000000";
                break;
        }
        return sectionColor;
    }

        /** set on click listener for title of news article.
         *
         * @param Url is the text view containing the url
         * @param url is the url of the article
         */
        private void openUrl(TextView Url, final String url) {
            Url.setOnClickListener(v -> {
                Uri webpage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                v.getContext().startActivity(intent);
            });
        }
    }

}