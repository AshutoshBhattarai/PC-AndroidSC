package com.app.pujaconnectsc.View.UIAdapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.ProductModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RVClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ProductDisplayAdapter extends RecyclerView.Adapter<ProductDisplayAdapter.MyVh> implements Filterable {
    private Context context;
    private ArrayList<ProductModel> productModels;
    private ArrayList<ProductModel> productModelsTemp;
    RVClickListener listener;

    public ProductDisplayAdapter(Context context, ArrayList<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
    }

    public ProductDisplayAdapter(Context context, ArrayList<ProductModel> productModels, RVClickListener listener) {
        this.context = context;
        this.productModels = productModels;
        this.listener = listener;
        productModelsTemp = new ArrayList<>(productModels);
    }

    @NonNull
    @Override
    public MyVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_show_grid,parent,false);
        final MyVh myViewHolder=new MyVh(view);
        return myViewHolder;
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onBindViewHolder(@NonNull MyVh holder, int position) {
        final ProductModel temp=productModels.get(position);
        holder.name.setText(temp.getName().trim());
        holder.price.setText(String.valueOf(temp.getPrice()));
        if(temp.getStock()==0 || temp.getStock()<0)
            holder.stock.setText("Out of Stock");
        else
        holder.stock.setText(String.valueOf(temp.getStock()));
        Glide.with(context)
                .load(temp.getImage())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.no_image_2)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return productModels==null?0:productModels.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ProductModel> filteredData = new ArrayList<>();
            if(charSequence.toString().isEmpty())
            {
                filteredData.addAll(productModelsTemp);
            }
            else
            {
                for(ProductModel m : productModelsTemp)
                {
                    String id = String.valueOf(m.getId());
                    if(id.toString().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filteredData.add(m);
                    }
                    else if(m.getName().toString().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filteredData.add(m);
                    }

                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            productModels.clear();
            productModels.addAll((ArrayList<ProductModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };


    protected class MyVh extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView stock,price,name;
        ImageView img;
       public MyVh(@NonNull View itemView) {
           super(itemView);
           stock=itemView.findViewById(R.id.prodDisStock);
           name=itemView.findViewById(R.id.prodDisName);
           price=itemView.findViewById(R.id.prodDisPrice);
           img=itemView.findViewById(R.id.prodDisImg);
           itemView.setOnClickListener(this);

       }
        @Override
        public void onClick(View view) {
         listener.onClick(itemView,getBindingAdapterPosition());
        }
    }


}
