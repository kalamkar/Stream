package com.houseparty.stream;

import android.support.annotation.BoolRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abhi on 8/22/17.
 */

public class ListAdapter extends RecyclerView.Adapter {
    private final List<Model.Item> viewData = new ArrayList(Config.NUM_LIST_ITEMS);

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView fromNameView;
        private TextView toNameView;
        private TextView timestampView;
        private TextView friendsView;

        ItemViewHolder(View view) {
            super(view);
            fromNameView = (TextView) view.findViewById(R.id.fromName);
            toNameView = (TextView) view.findViewById(R.id.toName);
            timestampView = (TextView) view.findViewById(R.id.timestamp);
            friendsView = (TextView) view.findViewById(R.id.areFriends);
        }
    }

    void addItem(Model.Item item) {
        // Assuming the items are ordered by timestamp already, otherwise use SortedList.
        viewData.add(0, item);
        if (viewData.size() > Config.NUM_LIST_ITEMS) {
            viewData.remove(Config.NUM_LIST_ITEMS);
        }
    }

    void clearNonFriends() {
        for (int i = viewData.size() - 1; i >= 0; i--) {
            if (!viewData.get(i).areFriends) {
                viewData.remove(i);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView itemView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < viewData.size()) {
            Model.Item item = viewData.get(position);
            ((ItemViewHolder) holder).fromNameView.setText(item.from.name);
            ((ItemViewHolder) holder).toNameView.setText(item.to.name);
            ((ItemViewHolder) holder).timestampView.setText(
                    Config.DATE_FORMAT.format(new Date(item.timestamp)));
            ((ItemViewHolder) holder).friendsView.setText(Boolean.toString(item.areFriends));
        }
    }

    @Override
    public int getItemCount() {
        return viewData.size();
    }
}
