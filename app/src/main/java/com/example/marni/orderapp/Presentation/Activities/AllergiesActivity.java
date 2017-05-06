package com.example.marni.orderapp.Presentation.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.marni.orderapp.Domain.Allergy;
import com.example.marni.orderapp.DummyGenerator.AllergiesGenerator;
import com.example.marni.orderapp.Presentation.Adapters.AllergiesListviewAdapter;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

/**
 * Created by Wallaard on 4-5-2017.
 */

public class AllergiesActivity extends AppCompatActivity {

    private AllergiesGenerator allergiesGenerator = new AllergiesGenerator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        ArrayList<Allergy> allergies = allergiesGenerator.getAllergies();

        ListView listViewAllergies = (ListView) findViewById(R.id.allergies_listview);
        BaseAdapter allergiesAdapter = new AllergiesListviewAdapter(this, getLayoutInflater(), allergies);
        listViewAllergies.setAdapter(allergiesAdapter);
    }
}
