package com.example.androidinstantchat;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public abstract class SingleFragmentActivity extends AppCompatActivity {


    public abstract Fragment createFragment();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_fragment_container);



        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment  fragment = fragmentManager.findFragmentById(R.id.single_fragment_container);

        if(fragment==null){
            fragment = createFragment();
            fragmentManager.beginTransaction().add(R.id.single_fragment_container,fragment)
                    .commit();
        }


    }
}