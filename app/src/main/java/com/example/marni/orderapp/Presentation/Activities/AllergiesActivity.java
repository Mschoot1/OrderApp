package com.example.marni.orderapp.Presentation.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.DummyGenerator.AllergiesGenerator;
import com.example.marni.orderapp.Presentation.Adapters.AllergiesListviewAdapter;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

/**
 * Created by Wallaard on 4-5-2017.
 */

public class AllergiesActivity extends AppCompatActivity{
    private ArrayList<Allergy> aAllergyList = new ArrayList<>();
    private ListView aAllergyListview;
    private AllergiesListviewAdapter aAllergiesListviewAdapter;
    private AllergiesGenerator allergiesGenerator = new AllergiesGenerator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        aAllergyList = allergiesGenerator.getAllergies();

        aAllergyListview = (ListView) findViewById(R.id.allergies_listview);
        aAllergiesListviewAdapter = new AllergiesListviewAdapter(this,aAllergyList);
        aAllergyListview.setAdapter(aAllergiesListviewAdapter);

    }
}
