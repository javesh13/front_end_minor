package com.example.root.new_hello;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class do_chat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_chat);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
