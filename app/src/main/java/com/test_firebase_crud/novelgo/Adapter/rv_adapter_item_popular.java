package com.test_firebase_crud.novelgo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test_firebase_crud.novelgo.Activity.DetailActivity;
import com.test_firebase_crud.novelgo.Model.Popular;
import com.test_firebase_crud.novelgo.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public  class rv_adapter_item_popular extends RecyclerView.Adapter<rv_adapter_item_popular.ViewHolder> {

    private Context context;
    private ArrayList<Popular> populars;

    public rv_adapter_item_popular(Context context, ArrayList<Popular> populars) {
        this.context = context;
        this.populars = populars;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_popular, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(populars.get(position).getTitle());
        Glide.with(context).load(populars.get(position).getImage()).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) context, holder.imageView, ViewCompat.getTransitionName(holder.imageView));
                intent.putExtra("image", populars.get(position).getImage());
                intent.putExtra("title", populars.get(position).getTitle());
                intent.putExtra("link", populars.get(position).getLink());
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return populars.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView =  itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
