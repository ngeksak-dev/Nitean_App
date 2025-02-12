package com.example.firebaserealtapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteStoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteStoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoriteStoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteStoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteStoryFragment newInstance(String param1, String param2) {
        FavoriteStoryFragment fragment = new FavoriteStoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    RecyclerView favRecyclerView;
    RecyclerViewAdapter favAdapter;
    List<Story> favStory;
    List<Story> allStory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.fragment_favorite_story, container, false);
        favRecyclerView = lView.findViewById(R.id.recyclerView);
        favRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favStory = new ArrayList<>();
        favAdapter = new RecyclerViewAdapter(getContext(), favStory,new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                // Send the selected story to the detail fragment
                Bundle bundle = new Bundle();
                Story story = favStory.get(position); // Use favStory instead of allStory
                bundle.putSerializable("title", story.getTitle());
                bundle.putSerializable("content", story.getContent());
                bundle.putSerializable("image", story.getImage());
                bundle.putSerializable("vdo_url", story.getVdo_url());
                StoryDetailFragment detailFragment = new StoryDetailFragment();
                detailFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        fetchallStory();
        favRecyclerView.setAdapter(favAdapter);
        return lView;
    }
    private void fetchallStory() {
        DatabaseReference storyRef = FirebaseDatabase.getInstance().getReference("Nitean_Story");
        storyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allStory = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Story story = dataSnapshot.getValue(Story.class);
                    if (story != null) {
                        story.setKey(dataSnapshot.getKey());
                        allStory.add(story);
                    }
                }
                fetchFavoriteStory(); // Fetch favorite stories after all stories are loaded
                Log.d("FetchAllStories", "Fetched " + allStory.size() + " stories");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch stories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch favorite stories based on the user's favorite IDs
    private void fetchFavoriteStory(){
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference favRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId).child("Favourite");
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> favIds = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String storyId = dataSnapshot.getKey();
                    favIds.add(storyId);
                }
                filterFavoriteStory(favIds);
                Log.d("FetchFavoriteStory", "Fetched " + favIds.size() + " favorite IDs: " + favIds);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching favorite stories", Toast.LENGTH_SHORT).show();

            }
        });
    }

    // Filter favorite stories based on the user's favorite IDs
    private void filterFavoriteStory(List<String> favIds) {
        favStory.clear(); // Clear the list before adding new items
        for (Story story : allStory) {
            if (favIds.contains(story.getId())) {
                favStory.add(story);
            }
        }
        favAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
        Log.d("FilterFavoriteStory", "Filtered " + favStory.size() + " favorite stories");
    }
}