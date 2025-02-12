package com.example.firebaserealtapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditUserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditUserInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditUserInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditUserInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditUserInfoFragment newInstance(String param1, String param2) {
        EditUserInfoFragment fragment = new EditUserInfoFragment();
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
    EditText usrName;
    ShapeableImageView usrImg;
    Uri uri;
    Button save;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View lView;
        lView=inflater.inflate(R.layout.fragment_edit_user_info, container, false);

        usrName=lView.findViewById(R.id.uploadName);
        usrImg=lView.findViewById(R.id.uploadImage);
        save=lView.findViewById(R.id.saveButton);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Guest");
        String userImg = sharedPreferences.getString("userImg", null);
        if(userName!="Guest") {
            usrName.setText(userName);
        }
        if(userImg == null){
            Glide.with(getContext()).load(R.drawable.avatar).into(usrImg);
        }else{
            Glide.with(getContext()).load(userImg).into(usrImg);
        }

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        usrImg.setOnClickListener(v ->openImagePicker());
        save.setOnClickListener(v -> saveUserInfo());
        return lView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            uri=data.getData();
            usrImg.setImageURI(uri);
        }
    }

    private void openImagePicker(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Pick Image"),1);

    }
    private void saveUserInfo(){
        String name=usrName.getText().toString();
        if(name.isEmpty()){
            usrName.setError("Name is required");
            return;
        }
        if(uri!=null){
            uploadImageandInfo(name);
            restartApp();

        }else {
            saveInfo(name,null);
            restartApp();
        }
    }
    private void uploadImageandInfo(String name){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("UserImg/"+userId+".jpg");
        ref.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                saveInfo(name,url);
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            });
        });
    }
    private void saveInfo(String name,String uri){
        User user = new User(name,uri);
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        userref.setValue(user).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(),"User saved successfully",Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
        });
        }
    private void restartApp() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
    }

