package com.example.marni.orderapp.Presentation;

import java.util.ArrayList;

/**
 * Created by Wallaard on 4-5-2017.
 */

public class AllergiesGenerator {
        public ArrayList<Allergy> getAllergies() {
            return allergies;
        }

        private ArrayList<Allergy> allergies = new ArrayList<>();

        public AllergiesGenerator() {
            Allergy a1 = new Allergy("eggs","This product contains eggs");
            allergies.add(a1);
            Allergy a2 = new Allergy("wheat","This product contains gluten");
            allergies.add(a2);
        }
    }


