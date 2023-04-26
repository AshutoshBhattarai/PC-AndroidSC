package com.app.pujaconnectsc.View.UIAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.PackDetailModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RVClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private ArrayList<PackDetailModel> productList;
    RVClickListener listener;

    public ProductAdapter(Context context, ArrayList<PackDetailModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    public ProductAdapter(Context context, ArrayList<PackDetailModel> productList, RVClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_detail_grid, parent, false);
        final ProductViewHolder myViewHolder = new ProductViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        final PackDetailModel temp = productList.get(position);
        holder.name.setText(temp.getName().trim());
        if (temp.getStock() <= 0) {
            holder.stock.setText("Out of Stock");
            holder.stock.setTextColor(context.getResources().getColor(R.color.red,context.getTheme()));
        }
        else
            holder.stock.setText(String.valueOf(temp.getStock()).trim());
        holder.price.setText(String.valueOf(temp.getPrice()).trim());
        holder.quantity.setText(String.valueOf(temp.getQuantity()).trim());
        Glide.with(context)
                .load(temp.getImage())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.no_image_2)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name, stock, price, quantity;
        ImageView image;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.prodImage);
            name = (TextView) itemView.findViewById(R.id.prodName);
            stock = (TextView) itemView.findViewById(R.id.prodStock);
            price = (TextView) itemView.findViewById(R.id.prodPrice);
            quantity = (TextView) itemView.findViewById(R.id.prodQuant);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(itemView, getAbsoluteAdapterPosition());
        }
    }
}
