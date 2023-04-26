package com.app.pujaconnectsc.View.UI.User;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.PackDetailModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RVClickListener;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.View.UIAdapter.ProductAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackDetail extends AppCompatActivity {
    private TextView txtTitle, txtDesc , diagName;
    private ImageView img;
    private Button order,delete;
    private RecyclerView rv;
    private ProductAdapter productAdapter;
    private ArrayList<PackDetailModel> packDetailList,packDetailModels;
    private String image, title,desc,amount;
    private EditText quant;
    private Button btnadd;
    private int id;
    private ProgressBar pb,pb2;
    private RVClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_detail);
        txtTitle = findViewById(R.id.txtPackTitle);
        txtDesc = findViewById(R.id.txtPackDesc);
        img = findViewById(R.id.packImgPP);
        pb=findViewById(R.id.pbCusPack);
        pb2=findViewById(R.id.pbPackdetail);
        order = findViewById(R.id.btnOrder);
        order.setVisibility(View.GONE);
        pb2.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            id = (int)getIntent().getSerializableExtra("Id");
            desc = (String) getIntent().getSerializableExtra("Description");
            txtDesc.setMovementMethod(new ScrollingMovementMethod());
            txtDesc.setText(desc.toString().trim());
            title = extras.getString("Title");
            txtTitle.setText(title);
            image = extras.getString("Image");
            Glide.with(getApplicationContext())
                    .load(image)
                    .skipMemoryCache(true)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            pb2.setVisibility(View.GONE);
                            return false;

                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            pb2.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.drawable.no_image_2)
                    .into(img);


        }
        pb.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getPackDetail();
            }
        },800);

        buildRecyclerView();
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(PackDetail.this, "Order Placed", Toast.LENGTH_SHORT).show();
                ArrayList<PackDetailModel> prod = new ArrayList<>();
                for (PackDetailModel m : packDetailList) {
                    if (!(m.getQuantity() == 0 || m.getStock()==0)&&m.getQuantity()<=m.getStock()){
                        prod.add(m);
                    }
                }
                Intent intent = new Intent(getApplicationContext(), ConfirmOrder.class);
                intent.putExtra("Array", prod);
                startActivity(intent);
            }
        });
    }

    private void getPackDetail() {
        packDetailList=new ArrayList<>();
        Call<List<PackDetailModel>> call= RetrofitConnect.getPackageApi().getPackDetail(String.valueOf(id));
        call.enqueue(new Callback<List<PackDetailModel>>() {
            @Override
            public void onResponse(Call<List<PackDetailModel>> call, Response<List<PackDetailModel>> response) {
                if(response.code()==200)
                {
                    pb.setVisibility(View.GONE);
                    order.setVisibility(View.VISIBLE);
                    packDetailModels=new ArrayList<>(response.body());
                    for(PackDetailModel m:packDetailModels)
                    {
                        packDetailList.add(m);
                    }
                    Collections.sort(packDetailList, new Comparator<PackDetailModel>() {
                        @Override
                        public int compare(PackDetailModel packDetailModel, PackDetailModel t1) {
                            return packDetailModel.getName().compareToIgnoreCase(t1.getName());
                        }
                    });
                    productAdapter = new ProductAdapter(getApplicationContext(), packDetailList, listener);
                    rv.setAdapter(productAdapter);
                }
                else{
                    pb.setVisibility(View.GONE);
                    Toast.makeText(PackDetail.this, "Information not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PackDetailModel>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
            }
        });

    }
    private void buildRecyclerView() {
        setOnClickListener();
        rv = findViewById(R.id.productRV);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        rv.setItemAnimator(new DefaultItemAnimator());

    }

    private void setOnClickListener() {
        listener = new RVClickListener() {
            @Override
            public void onClick(View v, int pos) {
                showCusDialog(pos);
            }
        };
    }

    void showCusDialog(int pos) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_customize_package);
        quant = dialog.findViewById(R.id.cusQuantity);
        btnadd = dialog.findViewById(R.id.btnCustReq);
        diagName = dialog.findViewById(R.id.txtDiagProdName);
        delete=dialog.findViewById(R.id.btnCusDelete);
        delete.setVisibility(View.GONE);
        quant.setText(String.valueOf(packDetailList.get(pos).getQuantity()));
        diagName.setText(packDetailList.get(pos).getName());
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=String.valueOf(packDetailList.get(pos).getId());
                String image=packDetailList.get(pos).getImage();
                String name = packDetailList.get(pos).getName();
                String stock = String.valueOf(packDetailList.get(pos).getStock());
                String price = String.valueOf(packDetailList.get(pos).getPrice());
                if(quant.getText().toString().equals(""))
                    amount="0";
                else
                amount = quant.getText().toString();
                packDetailList.set(pos, new PackDetailModel(Integer.parseInt(id),name, Double.parseDouble(price), Integer.parseInt(stock), Integer.parseInt(amount),image));
                productAdapter.notifyItemChanged(pos);
                //Toast.makeText(getApplicationContext(), "Amount = " + amount, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}