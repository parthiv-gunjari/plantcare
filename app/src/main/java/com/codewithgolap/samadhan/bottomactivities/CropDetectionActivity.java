package com.codewithgolap.samadhan.bottomactivities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codewithgolap.samadhan.R;
import com.codewithgolap.samadhan.ml.CropIndetification;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CropDetectionActivity extends AppCompatActivity {

    TextView result, confidence, demoTxt, classified, otherConfidence;
    ImageView imageView, arrowImage;
    Button picture, management_button, closeBtn;
    LinearLayout managementBtn;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detection);

        result = findViewById(R.id.result);
        //confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);
        //managementBtn = findViewById(R.id.managementBtn);
       // management_button =findViewById(R.id.management_button);

        demoTxt = findViewById(R.id.demoText);
        arrowImage = findViewById(R.id.demoArrow);
        classified = findViewById(R.id.classified);
        //otherConfidence = findViewById(R.id.confidencesText);

        demoTxt.setVisibility(View.VISIBLE);
        arrowImage.setVisibility(View.VISIBLE);
        classified.setVisibility(View.GONE);
        result.setVisibility(View.GONE);
       // otherConfidence.setVisibility(View.GONE);

        picture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

//        management_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                management(view);
//            }
//        });

    }


//    private void management(View view) {
//        if (result.getText()== "Bud Rot"){
//            AlertDialog.Builder builder = new AlertDialog.Builder(CropDetectionActivity.this);
//            ViewGroup viewGroup = findViewById(android.R.id.content);
//            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.bud_rot_layout, viewGroup, false);
//            builder.setView(dialogView);
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//            closeBtn = dialogView.findViewById(R.id.got_it_btn);
//            closeBtn.setOnClickListener(view15 -> alertDialog.dismiss());
//
//        }else if (result.getText()=="Ganoderma"){
//            AlertDialog.Builder builder = new AlertDialog.Builder(DetectionActivity.this);
//            ViewGroup viewGroup = findViewById(android.R.id.content);
//            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.ganoderma_layout, viewGroup, false);
//            builder.setView(dialogView);
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//            closeBtn = dialogView.findViewById(R.id.got_it_btn);
//            closeBtn.setOnClickListener(view14 -> alertDialog.dismiss());
//
//        }else if (result.getText()=="Stem Bleeding"){
//            AlertDialog.Builder builder = new AlertDialog.Builder(DetectionActivity.this);
//            ViewGroup viewGroup = findViewById(android.R.id.content);
//            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.stembleeding_layout, viewGroup, false);
//            builder.setView(dialogView);
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//            closeBtn = dialogView.findViewById(R.id.got_it_btn);
//            closeBtn.setOnClickListener(view13 -> alertDialog.dismiss());
//
//        }else if (result.getText()=="Leaf Spot"){
//            AlertDialog.Builder builder = new AlertDialog.Builder(DetectionActivity.this);
//            ViewGroup viewGroup = findViewById(android.R.id.content);
//            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.leaf_spot_layout, viewGroup, false);
//            builder.setView(dialogView);
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//            closeBtn = dialogView.findViewById(R.id.got_it_btn);
//            closeBtn.setOnClickListener(view12 -> alertDialog.dismiss());
//
//        }else if (result.getText()=="Crown Choke"){
//            AlertDialog.Builder builder = new AlertDialog.Builder(DetectionActivity.this);
//            ViewGroup viewGroup = findViewById(android.R.id.content);
//            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.crown_choke_layout, viewGroup, false);
//            builder.setView(dialogView);
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//            closeBtn = dialogView.findViewById(R.id.got_it_btn);
//            closeBtn.setOnClickListener(view1 -> alertDialog.dismiss());
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            demoTxt.setVisibility(View.GONE);
            arrowImage.setVisibility(View.GONE);
            classified.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
           // otherConfidence.setVisibility(View.VISIBLE);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void classifyImage(Bitmap image){
        try {
            CropIndetification model = CropIndetification.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            CropIndetification.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Apple", "Blueberry","Cherry", "Corn (maize)", "Grape", "Peach",
                    "Pepper bell", "Potato", "Raspberry", "Strawberry", "Tomato",
                    "Alstonia Scholaris", "Arjun", "Basil", "Chinar", "Gauva",
                    "Jamun", "Jatropha", "Lemon", "Mango", "Pomegranate", "Pongamia Pinnata"};
            result.setText(classes[maxPos]);

//            String s = "";
//            for(int i = 0; i < classes.length; i++){
//                s += String.format("%s: %.1f%%\t", classes[i], confidences[i] * 100);
//            }
//            confidence.setText(s);

           // managementBtn.setVisibility(View.VISIBLE);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }



}