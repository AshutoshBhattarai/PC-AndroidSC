package com.app.pujaconnectsc.View.UI.Admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.CusModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.View.UIAdapter.UserDetailAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserDetails extends AppCompatActivity {
        RecyclerView rv;
        ArrayList<CusModel> customers;
        UserDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_details);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        rv=findViewById(R.id.rvUserDetails);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        rv.setItemAnimator(new DefaultItemAnimator());
          getUserList();
    }
    private void getUserList()
    {
        Call<List<CusModel>> call = RetrofitConnect.getUserApi().getCustomers();
        call.enqueue(new Callback<List<CusModel>>() {
            @Override
            public void onResponse(Call<List<CusModel>> call, Response<List<CusModel>> response) {
                if(response.code()==200)
                {
                    customers = new ArrayList<>(response.body());
                    Collections.sort(customers, new Comparator<CusModel>() {
                        @Override
                        public int compare(CusModel cusModel, CusModel t1) {
                            String id1 = String.valueOf(cusModel.getId());
                            String id2 = String.valueOf(t1.getId());
                            return id1.compareToIgnoreCase(id2);
                        }
                    });
                    adapter =new UserDetailAdapter(getApplicationContext(),customers,AdminUserDetails.this);
                    rv.setAdapter(adapter);
                }
                else
                    Toast.makeText(AdminUserDetails.this, response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<CusModel>> call, Throwable t) {
                Toast.makeText(AdminUserDetails.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
