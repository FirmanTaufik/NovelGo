package com.test_firebase_crud.novelgo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test_firebase_crud.novelgo.Activity.ChapterActivity;
import com.test_firebase_crud.novelgo.Database.MyDatabaseHelper;
import com.test_firebase_crud.novelgo.Model.Chapter;
import com.test_firebase_crud.novelgo.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class rv_adapter_chapter extends RecyclerView.Adapter<rv_adapter_chapter.ViewHolder> {
    private Context context;
    private ArrayList<Chapter> chapters;
    int row_index = -1;
    int row_index2 =-1;
    private MyDatabaseHelper myDatabaseHelper ; ;
    public rv_adapter_chapter(Context context, ArrayList<Chapter> chapters) {
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
        myDatabaseHelper  = new MyDatabaseHelper(context);
        if (myDatabaseHelper.getTontonCount(chapters.get(position).getChapter(),
                chapters.get(position).getLink())>0) {

            row_index2=position;
            holder.relative.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryButton));
            holder.textView.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.relative.setBackgroundColor(context.getResources().getColor(R.color.white));

        }
            holder.textView.setText(chapters.get(position).getChapter());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index =position;
                notifyDataSetChanged();
                Intent intent = new Intent(context, ChapterActivity.class);
                intent.putExtra("link", chapters.get(position).getLink());
                intent.putExtra("chapter", chapters.get(position).getChapter());
                context.startActivity(intent);

            }
        });

        if(row_index ==position || row_index2 ==position){
            holder.relative.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryButton));
            holder.textView.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.relative.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private CardView cardView;
        private RelativeLayout relative;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView =itemView.findViewById(R.id.textView);
            cardView = itemView.findViewById(R.id.cardView);
            relative = itemView.findViewById(R.id.relative);
        }
    }
}
