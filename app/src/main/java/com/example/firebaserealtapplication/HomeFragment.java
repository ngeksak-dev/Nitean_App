package com.example.firebaserealtapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    RecyclerView recyclerView;
    DatabaseReference reference;
    List<Story> myStory;
    ValueEventListener eventListener;
    ImageView img_fav_icon;



    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View lview;
        lview = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView= lview.findViewById(R.id.recyclerView);
        img_fav_icon=lview.findViewById(R.id.heart);

        // Set up Process dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeFragment.this.getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.process_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set up recycler view
        myStory = new ArrayList<>();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(HomeFragment.this.getContext(), myStory,
        new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Sent data to detail fragment
                Bundle bundle = new Bundle();
                Story story = myStory.get(position);
                bundle.putSerializable("title", story.getTitle());
                bundle.putSerializable("content", story.getContent());
                bundle.putSerializable("image", story.getImage());
                bundle.putSerializable("vdo_url", story.getVdo_url());
                StoryDetailFragment detailFragment = new StoryDetailFragment();
                detailFragment.setArguments(bundle);

                // Replace fragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        LinearLayoutManager linearLayout = new LinearLayoutManager(HomeFragment.this.getContext());
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference("Nitean_Story");
        dialog.show();
        eventListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myStory.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Story story = itemSnapshot.getValue(Story.class);

                    story.setKey(itemSnapshot.getKey());
                    myStory.add(story);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();

            }
        });

        return lview;
    }

}