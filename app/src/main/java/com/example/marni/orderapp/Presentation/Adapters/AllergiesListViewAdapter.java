package com.example.marni.orderapp.Presentation.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

public class AllergiesListViewAdapter extends BaseAdapter {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Allergy> allergies;

    public AllergiesListViewAdapter(Context context, LayoutInflater layoutInflater, ArrayList<Allergy> allergies) {

        Log.i(TAG, "Size: " + allergies.size());

        this.context = context;
        this.layoutInflater = layoutInflater;
        this.allergies = allergies;
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

        final ViewHolder viewHolder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.listview_item_allergies, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textViewInformation = (TextView) convertView.findViewById(R.id.textViewAllergyTitle);
            viewHolder.imageViewAllergyIcon = (ImageView) convertView.findViewById(R.id.imageViewAllergyIcon);

            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        Allergy allergy = allergies.get(position);

        viewHolder.textViewInformation.setText(allergy.getInformationtext());

        String imageName = "@mipmap/" + String.valueOf(allergy.getImage_url());
        int imageId = context.getResources().getIdentifier(imageName, null, context.getPackageName());
        viewHolder.imageViewAllergyIcon.setImageResource(imageId);

        Log.i(TAG, "Image url: " + imageName );
        Log.i(TAG, "Image id: " + imageId );

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewInformation;
        ImageView imageViewAllergyIcon;
    }
}
//

