package com.example.firebaserealtapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter {
    Context mContext;
    List<Story> mData;
   private OnItemClickListener onItemClickListener;
   private String userID;


   public interface OnItemClickListener {
        void onItemClick(View view, int position);
   }

    public RecyclerViewAdapter(Context mContext, List<Story> mData, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.onItemClickListener = onItemClickListener;
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item_layout, parent, false);
        return new StoryViewHolder(lView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Story lStory = isSearching ? searchStoriesList.get(position) : mData.get(position);
        Story lStory = mData.get(position);
        StoryViewHolder holder1 = (StoryViewHolder) holder;
        checkFavStatus(lStory.getId(),holder1.favourite__icon);
        holder1.favourite__icon.setOnClickListener(v -> {
            toggleFavString(lStory.getId(),holder1.favourite__icon);
        });

        holder1.bind(lStory);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    private void checkFavStatus(String storyId,ImageView imgFav){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userID).child("Favourite").child(storyId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    imgFav.setImageResource(R.drawable.baseline_favorite_red);
                }else{
                    imgFav.setImageResource(R.drawable.baseline_favorite);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add story to favorite
    private void toggleFavString(String storyId,ImageView imgFav){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userID).child("Favourite").child(storyId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    databaseReference.removeValue();
                    imgFav.setImageResource(R.drawable.baseline_favorite);
                }else{
                    databaseReference.setValue(true);
                    imgFav.setImageResource(R.drawable.baseline_favorite_red);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // check story is favorited

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView storyImage;
        TextView title;
        ImageView favourite__icon;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            storyImage = itemView.findViewById(R.id.card_Image);
            title = itemView.findViewById(R.id.card_Title);
            favourite__icon = itemView.findViewById(R.id.heart);
        }
        public void bind(Story story){
            Glide.with(mContext).load(story.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(storyImage);
            title.setText(story.getTitle());

            itemView.setOnClickListener(v -> {
                if(onItemClickListener!= null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        onItemClickListener.onItemClick(v, position);
                    }
                }
            });

        }


    }
    private void fetchFavoriteStory(){
       String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Favourite");

       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               List<String> favouriteStoryIds = new ArrayList<>();
               for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                   String storyId = dataSnapshot.getKey();
                   favouriteStoryIds.add(storyId);
               }
               List<Story> favouriteStories = new ArrayList<>();
               for(Story story : mData){
                   if(favouriteStoryIds.contains(story.getId())){
                       favouriteStories.add(story);
                   }
               }
               notifyDataSetChanged();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();

           }
       });
    }
}
