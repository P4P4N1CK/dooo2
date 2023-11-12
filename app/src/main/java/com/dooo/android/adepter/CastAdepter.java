package com.dooo.android.adepter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dooo.android.R;
import com.dooo.android.list.CastList;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class CastAdepter extends RecyclerView.Adapter<CastAdepter.myViewHolder> {
    private Context context;
    private List<CastList> castList;

    public CastAdepter(Context context, List<CastList> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastAdepter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.cast_item_layout,parent,false);
        return new CastAdepter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastAdepter.myViewHolder holder, int position) {
        holder.setName(castList.get(position));
        holder.setRole(castList.get(position));
        holder.setImage(castList.get(position));
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView image;
        TextView name, role;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            role = itemView.findViewById(R.id.role);
        }

        void setName(CastList castList) {
            name.setText(castList.getName());
        }
        void setRole(CastList castList) {
            role.setText(castList.getRole());
        }
        void setImage(CastList castList) {
            Glide.with(context)
                    .load(castList.getImage())
                    .placeholder(R.drawable.ic_profile_user)
                    .into(image);
        }
    }
}
