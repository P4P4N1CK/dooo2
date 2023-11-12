package com.dooo.android.adepter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dooo.android.R;
import com.dooo.android.list.CommentList;
import com.dooo.android.list.EpisodeList;

import java.util.List;

public class CommentListAdepter extends RecyclerView.Adapter<CommentListAdepter.myViewHolder> {
    private int myID;
    private Context context;
    private List<CommentList> data;

    public CommentListAdepter(int myID, Context context, List<CommentList> data) {
        this.myID = myID;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position).getUserID() == myID) {
            return R.layout.my_comment_item;
        } else {
            return R.layout.comment_item;
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view;
        if(viewType == R.layout.comment_item){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_comment_item, parent, false);
        }
        return new CommentListAdepter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.setUserName(data.get(position));
        holder.setComment(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView userName, comment;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            comment = itemView.findViewById(R.id.comment);
        }

        void setUserName(CommentList commentList) {
            userName.setText(commentList.getUserName());
        }
        void setComment(CommentList commentList) {
            comment.setText(commentList.getComment());
        }
    }
}
