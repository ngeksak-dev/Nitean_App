package com.example.firebaserealtapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.firebaserealtapplication.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView txt_userName;
    GoogleSignInClient gClient;
    GoogleSignInOptions gOptions;
    DrawerLayout drawerLayout;
    NavigationView mynavigationView;
    Toolbar toolbar;
    TextView lblTite;
    ImageView search_icon,shareicon;
    SearchView search_view;
    ShapeableImageView userImage;
    DatabaseReference ref;
    String userId;
    String myuserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        lblTite= toolbar.findViewById(R.id.toolbar_title);
        search_icon = toolbar.findViewById(R.id.search_icon);
        shareicon = toolbar.findViewById(R.id.share_icon);
        search_view = toolbar.findViewById(R.id.search_view);
        lblTite.setText(R.string.app_name);
        setSupportActionBar(toolbar);

        // Retrieve data from SharedPreferences
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Guest");
        String userEmail = sharedPreferences.getString("userEmail", "");
        String userImg = sharedPreferences.getString("userImg", "");

        // Set up the drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        View headerView = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        mynavigationView = findViewById(R.id.nav_view);
        txt_userName = headerView.findViewById(R.id.usr_name);
        userImage = headerView.findViewById(R.id.usr_img);


        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    // Get user data
                    String myuserName = snapshot.child("name").getValue(String.class);
                    String myuserImg = snapshot.child("imageUrl").getValue(String.class);
                    String myuserEmail = snapshot.child("email").getValue(String.class);

                    // Save user data to SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userName", myuserName);
                    editor.putString("userImg", myuserImg);
                    editor.putString("userEmail", myuserEmail);
                    // Apply changes to SharedPreferences
                    editor.apply();

                } else {
                    Log.d("UserData", "No data found for user ID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserData", "Error fetching user data: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        if (userName!="Guest"){
            txt_userName.setText(userName);
        }else{
            txt_userName.setText(myuserName);
        }

        Glide.with(MainActivity.this).load(userImg).error(R.drawable.avatar).into(userImage);

        //set user name and email in profile
        if(userEmail.equals("admin@gmail.com")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UploadFragment()).commit();
            lblTite.setText("Upload");
            txt_userName.setText(userName);
        }

        // Set up the navigation view
        mynavigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open,R.string.close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Check if the fragment container is empty and add the HomeFragment if it is
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            mynavigationView.setCheckedItem(R.id.nav_home);
        }

        // search icon
        search_icon.setVisibility(View.VISIBLE);
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                search_icon.setVisibility(View.GONE);
                search_view.setVisibility(View.VISIBLE);
            }
        });


        //fetch user name and email
        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);


        lblTite.setText(R.string.app_name);
        applySavedLanguage();

    }
        @Override
        public boolean onNavigationItemSelected (@NonNull MenuItem menuItem){
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                //navigate to Home fragemant
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                lblTite.setText(R.string.app_name);
                search_icon.setVisibility(View.VISIBLE);

            } else if (id == R.id.favourite) {

                //navigate to Favourite fragemant
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavoriteStoryFragment()).commit();
                lblTite.setText(R.string.favourite);
                search_icon.setVisibility(View.GONE);

            } else if (id == R.id.search) {

                //navigate to About fragemant
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                lblTite.setText(R.string.search);
                search_icon.setVisibility(View.GONE);
                shareicon.setVisibility(View.GONE);
                lblTite.setVisibility(View.GONE);
                search_view.setVisibility(View.VISIBLE);

            } else if (id == R.id.feedback) {
                //navigate to Feedback fragemant
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedbackFragment()).commit();
                lblTite.setText(R.string.feedback);
                search_icon.setVisibility(View.GONE);

            } else if (id == R.id.setting) {

                //navigate to Setting fragemant
                lblTite.setText(R.string.setting);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                search_icon.setVisibility(View.GONE);

            } else if (id == R.id.nav_logout) {

                //navigate to LogOut fragemant
                FirebaseAuth.getInstance().signOut();
                gClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                    }
                });

                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Clear all data
                editor.clear();
                editor.apply();

                // Redirect to login screen
                Intent intent = new Intent(MainActivity.this, LogIn_Activity.class);
                startActivity(intent);
                finish();

            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
    }
    private void applySavedLanguage() {
        String lang = getSharedPreferences("Settings", Context.MODE_PRIVATE)
                .getString("My_Lang", "");
        if (!lang.isEmpty()) {
            setLocale(this, lang);
        }
    }

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
