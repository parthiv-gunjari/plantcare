package com.codewithgolap.samadhan.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.codewithgolap.samadhan.R;

public class FirstActivity extends AppCompatActivity {

    AppCompatButton btn;
    TextView prgtext;
    ImageView piximage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btn=findViewById(R.id.pixelbutton);
        prgtext=findViewById(R.id.progresstext);
        piximage=findViewById(R.id.pixelimage);
        TextView intro=findViewById(R.id.intro);
        TextView hi=findViewById(R.id.hello);

        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fadein);
        final Animation fade = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade);
        final Animation fadef = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fadefirst);
        final Animation fadebut = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fadebutton);
        hi.startAnimation(fadef);
        new Handler().postDelayed(() -> {
        }, 4000);
        piximage.startAnimation(an);
        new Handler().postDelayed(() -> {
        }, 2000);
        intro.startAnimation(fade);
        btn.startAnimation(fadebut);


        btn.setOnClickListener(v -> {

            Intent i = new Intent(FirstActivity.this, LoadingActivity.class);
            startActivity(i);
            finish();
        });
    }
}