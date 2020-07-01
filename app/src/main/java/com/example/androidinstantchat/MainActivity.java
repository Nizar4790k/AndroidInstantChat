package com.example.androidinstantchat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;

public class MainActivity extends SingleFragmentActivity{



    @Override
    public Fragment createFragment() {
        return new MainFragment();
    }
}