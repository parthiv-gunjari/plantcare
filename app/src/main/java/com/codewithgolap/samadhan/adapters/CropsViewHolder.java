package com.codewithgolap.samadhan.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithgolap.samadhan.R;
import com.codewithgolap.samadhan.interfaces.CropsItemClickListener;
import com.codewithgolap.samadhan.models.CropsModel;

import java.util.ArrayList;


public class CropsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView name, description;
    public ImageView image;
    public CropsItemClickListener listener;
    private Context context;


    public CropsViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        name = (TextView)itemView.findViewById(R.id.cropName);
        description = (TextView)itemView.findViewById(R.id.cropDescription);
        image = (ImageView)itemView.findViewById(R.id.cropImage);
    }

    public void setItemClickListener(CropsItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),false);
    }
}
