package com.codewithgolap.samadhan.bottomactivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codewithgolap.samadhan.MainActivity;
import com.codewithgolap.samadhan.R;
import com.codewithgolap.samadhan.adapters.PostAdapter;
import com.codewithgolap.samadhan.models.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityActivity extends AppCompatActivity {

    RelativeLayout contentView;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;

    Dialog popAddPost;
    CircleImageView popupUserImage;
    TextView popupUserName;
    ImageView popupPostImage;
    Button popupAddBtn;
    TextView popupTitle, popupDescription;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri = null;

    private RecyclerView postRecyclerView;
    private DatabaseReference postRef;
    private LinearLayout postProgressBar, emptyBox;
    String userId;
    Query query;

    DatabaseReference likeReference;
    Boolean isLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        CircleImageView imageView = (CircleImageView) findViewById(R.id.user_circle_image);

        likeReference = FirebaseDatabase.getInstance().getReference("likes");

        // ini
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = mAuth.getCurrentUser().getUid();

        if (currentUser.getPhotoUrl() != null)
        {
            Glide.with(CommunityActivity.this).load(currentUser.getPhotoUrl()).into(imageView);

        }
        else {
            Glide.with(CommunityActivity.this).load(R.drawable.pc_logoo).into(imageView);

        }

        //init popup
        iniPopup();

        setupPopupImageClick();

        Button uploadPost = findViewById(R.id.btn_public_post);
        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        contentView = findViewById(R.id.content_view);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.community);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.detect:
                                CommunityActivity.this.startActivity(new Intent(CommunityActivity.this,
                                        MainActivity.class));
                                CommunityActivity.this.finish();
                                CommunityActivity.this.overridePendingTransition(0, 0);
                                return true;
                            case R.id.crops:
                                CommunityActivity.this.startActivity(new Intent(CommunityActivity.this,
                                        CropsActivity.class));
                                CommunityActivity.this.finish();
                                CommunityActivity.this.overridePendingTransition(0, 0);
                                return true;
                            case R.id.community:
                                return true;
                            case R.id.weather:
                                CommunityActivity.this.startActivity(new Intent(CommunityActivity.this,
                                        WeatherActivity.class));
                                CommunityActivity.this.finish();
                                CommunityActivity.this.overridePendingTransition(0, 0);
                                return true;
                        }
                        return false;
                    }
                });

        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        postRecyclerView= findViewById(R.id.post_recyclerView);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        emptyBox = findViewById(R.id.empty_box);
        postProgressBar = findViewById(R.id.crops_progree_bar);
        postProgressBar.setVisibility(View.VISIBLE);

        query = postRef.orderByChild("title");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    postProgressBar.setVisibility(View.VISIBLE);
                    postsList();
                }
                else {
                    postProgressBar.setVisibility(View.GONE);
                    Toast.makeText(CommunityActivity.this, ":", Toast.LENGTH_SHORT).show();
                    emptyBox.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //postsList();
    }

    private void postsList() {
        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
        FirebaseRecyclerAdapter<Post, PostAdapter> postAdapter = new FirebaseRecyclerAdapter<Post,
                PostAdapter>(options) {
            @NonNull
            @Override
            public PostAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent,
                        false);
                PostAdapter holder = new PostAdapter(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull PostAdapter holder, final int position, @NonNull final Post model) {

                postProgressBar.setVisibility(View.GONE);
                holder.tvTitle.setText(model.getTitle());
                holder.tvUsername.setText(model.getUserName());
                holder.tvDesc.setText(model.getDescription());
                Glide.with(CommunityActivity.this).load(model.getPicture()).into(holder.imgPost);

                String userImg = model.getUserPhoto();
                if (userImg != null)
                {
                    Glide.with(CommunityActivity.this).load(userImg).into(holder.imgPostProfile);

                }
                else {
                    Glide.with(CommunityActivity.this).load(R.drawable.pc_logoo).into(holder.imgPostProfile);

                }

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userId = firebaseUser.getUid();
                String postKey = getRef(position).getKey();

                holder.getLikeButtonStatus(postKey, userId);
                holder.getComments(postKey);

                holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isLike = true;

                        likeReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (isLike == true){
                                    if (snapshot.child(postKey).hasChild(userId)){

                                        likeReference.child(postKey).removeValue();
                                        isLike = false;

                                    }else {
                                        likeReference.child(postKey).child(userId).setValue(true);
                                        isLike = false;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                String pTitle = model.getTitle();

                holder.shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.imgPost.getDrawable();
                        if (bitmapDrawable == null){
                            shareTextOnly(pTitle);
                        }else {
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            shareImageAndText(pTitle, bitmap);
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent postDetailActivity = new Intent(CommunityActivity.this, PostDetailsActivity.class);

                        postDetailActivity.putExtra("title",model.getTitle());
                        postDetailActivity.putExtra("userName", model.getUserName());
                        postDetailActivity.putExtra("postImage",model.getPicture());
                        postDetailActivity.putExtra("description",model.getDescription());
                        postDetailActivity.putExtra("postKey",model.getPostKey());
                        postDetailActivity.putExtra("userPhoto",model.getUserPhoto());

                        // will fix this later i forgot to add user name to post object
                        //postDetailActivity.putExtra("userName",mData.get(position).getUsername);
                        long timestamp  = (long) model.getTimeStamp();
                        postDetailActivity.putExtra("postDate",timestamp) ;
                        startActivity(postDetailActivity);
                    }
                });
            }
        };

        postRecyclerView.setAdapter(postAdapter);
        postAdapter.startListening();
    }

    private void setupPopupImageClick() {
        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here when image clicked we need to open the gallery
                // before we open the gallery we need to check if our app have the access to user files
                // we did this before in register activity I'm just going to copy the code to save time ...

                checkAndRequestForPermission();


            }
        });
    }

    private void shareImageAndText(String pTitle, Bitmap bitmap) {
        String shareBody = pTitle;
        Uri uri = saveImageToShare(bitmap);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent,"Share Via"));

    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(getCacheDir(),"images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder,"shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this,"com.codewithgolap.samadhan.fileprovider",file);

        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String pTitle) {
        String shareBody = pTitle;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent,"Share Via"));
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(CommunityActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CommunityActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(CommunityActivity.this, "Please accept for required permission",
                        Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(CommunityActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else
            // everything goes well : we have permission to access user gallery
            openGallery();
    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            popupPostImage.setImageURI(pickedImgUri);

        }


    }

    private void iniPopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //ini popup widgets
        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupUserName = popAddPost.findViewById(R.id.popup_user_name);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);

        // load Current user profile photo
        if (currentUser.getPhotoUrl() != null)
        {
            popupUserName.setText(currentUser.getDisplayName());
            Glide.with(CommunityActivity.this).load(currentUser.getPhotoUrl()).into(popupUserImage);

        }
        else {
            popupUserName.setText(currentUser.getDisplayName());
            Glide.with(CommunityActivity.this).load(R.drawable.pc_logoo).into(popupUserImage);

        }

        //Add post click listener
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                if (!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()
                        && pickedImgUri != null) {

                    //everything is okey no empty or null value
                    // TODO Create Post Object and add it to firebase database
                    // first we need to upload post Image
                    // access firebase storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();

                                    if (currentUser.getPhotoUrl() != null) {
                                        Post post = new Post(popupTitle.getText().toString(),
                                                popupDescription.getText().toString(),
                                                imageDownloadLink,
                                                currentUser.getUid(),
                                                currentUser.getPhotoUrl().toString(),
                                                currentUser.getDisplayName());

                                        //add post to firebase
                                        emptyBox.setVisibility(View.GONE);
                                        addPost(post);
                                    } else {
                                        Post post = new Post(popupTitle.getText().toString(),
                                                popupDescription.getText().toString(),
                                                imageDownloadLink,
                                                currentUser.getUid(),
                                                null,
                                                currentUser.getDisplayName());

                                        //add post to firebase
                                        emptyBox.setVisibility(View.GONE);
                                        addPost(post);
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // something goes wrong uploading picture

                                    showMessage(e.getMessage());
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });

                } else {
                    showMessage("Please verify all input fields and choose Post Image");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);

                }

            }
        });
    }

    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        // get post unique ID and upadte post key
        String key = myRef.getKey();
        post.setPostKey(key);


        // add post data to firebase database

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added successfully");
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();

                popupTitle.setText("");
                popupDescription.setText("");
                popupPostImage.setImageResource(R.drawable.upload_image);
            }
        });
    }

    private void showMessage(String s) {

        Toast.makeText(CommunityActivity.this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}