package com.example.marni.orderapp.Presentation.Fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.auth0.android.jwt.JWT;
import com.example.marni.orderapp.DataAccess.Category.CategoriesGetTask;
import com.example.marni.orderapp.Domain.Category;
import com.example.marni.orderapp.Presentation.Adapters.CategoriesAdapter;
import com.example.marni.orderapp.R;

import java.util.ArrayList;

import static com.example.marni.orderapp.Presentation.Activities.LogInActivity.JWT_STR;
import static com.example.marni.orderapp.Presentation.Adapters.CategoriesAdapter.*;

public class CategoryFragment extends DialogFragment implements CategoriesGetTask.OnCategoryAvailable {

    private final String TAG = getClass().getSimpleName();

    private CategoriesAdapter adapter;
    private ArrayList<Category> categories = new ArrayList<>();
    private Activity activity;
    private View view;

    public CategoryFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CategoryFragment newInstance(String jwt) {
        CategoryFragment frag = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(JWT_STR, jwt);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        getCategories("https://mysql-test-p4.herokuapp.com/product/categories", getArguments().getString(JWT_STR));
        activity = getActivity();
        final OnItemSelected listener = (OnItemSelected) activity;
        ListView listView = (ListView) view.findViewById(R.id.dialog_list_view);

        adapter = new CategoriesAdapter(this, activity.getLayoutInflater(), categories);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemSelected(categories.get(position).getCategoryId());
                dismiss();
            }
        });
        getDialog().setTitle("Categories");
        listView.requestFocus();
    }

    public void getCategories(String apiUrl, String jwt) {
        CategoriesGetTask task = new CategoriesGetTask(view, this);
        String[] urls = new String[]{apiUrl, jwt};
        task.execute(urls);
    }

    @Override
    public void onCategoryAvailable(Category category) {
        categories.add(category);
        adapter.notifyDataSetChanged();
    }

    public interface OnItemSelected {
        void onItemSelected(int i);
    }
}




