package com.example.beender;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class CardStackCallback extends DiffUtil.Callback {

    private List<ItemModel> oldItem, newItem;

    public CardStackCallback(List<ItemModel> oldItem, List<ItemModel> newItem) {
        this.oldItem = oldItem;
        this.newItem = newItem;
    }

    @Override
    public int getOldListSize() {
        return oldItem.size();
    }

    @Override
    public int getNewListSize() {
        return newItem.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItem.get(oldItemPosition).getImage() == newItem.get(newItemPosition).getImage();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItem.get(oldItemPosition) == newItem.get(newItemPosition);
    }
}