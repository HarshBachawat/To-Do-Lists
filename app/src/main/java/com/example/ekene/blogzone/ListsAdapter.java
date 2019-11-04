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

import java.util.List;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListsViewHolder> {

    private List<Lists> lists;
    private Context context;
    Context mContext;

    public ListsAdapter(List<Lists> lists,Context context){
        this.lists = lists;
        this.context = context;
    }


    @Override
    public ListsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_items, parent, false);
        return new ListsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ListsViewHolder holder, int position) {
        final Lists list = lists.get(position);
        holder.title.setText(list.getTitle());

        final String desc = list.getDesc();
        if (!TextUtils.isEmpty(desc))
            holder.desc.setText(desc);
        else
            holder.desc.setVisibility(View.GONE);

        byte[] image = list.getImage();
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
                Intent listItems = new Intent(view.getContext(),TaskActivity.class);
                listItems.putExtra("list_id",list.getId());
                listItems.putExtra("hasLocation",list.isHasLocation());
                listItems.putExtra("hasTimestamp",list.isHasTimestamp());
                listItems.putExtra("list_name",list.getTitle());
                ((Activity) mContext).startActivityForResult(listItems,1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class ListsViewHolder extends RecyclerView.ViewHolder{
        public TextView title,desc;
        public ImageView image;
        public LinearLayout linearLayout;
        public ListsViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);
            title = (TextView)itemView.findViewById(R.id.list_title);
            desc = (TextView)itemView.findViewById(R.id.list_desc);
            image = (ImageView)itemView.findViewById(R.id.list_image);
        }

    }
}
