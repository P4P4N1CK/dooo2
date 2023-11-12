package com.dooo.android.adepter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dooo.android.R;
import com.dooo.android.list.UpdateList;

import java.util.List;

public class UpdateListAdepter extends RecyclerView.Adapter<UpdateListAdepter.MyViewHolder> {

    private Context mContext;
    private List<UpdateList> mData;

    public UpdateListAdepter(Context mContext, List<UpdateList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.update_list_item,parent,false);
        return new UpdateListAdepter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Whats_New;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Whats_New = (TextView) itemView.findViewById(R.id.Whats_New_Text);
        }

        void setText(UpdateList text) {
            Whats_New.setText(text.getWhats_New());
        }
    }
}
