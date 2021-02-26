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
import com.test_firebase_crud.novelgo.Model.Update;
import com.test_firebase_crud.novelgo.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public  class rv_adapter_item_update extends RecyclerView.Adapter<rv_adapter_item_update.ViewHolder> {

    private Context context;
    private ArrayList<Update> update;

    public rv_adapter_item_update(Context context, ArrayList<Update> update) {
        this.context = context;
        this.update = update;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_update, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewTitle.setText(update.get(position).getTitle());
        if (update.get(position).getChapter()==null) {
            holder.textViewChapter.setVisibility(View.GONE);
        }
        holder.textViewChapter.setText(update.get(position).getChapter());
        Glide.with(context).load(update.get(position).getImage()).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) context, holder.imageView, ViewCompat.getTransitionName(holder.imageView));
                intent.putExtra("image", update.get(position).getImage());
                intent.putExtra("title", update.get(position).getTitle());
                intent.putExtra("link", update.get(position).getLink());
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return update.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewTitle,textViewChapter;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView =  itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewChapter = itemView.findViewById(R.id.textViewChapter);
        }
    }
}
