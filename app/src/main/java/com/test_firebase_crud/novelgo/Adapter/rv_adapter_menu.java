package com.test_firebase_crud.novelgo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.test_firebase_crud.novelgo.Activity.MenuActivity;
import com.test_firebase_crud.novelgo.Model.Chapter;
import com.test_firebase_crud.novelgo.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class rv_adapter_menu extends RecyclerView.Adapter<rv_adapter_menu.ViewHolder> {
    private Context context;
    private ArrayList<Chapter> chapters;

    public rv_adapter_menu(Context context, ArrayList<Chapter> chapters) {
        this.context = context;
        this.chapters = chapters;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(chapters.get(position).getChapter());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Finding Novel Genre With "+ chapters.get(position).getChapter(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MenuActivity.class);
                intent.putExtra("from","genre");
                intent.putExtra("link", chapters.get(position).getLink());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView =itemView.findViewById(R.id.textView);
        }
    }
}
