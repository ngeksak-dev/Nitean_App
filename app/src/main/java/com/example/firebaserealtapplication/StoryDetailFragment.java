package com.example.firebaserealtapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoryDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StoryDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoryDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoryDetailFragment newInstance(String param1, String param2) {
        StoryDetailFragment fragment = new StoryDetailFragment();
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

    TextView tittle, content;
    ImageView img,share_icon,search_icon;
    AppCompatActivity mainActivity;
    String vdo_url;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View lview;
        lview = inflater.inflate(R.layout.fragment_story_detail, container, false);
        content = lview.findViewById(R.id.story_content);
        img = lview.findViewById(R.id.story_img);
        FloatingActionButton fab = lview.findViewById(R.id.btn_vdo);
        mainActivity = (AppCompatActivity) requireActivity();

        ActionBar supportActionBar = mainActivity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(" ");
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            Toolbar toolbar1 = mainActivity.findViewById(R.id.toolbar);
            if (toolbar1 != null) {
                tittle = toolbar1.findViewById(R.id.toolbar_title);
                share_icon = toolbar1.findViewById(R.id.share_icon);
                search_icon = toolbar1.findViewById(R.id.search_icon);
                search_icon.setVisibility(View.INVISIBLE);
                share_icon.setVisibility(View.VISIBLE);
            }
        }
        if (getArguments() != null) {
            tittle.setText(getArguments().getString("title"));
            content.setText(getArguments().getString("content"));
            vdo_url = getArguments().getString("vdo_url");
            //display story image
            Glide.with(getContext()).load(getArguments().getString("image")).error(R.drawable.app_logo).into(img);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoFragment videoFragment = new VideoFragment();
                // Send story title
                Bundle bundle = new Bundle();
                bundle.putString("title", tittle.getText().toString());
                bundle.putString("vdo_url", vdo_url);

                videoFragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, videoFragment)
                        .addToBackStack(null)
                        .commit();

//                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new VideoFragment()).addToBackStack(null).commit();
            }
        });

        share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null) {
                    String storyTitle = getArguments().getString("title");
                    String imageUrl = getArguments().getString("image");
                    String shareText = "Check out this story: \n" + storyTitle + "\n"
                            +"\nDownload Nitean for more intersting story !\n";


                    Glide.with(getContext())
                            .asBitmap()
                            .load(imageUrl)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                                    try {
                                        // Save the image to the cache directory
                                        File cachePath = new File(getContext().getCacheDir(), "images");
                                        cachePath.mkdirs();
                                        File file = new File(cachePath, "shared_image.png");
                                        FileOutputStream out = new FileOutputStream(file);
                                        resource.compress(Bitmap.CompressFormat.PNG, 100, out);
                                        out.close();

                                        // Get the URI for the file
                                        Uri uri = FileProvider.getUriForFile(
                                                getContext(),
                                                getContext().getPackageName() + ".fileprovider",
                                                file
                                        );

                                        // Create the share intent
                                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                        shareIntent.setType("image/*");
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                        // Start the share intent
                                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                                        Toast.makeText(getContext(), "Story shared", Toast.LENGTH_SHORT).show();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    Log.d("StoryDetailFragment", "Bitmap load cleared");
                                }
                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    super.onLoadFailed(errorDrawable);
                                    Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Log.e("StoryDetailFragment", "Arguments are null in share_icon click");
                }
            }
        });


        // Inflate the layout for this fragment
        return lview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActionBar supportActionBar = mainActivity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(" ");
            // Enable the Up button
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            Toolbar toolbar1 = mainActivity.findViewById(R.id.toolbar);
            tittle = toolbar1.findViewById(R.id.toolbar_title);
            tittle.setText(R.string.app_name);
            search_icon.setVisibility(View.VISIBLE);
            share_icon.setVisibility(View.INVISIBLE);
        }
    }
}