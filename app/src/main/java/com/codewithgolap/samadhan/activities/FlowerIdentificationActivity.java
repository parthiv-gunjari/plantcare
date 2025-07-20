package com.codewithgolap.samadhan.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codewithgolap.samadhan.R;
import com.codewithgolap.samadhan.ml.CropIndetification;
import com.codewithgolap.samadhan.ml.FlowersModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FlowerIdentificationActivity extends AppCompatActivity {

    TextView result, confidence, demoTxt, classified, otherConfidence, clickHere;
    ImageView imageView, arrowImage;
    Button picture, management_button, closeBtn;
    LinearLayout managementBtn;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_identification);

        result = findViewById(R.id.result);
        //confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);
        //managementBtn = findViewById(R.id.managementBtn);
        // management_button =findViewById(R.id.management_button);

        demoTxt = findViewById(R.id.demoText);
        clickHere = findViewById(R.id.click_here);
        arrowImage = findViewById(R.id.demoArrow);
        classified = findViewById(R.id.classified);
        //otherConfidence = findViewById(R.id.confidencesText);

        demoTxt.setVisibility(View.VISIBLE);
        clickHere.setVisibility(View.GONE);
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
    }

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
            clickHere.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
            // otherConfidence.setVisibility(View.VISIBLE);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void classifyImage(Bitmap image) {
        try {
            FlowersModel model = FlowersModel.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            FlowersModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"pink primrose",
                    "hard-leaved pocket orchid",
                    "canterbury bells",
                    "sweet pea",
                    "english marigold",
                    "tiger lily",
                    "moon orchid",
                    "bird of paradise",
                    "monkshood",
                    "globe thistle",
                    "snapdragon",
                    "colt's foot",
                    "king protea",
                    "spear thistle",
                    "yellow iris",
                    "globe-flower",
                    "purple coneflower",
                    "peruvian lily",
                    "balloon flower",
                    "giant white arum lily",
                    "fire lily",
                    "pincushion flower",
                    "fritillary",
                    "red ginger",
                    "grape hyacinth",
                    "corn poppy",
                    "prince of wales feathers",
                    "stemless gentian",
                    "artichoke",
                    "sweet william",
                    "carnation",
                    "garden phlox",
                    "love in the mist",
                    "mexican aster",
                    "alpine sea holly",
                    "ruby-lipped cattleya",
                    "cape flower",
                    "great masterwort",
                    "siam tulip",
                    "lenten rose",
                    "barbeton daisy",
                    "daffodil",
                    "sword lily",
                    "poinsettia",
                    "bolero deep blue",
                    "wallflower",
                    "marigold",
                    "buttercup",
                    "oxeye daisy",
                    "common dandelion",
                    "petunia",
                    "wild pansy",
                    "primula",
                    "sunflower",
                    "pelargonium",
                    "bishop of llandaff",
                    "gaura",
                    "geranium",
                    "orange dahlia",
                    "pink-yellow dahlia",
                    "cautleya spicata",
                    "japanese anemone",
                    "black-eyed susan",
                    "silverbush",
                    "californian poppy",
                    "osteospermum",
                    "spring crocus",
                    "bearded iris",
                    "windflower",
                    "tree poppy",
                    "gazania",
                    "azalea",
                    "water lily",
                    "rose",
                    "thorn apple",
                    "morning glory",
                    "passion flower",
                    "lotus",
                    "toad lily",
                    "anthurium",
                    "frangipani",
                    "clematis",
                    "hibiscus",
                    "columbine",
                    "desert-rose",
                    "tree mallow",
                    "magnolia",
                    "cyclamen",
                    "watercress",
                    "canna lily",
                    "hippeastrum",
                    "bee balm",
                    "ball moss",
                    "foxglove",
                    "bougainvillea",
                    "camellia",
                    "mallow",
                    "mexican petunia",
                    "bromelia",
                    "blanket flower",
                    "trumpet creeper",
                    "blackberry lily"
            };
            result.setText(classes[maxPos]);
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/search?q="+result.getText())));
                }
            });

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