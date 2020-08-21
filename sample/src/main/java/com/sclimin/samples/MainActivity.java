package com.sclimin.samples;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.flip_horizontal).setOnClickListener(this::test);
        findViewById(R.id.flip_vertical).setOnClickListener(this::test);
    }

    private void test(View view) {
        startActivity(new Intent(view.getContext(), FlipActivity.class)
                .putExtra("LAYOUT_ID", view.getId() == R.id.flip_horizontal ?
                        R.layout.activity_flip_horizontal : R.layout.activity_flip_vertical));
    }
}