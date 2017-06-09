package com.example.marni.orderapp.presentation.adapters;

import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.marni.orderapp.domain.Category;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

public class CategoriesAdapter extends BaseAdapter {

    private final String TAG = getClass().getSimpleName();

    private DialogFragment dialogFragment;
    private LayoutInflater layoutInflater;
    private ArrayList<Category> categories;

    public CategoriesAdapter(DialogFragment dialogFragment, LayoutInflater layoutInflater, ArrayList<Category> categories) {
        this.dialogFragment = dialogFragment;
        this.layoutInflater = layoutInflater;
        this.categories = categories;
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

        Log.i(TAG, "Getting item: " + position);

        final ViewHolder viewHolder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.listview_item_category, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
        Category c = categories.get(position);
        viewHolder.textViewTitle.setText(c.getCategoryName());

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewTitle;
    }
}
