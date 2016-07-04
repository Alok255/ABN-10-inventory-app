package com.fanrir.inventory.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fanrir.inventory.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new InventoryFragment(), InventoryFragment.class.getSimpleName())
                    .commit();
        }
    }
}
