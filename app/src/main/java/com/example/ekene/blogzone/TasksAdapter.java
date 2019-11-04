package com.example.ekene.blogzone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    private ArrayList<Tasks> tasks;
    private Context context;
    Context mContext;

    public TasksAdapter(ArrayList<Tasks> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_task, parent, false);
        mContext = parent.getContext();
        return new TasksViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        final Tasks task = tasks.get(position);
        holder.title.setText(task.getTitle());

        final String desc = task.getDesc(),timestamp = task.getTimestamp();
        if (!TextUtils.isEmpty(desc))
            holder.desc.setText(task.getDesc());
        else
            holder.desc.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(timestamp))
            holder.timestamp.setText(task.getTimestamp());
        else
            holder.timestamp.setVisibility(View.GONE);

        byte[] image = task.getImage();
        Bitmap bitmap;
        if (image != null) {
            bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
            holder.image.setImageBitmap(bitmap);
        }
        else
            holder.image.setVisibility(View.GONE);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent singleTask = new Intent(view.getContext(), SingleTaskActivity.class);
                singleTask.putExtra("task_id",task.getId());
                ((Activity) mContext).startActivityForResult(singleTask,3);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TasksViewHolder extends RecyclerView.ViewHolder{
        public TextView title,desc,timestamp;
        public ImageView image;
        public LinearLayout linearLayout;
        public TasksViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);
            title = (TextView)itemView.findViewById(R.id.task_title);
            desc = (TextView)itemView.findViewById(R.id.task_desc);
            timestamp = (TextView)itemView.findViewById(R.id.task_ts);
            image = (ImageView)itemView.findViewById(R.id.task_image);
        }

    }
}
