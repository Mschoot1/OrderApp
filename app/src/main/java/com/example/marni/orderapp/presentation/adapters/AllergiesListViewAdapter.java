package com.example.marni.orderapp.presentation.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marni.orderapp.domain.Allergy;
import com.example.marni.orderapp.R;

import java.util.ArrayList;
import java.util.List;

public class AllergiesListViewAdapter extends BaseAdapter {

    private final String tag = getClass().getSimpleName();

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Allergy> allergies;

    public AllergiesListViewAdapter(Context context, LayoutInflater layoutInflater, List<Allergy> allergies) {

        Log.i(tag, "Size: " + allergies.size());

        this.context = context;
        this.layoutInflater = layoutInflater;
        this.allergies = (ArrayList<Allergy>) allergies;
    }

    @Override
    public int getCount() {
        return allergies.size();
    }

    @Override
    public Object getItem(int position) {
        return allergies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder viewHolder;

        if (view == null) {

            view = layoutInflater.inflate(R.layout.listview_item_allergies, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewInformation = (TextView) view.findViewById(R.id.textViewAllergyTitle);
            viewHolder.imageViewAllergyIcon = (ImageView) view.findViewById(R.id.imageViewAllergyIcon);

            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }

        Allergy allergy = allergies.get(position);

        viewHolder.textViewInformation.setText(allergy.getInformationText());

        String imageName = "@mipmap/" + allergy.getImageUrl();
        int imageId = context.getResources().getIdentifier(imageName, null, context.getPackageName());
        viewHolder.imageViewAllergyIcon.setImageResource(imageId);

        Log.i(tag, "Image url: " + imageName );
        Log.i(tag, "Image id: " + imageId );

        return view;
    }

    private static class ViewHolder {
        TextView textViewInformation;
        ImageView imageViewAllergyIcon;
    }
}
//


