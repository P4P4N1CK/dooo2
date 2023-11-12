package com.dooo.android.adepter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.dooo.android.R;
import com.dooo.android.SubscriptionDetails;
import com.dooo.android.list.SubscriptionPlanList;

import java.util.Currency;
import java.util.List;

public class SubscriptionPlanListAdepter extends RecyclerView.Adapter<SubscriptionPlanListAdepter.MyViewHolder> {
    private Context mContext;
    private List<SubscriptionPlanList> mData;

    Context context;

    public SubscriptionPlanListAdepter(Context mContext, List<SubscriptionPlanList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.subscription_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setName(mData.get(position));
        holder.setTime(mData.get(position));
        holder.setAmount(mData.get(position));
        holder.setBackground(mData.get(position));

        holder.Subscription_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SubscriptionDetails.class);
                intent.putExtra("ID", mData.get(position).getID());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Title_TextView;
        TextView Time_TextView;
        TextView Amount_TextView;
        ImageView Subscription_item_bg;

        CardView Subscription_Item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Title_TextView = (TextView) itemView.findViewById(R.id.Name_TextView);
            Time_TextView = (TextView) itemView.findViewById(R.id.Time_TextView);
            Amount_TextView = (TextView) itemView.findViewById(R.id.Amount_TextView);
            Subscription_item_bg = (ImageView) itemView.findViewById(R.id.Subscription_item_bg);

            Subscription_Item = (CardView) itemView.findViewById(R.id.Subscription_Item);
        }

        void setName(SubscriptionPlanList Title_Text) { Title_TextView.setText(Title_Text.getName()); }
        void setTime(SubscriptionPlanList Time_Text) { Time_TextView.setText(String.valueOf(Time_Text.getTime()+"Days")); }
        void setAmount(SubscriptionPlanList Amount_Text) {
            String symbol = "$";
            switch (Amount_Text.getCurrency()) {
                case 0:
                    symbol = Currency.getInstance("INR").getSymbol();
                    break;
                case 1:
                    symbol = Currency.getInstance("USD").getSymbol();
                    break;
                default:
                    symbol = Currency.getInstance("USD").getSymbol();
            }
            Amount_TextView.setText(String.valueOf(symbol+Amount_Text.getAmount()));
        }
        void setBackground(SubscriptionPlanList BG_Image) { Glide.with(context)
                .load(BG_Image.getBackground())
                .placeholder(R.mipmap.ic_launcher)
                .into(Subscription_item_bg);
        }
    }
}
