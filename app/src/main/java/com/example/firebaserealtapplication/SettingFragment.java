package com.example.firebaserealtapplication;

import static androidx.databinding.adapters.TextViewBindingAdapter.setTextSize;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Locale;

public class SettingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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

    ShapeableImageView img_avatar;
    Button edt_profile;
    TextView txt_name, txt_email, txt_size, txt_language;
    private int selectedIndex = 1; // Default to "Medium"
    private int selectedLangIndex = 0; // Default to "English"

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.fragment_setting, container, false);
        img_avatar = lView.findViewById(R.id.img_avatar);
        edt_profile = lView.findViewById(R.id.btn_edit_profile);
        txt_name = lView.findViewById(R.id.full_name);
        txt_email = lView.findViewById(R.id.email);
        txt_size = lView.findViewById(R.id.txt_Size);
        txt_language = lView.findViewById(R.id.txtLang);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Guest");
        String userEmail = sharedPreferences.getString("userEmail", "");
        String userImg = sharedPreferences.getString("userImg", "");
        String savedTextSize = sharedPreferences.getString("textSize", "Medium");
        String savedLanguage = sharedPreferences.getString("language", "English");

        txt_name.setText(userName);
        txt_email.setText(userEmail);
        txt_size.setText(savedTextSize);
        txt_language.setText(savedLanguage);

        switch (savedTextSize) {
            case "Small":
                selectedIndex = 0;
                break;
            case "Medium":
                selectedIndex = 1;
                break;
            case "Large":
                selectedIndex = 2;
                break;
        }

        switch (savedLanguage) {
            case "English":
                selectedLangIndex = 0;
                break;
            case "Khmer":
                selectedLangIndex = 1;
                break;
            case "Thai":
                selectedLangIndex = 2;
                break;
        }

        if (userImg == null) {
            Glide.with(getContext()).load(R.drawable.avatar).into(img_avatar);
        } else {
            Glide.with(getContext()).load(userImg).into(img_avatar);
        }

        loadLocale();

        txt_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSizeDialog();
            }
        });

        edt_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditUserInfoFragment()).commit();
            }
        });

        txt_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLangDialog();
            }
        });

        return lView;
    }
    public void setTextSize(float size) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.fontScale = size;
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        getActivity().recreate();
    }

    public void showSizeDialog() {
        final String[] sizeOptions = {"Small", "Medium", "Large"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Font Size");

        builder.setSingleChoiceItems(sizeOptions, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedIndex = which;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedSize = sizeOptions[selectedIndex];

                switch (selectedIndex) {
                    case 0:
                        setTextSize(1f);
                        break;
                    case 1:
                        setTextSize(1.25f);
                        break;
                    case 2:
                        setTextSize(1.5f);
                        break;
                }
                txt_size.setText(selectedSize);
                SharedPreferences.Editor editor = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE).edit();
                editor.putString("textSize", selectedSize);
                editor.apply();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showLangDialog() {
        final String[] langOptions = {"English", "Khmer", "Thai"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Language");

        builder.setSingleChoiceItems(langOptions, selectedLangIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedLangIndex = which;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedLanguage = langOptions[selectedLangIndex];

                switch (selectedLangIndex) {
                    case 0:
                        setLocale("en");
                        break;
                    case 1:
                        setLocale("km");
                        break;
                    case 2:
                        setLocale("th");
                        break;
                }

                txt_language.setText(selectedLanguage);
                SharedPreferences.Editor editor = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE).edit();
                editor.putString("language", selectedLanguage);
                editor.apply();

                restartApp();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadLocale() {
        String languageCode = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
                .getString("My_Lang", "");
        if (!languageCode.isEmpty()) {
            setLocale(languageCode);
        }
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        SharedPreferences.Editor editor = getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang", languageCode);
        editor.apply();
    }

    private void restartApp() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
