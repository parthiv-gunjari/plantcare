package com.codewithgolap.samadhan.bottomactivities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.codewithgolap.samadhan.R
import com.codewithgolap.samadhan.activities.Classifier
import kotlinx.android.synthetic.main.activity_tensor.*
import kotlinx.android.synthetic.main.apple_black_rot.view.*
import java.io.IOException

class TensorActivity : AppCompatActivity() {

    private lateinit var mClassifier: Classifier
    private lateinit var mBitmap: Bitmap

    private val mCameraRequestCode = 0
    private val mGalleryRequestCode = 2

    private val mInputSize = 224
    private val mModelPath = "plant_disease_model.tflite"
    private val mLabelPath = "plant_labels.txt"
    private val mSamplePath = "scan_leaf.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tensor)

        mClassifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)

        resources.assets.open(mSamplePath).use {
            mBitmap = BitmapFactory.decodeStream(it)
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mInputSize, mInputSize, true)
            mPhotoImageView.setImageBitmap(mBitmap)
        }

        mCameraButton.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(callCameraIntent, mCameraRequestCode)
        }

        mGalleryButton.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_PICK)
            callGalleryIntent.type = "image/*"
            startActivityForResult(callGalleryIntent, mGalleryRequestCode)
        }
        mDetectButton.setOnClickListener {
            val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
            mResultTextView.text= results?.title+"\n Confidence:"+results?.confidence

            mShowPrecaution.alpha = 1.0f
            mResultTextView.alpha = 1.0f
            demoTxt.alpha = 0.0f
            demoArrow.alpha = 0.0f
            val animationSlideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
            mShowPrecaution.startAnimation(animationSlideUp)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == mCameraRequestCode){
            //Considérons le cas de la caméra annulée
            if(resultCode == Activity.RESULT_OK && data != null) {
                mBitmap = data.extras!!.get("data") as Bitmap
                mBitmap = scaleImage(mBitmap)
                val toast = Toast.makeText(this, ("Image crop to: w= ${mBitmap.width} h= ${mBitmap.height}"), Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 20)
                toast.show()
                mPhotoImageView.setImageBitmap(mBitmap)
                mResultTextView.text= "Your photo image set now."
                mDetectButton.alpha = 1.0f
                demoTxt.alpha = 0.0f
                demoArrow.alpha = 0.0f
                mResultTextView.alpha = 1.0f
            } else {
                Toast.makeText(this, "Camera cancel..", Toast.LENGTH_LONG).show()
            }
        } else if(requestCode == mGalleryRequestCode) {
            if (data != null) {
                val uri = data.data

                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                println("Success!!!")
                mBitmap = scaleImage(mBitmap)
                mPhotoImageView.setImageBitmap(mBitmap)
                mDetectButton.alpha = 1.0f
                demoTxt.alpha = 0.0f
                demoArrow.alpha = 0.0f
                mResultTextView.alpha = 1.0f

            }
        } else {
            Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_LONG).show()

        }
    }


    fun scaleImage(bitmap: Bitmap?): Bitmap {
        val orignalWidth = bitmap!!.width
        val originalHeight = bitmap.height
        val scaleWidth = mInputSize.toFloat() / orignalWidth
        val scaleHeight = mInputSize.toFloat() / originalHeight
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, orignalWidth, originalHeight, matrix, true)
    }

    fun precaution(view: View) {
        val results = mClassifier.recognizeImage(mBitmap).firstOrNull()

        when (results?.title) {
            "apple apple scab" -> {
                val view = View.inflate(this@TensorActivity, R.layout.apple_scab, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "apple black rot" -> {
                val view = View.inflate(this@TensorActivity, R.layout.apple_black_rot, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "apple cedar apple rust" -> {
                val view = View.inflate(this@TensorActivity, R.layout.apple_cedar_rust, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "apple healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.apply_healthy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "blueberry healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.blueberry_healthy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "cherry including sour powdery mildew" -> {
                val view = View.inflate(this@TensorActivity, R.layout.cherry_powdery, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "cherry including sour healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.cherry_helathy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "corn maize cercospora leaf spot gray leaf spot" -> {
                val view = View.inflate(this@TensorActivity, R.layout.corn_leaf_spot, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "corn maize common rust" -> {
                val view = View.inflate(this@TensorActivity, R.layout.corn_common_rust, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "corn maize northern leaf blight" -> {
                val view = View.inflate(this@TensorActivity, R.layout.corn_northern_leaf_blight, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "corn maize healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.corn_healthy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "grape black rot" -> {
                val view = View.inflate(this@TensorActivity, R.layout.grape_black_rot, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "grape esca black measles" -> {
                val view = View.inflate(this@TensorActivity, R.layout.grape_esca_black_measles, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "grape leaf blight isariopsis leaf spot" -> {
                val view = View.inflate(this@TensorActivity, R.layout.grape_blight_isariopsis_spot, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "grape healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.grape_healthy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "peach bacterial spot" -> {
                val view = View.inflate(this@TensorActivity, R.layout.peach_bacterial_spot, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "peach healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.peach_healthy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "potato early blight" -> {
                val view = View.inflate(this@TensorActivity, R.layout.potato_early_blighty, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "potato late blight" -> {
                val view = View.inflate(this@TensorActivity, R.layout.potato_late_blight, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "potato healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.potato_healthy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "raspberry healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.raspberry_healthy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "strawberry leaf scorch" -> {
                val view = View.inflate(this@TensorActivity, R.layout.strawberry_leaf_scorch, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "strawberry healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.strawberry_healthy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato bacterial spot" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_bacterial_spot, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato early blight" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_early_blight, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato late blight" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_late_blight, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato leaf mold" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_leaf_mold, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato septoria leaf spot" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_septoria_leaf_spot, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato spider mites two spotted spider mite" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_spider_mites, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato target spot" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_target_spot, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato tomato yellow leaf curl virus" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_yellow_curl_virus, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato tomato mosaic virus" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_mosaic_virus, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
            "tomato healthy" -> {
                val view = View.inflate(this@TensorActivity, R.layout.tomato_healthy, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }

            else -> {
                //Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show()
                val view = View.inflate(this@TensorActivity, R.layout.custom_dailog, null)
                val builder = AlertDialog.Builder(this@TensorActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.show()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                view.got_it_btn.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }
}