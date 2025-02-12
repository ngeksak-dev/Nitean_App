package com.example.firebaserealtapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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
    Uri uri;
    String img_url;
    ImageView upload_img;
    EditText upload_title, upload_content,upload_vdo_url;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View lview;


        Button upload_btn;

        lview = inflater.inflate(R.layout.fragment_upload, container, false);
        upload_img = lview.findViewById(R.id.uploadImage);
        upload_title = lview.findViewById(R.id.upload_title);
        upload_content = lview.findViewById(R.id.uploadCOntent);
        upload_vdo_url = lview.findViewById(R.id.upload_vdo_url);
        upload_btn = lview.findViewById(R.id.saveButton);


        //upload image from gallery to firebase storage
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == getActivity().RESULT_OK) {
                            Intent data = o.getData();
                            uri = data.getData();
                            upload_img.setImageURI(uri);
                        } else {
                            Toast.makeText(UploadFragment.this.getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );

        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedData();
            }
        });


        // Inflate the layout for this fragment
        return lview;
    }
        public void savedData () {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images")
                    .child(uri.getLastPathSegment());
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setView(R.layout.process_layout);
            AlertDialog dialog = builder.create();
            dialog.show();
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Uri urlImage = uriTask.getResult();
                    img_url = urlImage.toString();
                    uploadData();
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
        }
        public void uploadData () {

            String title = upload_title.getText().toString();
            String content = upload_content.getText().toString();
            String vdo_url = upload_vdo_url.getText().toString();


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Nitean_Story");
            String id = reference.push().getKey();
            Story myStory = new Story(id,title, content, img_url, vdo_url);
            reference.child(id).setValue(myStory).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UploadFragment.this.getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                upload_title.setText("");
                                upload_content.setText("");
                                upload_vdo_url.setText("");
                                upload_img.setImageResource(R.drawable.upload);
                            } else {
                                Toast.makeText(UploadFragment.this.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadFragment.this.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
}