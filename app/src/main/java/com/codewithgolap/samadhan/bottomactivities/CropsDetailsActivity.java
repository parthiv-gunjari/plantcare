package com.codewithgolap.samadhan.bottomactivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codewithgolap.samadhan.R;
import com.codewithgolap.samadhan.models.CropsModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CropsDetailsActivity extends AppCompatActivity {

    private ImageView mainImageView;
    private TextView tempTv, cropNameTv, cropScTv, cropDetailsTv;
    private String cropID = "";
    private ProgressBar cropsProgressBar;

    private RelativeLayout detailsRL, cultureRL;

    FloatingActionButton mAddCultureFab, mAddInfoFab;
    ExtendedFloatingActionButton mAddFab;

    TextView addCultureActionText, addInfoActionText;

    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crops_details);

        cropID = getIntent().getStringExtra("cId");

        mainImageView = (ImageView) findViewById(R.id.mainImageView);
        tempTv = (TextView) findViewById(R.id.weatherDegreeText);
        cropNameTv = (TextView) findViewById(R.id.cropName);
        cropScTv = (TextView) findViewById(R.id.cropScientificName);
        cropDetailsTv = (TextView) findViewById(R.id.cropDetails);
//        detailsRL = (RelativeLayout) findViewById(R.id.detailsCard);
//        cultureRL = (RelativeLayout) findViewById(R.id.cultureCard);

        cropsProgressBar = findViewById(R.id.progress_bar);

        mAddFab = findViewById(R.id.add_fab);
        // FAB button
        mAddCultureFab = findViewById(R.id.add_culture_fab);
        mAddInfoFab = findViewById(R.id.add_info_fab);

        addCultureActionText = findViewById(R.id.add_culture_action_text);
        addInfoActionText = findViewById(R.id.add_info_action_text);

        mAddCultureFab.setVisibility(View.GONE);
        mAddInfoFab.setVisibility(View.GONE);
        addCultureActionText.setVisibility(View.GONE);
        addInfoActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;
        mAddFab.shrink();

        mAddFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {

                            mAddCultureFab.show();
                            mAddInfoFab.show();
                            addCultureActionText.setVisibility(View.VISIBLE);
                            addInfoActionText.setVisibility(View.VISIBLE);

                            mAddFab.extend();

                            isAllFabsVisible = true;
                        } else {
                            mAddCultureFab.hide();
                            mAddInfoFab.hide();
                            addCultureActionText.setVisibility(View.GONE);
                            addInfoActionText.setVisibility(View.GONE);

                            mAddFab.shrink();

                            isAllFabsVisible = false;
                        }
                    }
                });

        mAddCultureFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CropsDetailsActivity.this, CultureActivity.class);
                        intent.putExtra("cId", cropID);
                        startActivity(intent);
                    }
                });

        mAddInfoFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CropsDetailsActivity.this, DetailsActivity.class);
                        intent.putExtra("cId", cropID);
                        startActivity(intent);
                    }
                });

        //cropsProgressBar.setVisibility(View.VISIBLE);
//        detailsRL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(CropsDetailsActivity.this, DetailsActivity.class);
//                intent.putExtra("cId", cropID);
//                startActivity(intent);
//            }
//        });
//
//        cultureRL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(CropsDetailsActivity.this, CultureActivity.class);
//                intent.putExtra("cId", cropID);
//                startActivity(intent);
//            }
//        });

        getCropDetails(cropID);
    }

    private void getCropDetails(String cropID) {

        DatabaseReference cropRef = FirebaseDatabase.getInstance().getReference().child("Crops");

        cropRef.child(cropID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    CropsModel cropsModel = dataSnapshot.getValue(CropsModel.class);

                    cropNameTv.setText(cropsModel.getName());
                    cropScTv.setText(cropsModel.getDescription());
                    cropDetailsTv.setText(cropsModel.getDetails());
                    tempTv.setText(cropsModel.getTemp());
                    //Picasso.get().load(cropsModel.getmImg()).into(mainImageView);
                    //Glide.with(CropsDetailsActivity.this).load(cropsModel.getmImg()).into(mainImageView);
                    Glide.with(CropsDetailsActivity.this)
                            .load(cropsModel.getmImg())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    cropsProgressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    cropsProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(mainImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}