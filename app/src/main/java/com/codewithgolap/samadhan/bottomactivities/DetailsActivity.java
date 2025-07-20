package com.codewithgolap.samadhan.bottomactivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codewithgolap.samadhan.R;
import com.codewithgolap.samadhan.models.CropsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {

    private ImageView detailsCropImage;
    private TextView detailsCropText;
    private String cropID = "";
    private ProgressBar cropsProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        cropID = getIntent().getStringExtra("cId");

        detailsCropImage = (ImageView) findViewById(R.id.detailsCropImage);
        detailsCropText = (TextView) findViewById(R.id.cropDetailsText);
        cropsProgressBar = findViewById(R.id.progress_bar);


        getCropDetails(cropID);
    }

    private void getCropDetails(String cropID) {
        DatabaseReference cropRef = FirebaseDatabase.getInstance().getReference().child("Crops");

        cropRef.child(cropID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    CropsModel cropsModel = dataSnapshot.getValue(CropsModel.class);

                    detailsCropText.setText(cropsModel.getDetailCrop());
                    //Picasso.get().load(cropsModel.getmImg()).into(mainImageView);
                    //Glide.with(CropsDetailsActivity.this).load(cropsModel.getmImg()).into(mainImageView);
                    Glide.with(DetailsActivity.this)
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
                            .into(detailsCropImage);
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