    package com.example.firebaserealtapplication;

    import android.app.Activity;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.graphics.drawable.ColorDrawable;
    import android.net.Uri;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.util.Patterns;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.activity.result.ActivityResult;
    import androidx.activity.result.ActivityResultCallback;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.bumptech.glide.Glide;
    import com.developer.gbuttons.GoogleSignInButton;
    import com.google.android.gms.auth.api.identity.BeginSignInRequest;
    import com.google.android.gms.auth.api.identity.SignInClient;
    import com.google.android.gms.auth.api.identity.SignInCredential;
    import com.google.android.gms.auth.api.signin.GoogleSignIn;
    import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
    import com.google.android.gms.auth.api.signin.GoogleSignInClient;
    import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
    import com.google.android.gms.auth.api.signin.SignInAccount;
    import com.google.android.gms.common.api.ApiException;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthCredential;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.auth.GoogleAuthProvider;
    import com.google.firebase.ktx.Firebase;

    import java.util.Objects;

    public class LogIn_Activity extends AppCompatActivity {

        EditText emailEditText, passwordEditText;
        Button loginButton;
        GoogleSignInButton googleButton;
        TextView signupRedirectText, forgotPassword;
        FirebaseAuth mAuth;

        GoogleSignInClient mGoogleSignInClient;
        GoogleSignInOptions gOptions;


        // Check login state
        @Override
        protected void onStart() {
            super.onStart();
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

            // Check login state
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            String userEmail = sharedPreferences.getString("userEmail", "");
            String userName = sharedPreferences.getString("userName", "");

            if (isLoggedIn) {
                // User is logged in, navigate to the main activity
                Intent intent = new Intent(LogIn_Activity.this, MainActivity.class);
                intent.putExtra("email", userEmail);
                intent.putExtra("username", userName);
                startActivity(intent);
                finish();
            }

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_log_in);

            emailEditText=findViewById(R.id.emailEditText);
            passwordEditText=findViewById(R.id.passwordEditText);
            loginButton=findViewById(R.id.loginButton);
            signupRedirectText=findViewById(R.id.signUpTextView);
            forgotPassword=findViewById(R.id.forgotPasswordTextView);
            googleButton=findViewById(R.id.googleBtn);
            mAuth = FirebaseAuth.getInstance();


            //Login Button Logic
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    if(!email.isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        if(!password.isEmpty()){
                            mAuth.signInWithEmailAndPassword(email,password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override

                                        public void onSuccess(AuthResult authResult) {
                                            FirebaseUser user = mAuth.getInstance().getCurrentUser();
                                            if (user != null) {
                                                String usrname = user.getDisplayName();
                                                String email = user.getEmail();

                                                // Save user data to SharedPreferences
                                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                                // Save user email and login state
                                                editor.putBoolean("isLoggedIn", true);
                                                editor.putString("userEmail", user.getEmail());
                                                editor.apply(); // Use apply() for asynchronous saving

                                                Intent intent = new Intent(LogIn_Activity.this, MainActivity.class);
                                                intent.putExtra("username", usrname);
                                                intent.putExtra("email", email);
                                                startActivity(intent);
                                                finish();
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LogIn_Activity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else {
                            passwordEditText.setError("Password cannot be empty");
                        }
                    } else if(email.isEmpty()){
                        emailEditText.setError("Email cannot be empty");
                    } else {
                        emailEditText.setError("Please enter a valid email");

                    }
                }
                });

            //Redirect to Signup Activity
            signupRedirectText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LogIn_Activity.this, SignUp_Activity.class));
                }
            });

            //Redirect to Forgot Password Dialog
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Forgot Password Dialog Box
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogIn_Activity.this);
                    View dailogView = getLayoutInflater().inflate(R.layout.dialog_forgot_layout, null);
                    EditText emailEditText = dailogView.findViewById(R.id.email_box);
                    builder.setView(dailogView);
                    AlertDialog dialog = builder.create();

                    //Password Reset Button Logic
                    dailogView.findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String usremail = emailEditText.getText().toString();

                            if (TextUtils.isEmpty(usremail) || !Patterns.EMAIL_ADDRESS.matcher(usremail).matches())
                            {
                                Toast.makeText(LogIn_Activity.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mAuth.sendPasswordResetEmail(usremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LogIn_Activity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(LogIn_Activity.this, "Unable to send", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                    //Cancel Button Logic
                    dailogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    dialog.show();
                }
            });


            //Google Sign In
            gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gOptions);
            mAuth = FirebaseAuth.getInstance();

            GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (gAccount != null) {
                finish();
                Intent intent = new Intent(LogIn_Activity.this, MainActivity.class);
                startActivity(intent);
            }
            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK && o.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(o.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);

                            // Save login state and user data to SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("userName", account.getDisplayName());
                            editor.putString("userEmail", account.getEmail());
                            editor.apply();

                            firebaseAuthWithGoogle(account.getIdToken());

                        } catch (ApiException e) {
                            Toast.makeText(LogIn_Activity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            //Google Sign In Button Logic
            googleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = mGoogleSignInClient.getSignInIntent();
                    activityResultLauncher.launch(intent);
                }
            });


        }

        //Google Sign In
        private void firebaseAuthWithGoogle(String idToken) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Navigate to the main activity
                                Intent intent = new Intent(LogIn_Activity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                // Sign in failed
                                Toast.makeText(LogIn_Activity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }