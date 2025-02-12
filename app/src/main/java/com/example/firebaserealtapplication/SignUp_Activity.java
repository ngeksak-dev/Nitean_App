package com.example.firebaserealtapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp_Activity extends AppCompatActivity {

    Button btn_signup;
    EditText edt_name, edt_email, edt_password,edt_confirm_password;
    TextView logIn_Redirect;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        auth= FirebaseAuth.getInstance();
        btn_signup = findViewById(R.id.signupButton);
        edt_name = findViewById(R.id.nameEditText);
        edt_email = findViewById(R.id.emailEditText);
        edt_password = findViewById(R.id.passwordEditText);
        edt_confirm_password = findViewById(R.id.confirmPasswordEditText);
        logIn_Redirect = findViewById(R.id.loginTextView);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name,email,password,confirm_password;

                name=edt_name.getText().toString();
                email=edt_email.getText().toString();
                password=edt_password.getText().toString();

                if(email.isEmpty()){
                    edt_email.setError("Please enter email");
                }
                else if(password.isEmpty()){
                    edt_password.setError("Please enter password");
                }else {
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(SignUp_Activity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp_Activity.this,LogIn_Activity.class));
                            }else {
                                Toast.makeText(SignUp_Activity.this, "Sign up failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        logIn_Redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp_Activity.this,LogIn_Activity.class);
                startActivity(intent);
            }
        });

    }
}