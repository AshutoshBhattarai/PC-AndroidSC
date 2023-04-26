package com.app.pujaconnectsc.View.UI.User;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.ProductModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RVClickListener;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.View.UIAdapter.ProductDisplayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsDisplay extends AppCompatActivity {
    RecyclerView rv;
    ArrayList<ProductModel> productModelArrayList;
    ProductDisplayAdapter adapter;
    RVClickListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_display);
        rv = findViewById(R.id.rvDisplayProducts);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        rv.setItemAnimator(new DefaultItemAnimator());
        getSupportActionBar().setTitle("Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setClickListener();
        getProdList();
    }
    private void getProdList() {
        Call<List<ProductModel>> call = RetrofitConnect.getPackageApi().getProdcuts();
        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.code() == 200) {
                    productModelArrayList = new ArrayList<>(response.body());
                    Collections.sort(productModelArrayList, new Comparator<ProductModel>() {
                        @Override
                        public int compare(ProductModel productModel, ProductModel t1) {
                            return productModel.getName().compareToIgnoreCase(t1.getName());
                        }
                    });
                    adapter=new ProductDisplayAdapter(getApplicationContext(),productModelArrayList,listener);
                    rv.setAdapter(adapter);
                } else if (response.code() == 400) {
                    Toast.makeText(getApplicationContext(), "There are no products", Toast.LENGTH_SHORT).show();
                    productModelArrayList = new ArrayList<>();
                    adapter=new ProductDisplayAdapter(getApplicationContext(),productModelArrayList);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setClickListener() {
        listener = new RVClickListener() {
            @Override
            public void onClick(View v, int pos) {

            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}