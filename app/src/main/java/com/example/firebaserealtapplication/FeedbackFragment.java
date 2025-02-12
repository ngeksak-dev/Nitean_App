package com.example.firebaserealtapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
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

    View lView;
    EditText edt_feedback;
    Button btn_submit;
    DatabaseReference ref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lView = inflater.inflate(R.layout.fragment_feedback, container, false);
        edt_feedback = lView.findViewById(R.id.feedback);
        btn_submit = lView.findViewById(R.id.btn_submit);

        ref = FirebaseDatabase.getInstance().getReference("feedback");

        //get username

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFeedback();
            }
        });


        return lView;
    }
    private void uploadFeedback() {
        String feedback = edt_feedback.getText().toString().trim();
        if(feedback.isEmpty()) {
            edt_feedback.setError("Please enter your feedback");
            edt_feedback.requestFocus();
            return;
        }
        String feedbackId = ref.push().getKey();
        if(feedbackId != null) {
            ref.child(feedbackId).child("feedback").setValue(feedback)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                edt_feedback.setText("");
                                Toast.makeText(getActivity(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }else {
            Toast.makeText(getActivity(), "Failed to submit feedback", Toast.LENGTH_SHORT).show();
        }
        }
}