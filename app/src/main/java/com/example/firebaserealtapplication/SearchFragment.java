package com.example.firebaserealtapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    List<Story> search_list;
    RecyclerViewAdapter search_adapter;
    DatabaseReference databaseReference;
    SearchView search_view;
    AppCompatActivity mainActivity;
    TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = lView.findViewById(R.id.search_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainActivity = (AppCompatActivity) requireActivity();

        // Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Nitean_Story");
        search_list = new ArrayList<>();

        // Set up Toolbar
        ActionBar supportActionBar = mainActivity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("Search");
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            Toolbar toolbar1 = mainActivity.findViewById(R.id.toolbar);
            search_view = toolbar1.findViewById(R.id.search_view);
            title = toolbar1.findViewById(R.id.toolbar_title);
            title.setVisibility(View.INVISIBLE);
        }

        // Search View Listeners
        search_view.setOnClickListener(v -> {
            search_view.setIconified(false);
            search_view.requestFocus();
        });

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        // Initialize Adapter
        search_adapter = new RecyclerViewAdapter(getContext(), search_list, (view, position) -> {
            // Navigate to StoryDetailFragment with selected story
            Bundle bundle = new Bundle();
            Story story = search_list.get(position);
            bundle.putSerializable("title", story.getTitle());
            bundle.putSerializable("content", story.getContent());
            bundle.putSerializable("image", story.getImage());
            bundle.putSerializable("vdo_url",story.getVdo_url());
            StoryDetailFragment detailFragment = new StoryDetailFragment();
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(search_adapter);

        return lView;
    }

    @Override
    public void onStop() {
        super.onStop();
        search_list.clear();
        search_adapter.notifyDataSetChanged();
        search_view.setQuery("", false);
        search_view.clearFocus();
        title.setVisibility(View.VISIBLE);
        search_view.setVisibility(View.INVISIBLE);
    }

    private void performSearch(String searchText) {
        String searchTextNormalized = Normalizer.normalize(searchText, Normalizer.Form.NFC);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                search_list.clear(); // Clear list before adding results
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Story story = dataSnapshot.getValue(Story.class);
                    if (story != null) {
                        String titleNormalized = Normalizer.normalize(story.getTitle(), Normalizer.Form.NFC);
                        if (titleNormalized.contains(searchTextNormalized)) {
                            search_list.add(story);
                        }
                    }
                }

                if (search_list.isEmpty()) {
                    Toast.makeText(getContext(), "No match found", Toast.LENGTH_SHORT).show();
                }

                search_adapter.notifyDataSetChanged(); // Notify adapter of changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
