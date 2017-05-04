package com.example.marni.orderapp.Presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

/**
 * Created by Wallaard on 4-5-2017.
 */

public class AllergiesListviewClass extends AppCompatActivity{
    private ArrayList<Allergy> aAllergyList = new ArrayList<>();
    private ListView aAllergyListview;
    private InformationAllergiesListviewAdapter aInformationAllergiesListviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_allergies);

        aAllergyList = new AllergiesGenerator().getAllergies();
        aAllergyListview = (ListView) findViewById(R.id.allergies_listview);
        aInformationAllergiesListviewAdapter = new InformationAllergiesListviewAdapter(this,aAllergyList);
        aAllergyListview.setAdapter(aInformationAllergiesListviewAdapter);

    }

}
