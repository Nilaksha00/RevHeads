package com.example.revheads.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revheads.AdminHomeActivity;
import com.example.revheads.AdminItemViewActivity;
import com.example.revheads.R;
import com.example.revheads.model.RCItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RCItemListAdapter extends RecyclerView.Adapter<RCItemListAdapter.ItemViewHolder> {

    Context context;
    List<RCItem> itemList;
    public RCItemListAdapter(Context context, List<RCItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RCItemListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item, parent, false);
        return new RCItemListAdapter.ItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RCItemListAdapter.ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(itemList.get(position).getName());
        holder.price.setText("LKR " +itemList.get(position).getPrice()+".00");
        Picasso.get().load(itemList.get(position).getImage()).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdminItemViewActivity.class);
                intent.putExtra("name",itemList.get(position).getName());
                intent.putExtra("id",itemList.get(position).getId());
                intent.putExtra("price",itemList.get(position).getPrice());
                intent.putExtra("image",itemList.get(position).getImage());
                intent.putExtra("description",itemList.get(position).getDescription());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);
        }
    }
}
