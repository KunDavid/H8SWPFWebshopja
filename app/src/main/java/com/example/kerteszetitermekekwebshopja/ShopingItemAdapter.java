package com.example.kerteszetitermekekwebshopja;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

public class ShopingItemAdapter extends RecyclerView.Adapter<ShopingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<ShopingItem> mShopingIemData;
    private ArrayList<ShopingItem> mShopingIemDataAll;
    private Context mContext;
    private int lastPosition = -1;

    ShopingItemAdapter(Context context, ArrayList<ShopingItem> itemsData){
        this.mShopingIemData = itemsData;
        this.mShopingIemDataAll = itemsData;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShopingItemAdapter.ViewHolder holder, int position) {
        int random = new Random().nextInt((100 - 1) + 1) + 1;
        ShopingItem currentItem = mShopingIemData.get(position);

        holder.bindTo(currentItem);

        if(holder.getAdapterPosition() > lastPosition && random > 50){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
        if(holder.getAdapterPosition() > lastPosition && random <= 50){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mShopingIemData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShopingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mShopingIemDataAll.size();
                results.values = mShopingIemDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(ShopingItem item : mShopingIemDataAll) {
                    if(item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mShopingIemData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mPriceText = itemView.findViewById(R.id.price);
            mItemImage = itemView.findViewById(R.id.itemImage);

            // itemView.findViewById(R.id.add_to_cart).setOnClickListener(view -> ((ShopListActivity)mContext).updateAlertIcon());

        }

        public void bindTo(ShopingItem currentItem) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());

            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
            itemView.findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Log.d("Activity", "Kosárba gomb lenyomva");
                    ((ShopListActivity)mContext).updateAlertIcon(currentItem);
                }
            });

            itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Log.d("Activity", "Kitöröl gomb lenyomva");
                    ((ShopListActivity)mContext).deleteI(currentItem);
                }
            });
        }
    };
}


