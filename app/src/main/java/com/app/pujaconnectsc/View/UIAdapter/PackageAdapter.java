package com.app.pujaconnectsc.View.UIAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.PackModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.View.UI.User.PackDetail;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {
    private ArrayList<PackModel> packList;
    private Context context;


    public PackageAdapter(Context context, ArrayList<PackModel> packList) {
        this.context = context;
        this.packList = packList;
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item, parent, false);
        final PackageViewHolder myViewHolder = new PackageViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        final PackModel temp = packList.get(position);
        holder.title.setText(packList.get(position).getTitle().trim());
        holder.pb.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(packList.get(position).getImgUrl())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.pb.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.pb.setVisibility(View.GONE);
                        return false;

                    }
                })
                .error(R.drawable.no_image_2)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
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


    public class PackageViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView imageView;
        private ProgressBar pb;

        public PackageViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txtTitle);
            imageView = (ImageView) view.findViewById(R.id.imgPack);
            pb=view.findViewById(R.id.pbPackImg);
        }
    }
}
