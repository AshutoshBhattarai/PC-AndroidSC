package com.app.pujaconnectsc.View.UI.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.PackModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.Services.SessionManagement;
import com.app.pujaconnectsc.View.UI.Admin.AdminLoginPopup;
import com.app.pujaconnectsc.View.UIAdapter.PackageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PackageAdapter packageAdapter;
    private ArrayList<PackModel> packList;
    private SessionManagement sm;
    private NavigationView navigationView;
    private Button retry;
    private Boolean isAdmin = false;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        getSupportActionBar().setTitle("Home");
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View view = navigationView.getHeaderView(0);
        sm = new SessionManagement(getApplicationContext());
        TextView navName, navEmail;
        navName = view.findViewById(R.id.nav_head_name);
        if (sm.isLogged() == true) {
            navName.setText(sm.getSessionUserName());
        } else {
            navName.setText("");
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.MenuLogout:
                        //Toast.makeText(HomePage.this, "Login Pressed", Toast.LENGTH_SHORT).show();
                        logout();
                        return true;
                    case R.id.MenuAbout:
                        openAbout();
                        //Toast.makeText(HomePage.this, "About", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.MenuProfile:
                        openProfile();
                        //Toast.makeText(HomePage.this, "Profile", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.MenuOrders:
                        //Toast.makeText(HomePage.this, "My Orders", Toast.LENGTH_SHORT).show();
                        openOrders();
                        return true;
                    case R.id.MenuProductsDisplay:
                        //Toast.makeText(HomePage.this, "My Orders", Toast.LENGTH_SHORT).show();
                        openProductsDisplay();
                        return true;

                }

                return true;
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.packageRV);
        progressBar = (ProgressBar) findViewById(R.id.packLoading);
        retry = findViewById(R.id.btnRetry);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar.setVisibility(View.VISIBLE);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPackList();
                    }
                }, 1000);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPackList();
            }
        }, 1000);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    //Dot Icon Menu Code
    /**    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
        switch (item.getItemId()) {
            case R.id.MenuLogout:
                logout();
                return true;
            case R.id.MenuAbout:
                openAbout();
                return true;
            case R.id.MenuAdmin:
                openAdminLogin();
                return true;
            case R.id.MenuProfile:
                openProfile();
                return true;
            case R.id.MenuOrders:
                openOrders();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }}
        return super.onOptionsItemSelected(item);
    }**/

    private void getPackList() {
        retry.setVisibility(View.GONE);
        Call<List<PackModel>> call = RetrofitConnect.getPackageApi().getPackages();
        call.enqueue(new Callback<List<PackModel>>() {
            @Override
            public void onResponse(Call<List<PackModel>> call, Response<List<PackModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.code() == 200) {
                    packList = new ArrayList<>(response.body());
                    packageAdapter = new PackageAdapter(getApplicationContext(), packList);
                    recyclerView.setAdapter(packageAdapter);
                } else if (response.code() == 400) {
                    Toast.makeText(HomePage.this, "Sorry no information found", Toast.LENGTH_SHORT).show();
                    packList = new ArrayList<>();
                    packageAdapter = new PackageAdapter(getApplicationContext(), packList);
                    recyclerView.setAdapter(packageAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<PackModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
                Toast.makeText(HomePage.this, "Connection to Server Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logout() {
        sm.removeUserSession();
        sm.removeLocation();
        sm.removeAdminSession();
        notificationRemove();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private void openAbout() {
        startActivity(new Intent(getApplicationContext(), AboutUs.class));
    }

    private void openAdminLogin() {
        AdminLoginPopup dialog = new AdminLoginPopup();
        dialog.show(getSupportFragmentManager(), "Login");
        //startActivity(new Intent(getApplicationContext(), DashBoard.class));

    }

    private void openProfile() {
        Intent intent =new Intent(getApplicationContext(), ProfileUser.class);
        intent.putExtra("From","Home");
        startActivity(intent);
    }

    private void openOrders() {
        startActivity(new Intent(getApplicationContext(), ViewOrder.class));
    }

    private void openProductsDisplay() {
        startActivity(new Intent(getApplicationContext(), ProductsDisplay.class));
    }

    private void notificationRemove() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("CustomerNotify")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(HomePage.this, "Task Failed"+task.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}