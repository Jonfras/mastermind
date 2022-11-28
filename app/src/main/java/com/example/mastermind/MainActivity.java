package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Mastermind mastermind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mastermind = new Mastermind(getAssets(), "config.txt");
        List<Character> code = mastermind.createCode();

        findViewById(R.id.btnSubmit).setOnClickListener(view -> {

        });
    }





}