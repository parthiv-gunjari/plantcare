package com.codewithgolap.samadhan.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.codewithgolap.samadhan.R;
import com.codewithgolap.samadhan.interfaces.PostItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvUsername, tvTitle, tvDesc;
    public ImageView imgPost;
    public CircleImageView imgPostProfile;
    public ProgressBar progressBar;
    public PostItemClickListener listener;
    private final Context context;
    public TextView likeTv, commentTv;
    public ImageView likeBtn, shareBtn;
    DatabaseReference likeReference;

    public PostAdapter(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        tvUsername = itemView.findViewById(R.id.row_post_profile_name);
        tvTitle = itemView.findViewById(R.id.row_post_title);
        tvDesc = itemView.findViewById(R.id.row_post_description);
        imgPost = itemView.findViewById(R.id.row_post_img);
        imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
        progressBar = itemView.findViewById(R.id.progress_bar);

        likeBtn = itemView.findViewById(R.id.like_btn);
        shareBtn = itemView.findViewById(R.id.share_btn);
        likeTv = itemView.findViewById(R.id.like_text);
        commentTv = itemView.findViewById(R.id.comment_text);
    }

    public void getComments(String postKey) {
        FirebaseDatabase.getInstance().getReference().child("Comment").child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentTv.setText(snapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getLikeButtonStatus(final String postKey, final String userId){
        likeReference = FirebaseDatabase.getInstance().getReference("likes");
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(userId)){
                    int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    likeTv.setText(likeCount+" likes");
                    likeBtn.setImageResource(R.drawable.fav_fill);
                }else {
                    int likeCount = (int) snapshot.child(postKey).getChildrenCount();
                    likeTv.setText(likeCount+" likes");
                    likeBtn.setImageResource(R.drawable.ic_round_favorite_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void setItemClickListener(PostItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),false);
    }

}
