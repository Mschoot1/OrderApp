package com.example.marni.orderapp.presentation.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.marni.orderapp.domain.Category;
import com.example.marni.orderapp.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends BaseAdapter {

    private final String tag = getClass().getSimpleName();

    private LayoutInflater layoutInflater;
    private ArrayList<Category> categories;

    public CategoriesAdapter(LayoutInflater layoutInflater, List<Category> categories) {
        this.layoutInflater = layoutInflater;
        this.categories = (ArrayList<Category>) categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Log.i(tag, "Getting item: " + position);

        View view = convertView;
        final ViewHolder viewHolder;

        if (view == null) {

            view = layoutInflater.inflate(R.layout.listview_item_category, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);

            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }
        Category c = categories.get(position);
        viewHolder.textViewTitle.setText(c.getCategoryName());

        return view;
    }

    private static class ViewHolder {
        TextView textViewTitle;
    }
}
