package com.example.hardapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.ViewHolder> {


    private Context context;
    private List<Item> items;

    public MyAdaptor(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view=LayoutInflater.from(context).inflate(R.layout.item_view, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.itemName.setText(items.get(i).getName());
        viewHolder.itemPrice.setText("Rs."+items.get(i).getPrice());

        if(!items.get(i).getPicture().equals("") ||items.get(i).getPicture()!=null){
            RequestOptions requestOptions= new RequestOptions();
            requestOptions=requestOptions.transform(new RoundedCorners(200));
            Glide.with(context).load(items.get(i).getPicture())
                    .apply(requestOptions)
                    .into(viewHolder.itemPic);
        }else{

            viewHolder.itemPic.setImageDrawable(context.getResources().getDrawable(R.drawable.for_sale));
        }

    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout item;
        public TextView itemName;
        public TextView itemPrice;
        public ImageView itemPic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName=itemView.findViewById(R.id.itemName);
            itemPrice=itemView.findViewById(R.id.price);
            itemPic=itemView.findViewById(R.id.itemPic);
            item=itemView.findViewById(R.id.gridItem);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.gridItem:
                    Toast.makeText(context, "Clicked on::"+getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(context,ItemDetailView.class);
                    intent.putExtra("Item", items.get(getAdapterPosition()));
                    context.startActivity(intent);
                    break;

            }
        }
    }
}
