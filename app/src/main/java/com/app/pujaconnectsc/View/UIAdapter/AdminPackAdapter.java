package com.app.pujaconnectsc.View.UIAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.PackModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.View.UI.Admin.AdminPackDetail;
import com.app.pujaconnectsc.View.UI.User.PackDetail;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class AdminPackAdapter extends RecyclerView.Adapter<AdminPackAdapter.AdminPackageViewHolder>{
    private ArrayList<PackModel> packList;
    private Context context;


    public AdminPackAdapter(Context context, ArrayList<PackModel> packList) {
        this.context = context;
        this.packList = packList;
    }

    @NonNull
    @Override
    public AdminPackAdapter.AdminPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item, parent, false);
        final AdminPackAdapter.AdminPackageViewHolder myViewHolder = new AdminPackAdapter.AdminPackageViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPackAdapter.AdminPackageViewHolder holder, int position) {
        final PackModel temp = packList.get(position);
        holder.title.setText(packList.get(position).getTitle().trim());
        Glide.with(context)
                .load(packList.get(position).getImgUrl())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.no_image_2)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, AdminPackDetail.class);
                intent.putExtra("AdminPackId",temp.getId());
                intent.putExtra("AdminPackTitle",temp.getTitle());
                intent.putExtra("AdminPackDescription",temp.getDescription());
                intent.putExtra("AdminPackImage",temp.getImgUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PackDetail.class);
                intent.putExtra("Id",temp.getId());
                intent.putExtra("Title",temp.getTitle());
                intent.putExtra("Description",temp.getDescription());
                intent.putExtra("Image",temp.getImgUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return packList == null ? 0 : packList.size();
    }


    protected class AdminPackageViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView imageView;
        public AdminPackageViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txtTitle);
            imageView = (ImageView) view.findViewById(R.id.imgPack);
        }
    }
}
