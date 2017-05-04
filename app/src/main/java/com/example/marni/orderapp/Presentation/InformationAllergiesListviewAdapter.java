package com.example.marni.orderapp.Presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

/**
 * Created by Wallaard on 4-5-2017.
 */

public class InformationAllergiesListviewAdapter extends ArrayAdapter<Allergy> {
        public InformationAllergiesListviewAdapter(Context context,ArrayList<Allergy> allergies){
            super(context,0,allergies);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater allergyinflator = LayoutInflater.from(getContext());
            View customView = allergyinflator.inflate(R.layout.custom_listview_information_allergies,parent,false);

            Allergy allergy = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_listview_information_allergies, parent, false);
            }

            TextView allergyinformation = (TextView) convertView.findViewById(R.id.information_allergies_text);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.allergies_icon);

            allergyinformation.setText(allergy.getInformationtext());

            String imageName = "@mipmap/"+String.valueOf(allergy.getImageid());
            int imageId = getContext().getResources().getIdentifier(imageName, null, getContext().getPackageName());
            imageView.setImageResource(imageId);

            return convertView;
        }
}


