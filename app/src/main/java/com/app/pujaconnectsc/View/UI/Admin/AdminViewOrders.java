package com.app.pujaconnectsc.View.UI.Admin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.OrderModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.View.UIAdapter.AdminOrderAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminViewOrders extends AppCompatActivity {
    RecyclerView rv;
    ArrayList<OrderModel> orderModels;
    AdminOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orders);
        getSupportActionBar().setTitle("Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        rv = findViewById(R.id.rvOrderDetail);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        rv.setItemAnimator(new DefaultItemAnimator());
        getOrderList();
    }

    private void getOrderList() {
        Call<List<OrderModel>> call = RetrofitConnect.getPackageApi().getAllOrders();
        call.enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                if (response.code() == 200) {
                    orderModels = new ArrayList<>(response.body());
                    Collections.sort(orderModels, new Comparator<OrderModel>() {
                        @Override
                        public int compare(OrderModel orderModel, OrderModel t1) {
                            String id1 = String.valueOf(orderModel.getId());
                            String id2 = String.valueOf(t1.getId());
                            return id1.compareToIgnoreCase(id2);
                        }
                    });
                    adapter = new AdminOrderAdapter(getApplicationContext(), orderModels);
                    rv.setAdapter(adapter);
                } else if (response.code() == 400) {

                }
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
//                Toast.makeText(ViewOrder.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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