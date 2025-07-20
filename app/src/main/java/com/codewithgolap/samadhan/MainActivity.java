package com.codewithgolap.samadhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codewithgolap.samadhan.activities.FlowerIdentificationActivity;
import com.codewithgolap.samadhan.activities.logins.LoginActivity;
import com.codewithgolap.samadhan.bottomactivities.CommunityActivity;
import com.codewithgolap.samadhan.bottomactivities.CropDetectionActivity;
import com.codewithgolap.samadhan.bottomactivities.CropsActivity;
import com.codewithgolap.samadhan.bottomactivities.TensorActivity;
import com.codewithgolap.samadhan.bottomactivities.WeatherActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    RelativeLayout contentView;
    Button detectBtn, detectBtn2, detectFlowerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        detectBtn = (Button) findViewById(R.id.detectBtn);
        detectBtn2 = (Button) findViewById(R.id.detectBtn_for_crop);
        detectFlowerBtn = (Button) findViewById(R.id.detectBtn_for_flower);

        startActivties();

        contentView = findViewById(R.id.content_view);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.detect);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.detect:
                                return true;
                            case R.id.crops:
                                MainActivity.this.startActivity(new Intent(MainActivity.this,
                                        CropsActivity.class));
                                MainActivity.this.finish();
                                MainActivity.this.overridePendingTransition(0, 0);
                                return true;
                            case R.id.community:
                                MainActivity.this.startActivity(new Intent(MainActivity.this,
                                        CommunityActivity.class));
                                MainActivity.this.finish();
                                MainActivity.this.overridePendingTransition(0, 0);
                                return true;
                            case R.id.weather:
                                MainActivity.this.startActivity(new Intent(MainActivity.this,
                                        WeatherActivity.class));
                                MainActivity.this.finish();
                                MainActivity.this.overridePendingTransition(0, 0);
                                return true;
                        }
                        return false;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.home_about:
                Toast.makeText(this, "Home clicked!!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startActivties() {
        detectBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, TensorActivity.class));
        });

        detectBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CropDetectionActivity.class));
            }
        });
        detectFlowerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FlowerIdentificationActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}