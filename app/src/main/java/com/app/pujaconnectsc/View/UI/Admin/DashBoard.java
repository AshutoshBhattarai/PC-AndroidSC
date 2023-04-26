package com.app.pujaconnectsc.View.UI.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.SessionManagement;
import com.app.pujaconnectsc.View.UI.User.LoginActivity;
import com.app.pujaconnectsc.View.UI.User.ProfileUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

public class DashBoard extends AppCompatActivity {
    ImageView order, pack, prod, user;
    TextView txtorder, txtpack, txtprod, txtuser;
    private SessionManagement sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        getSupportActionBar().setTitle("Dashboard");
        sm = new SessionManagement(getApplicationContext());
        order = findViewById(R.id.dbimgorder);
        pack = findViewById(R.id.dbimgpack);
        prod = findViewById(R.id.dbimgprod);
        user = findViewById(R.id.dbimguser);
        txtorder = findViewById(R.id.dbtxtorder);
        txtpack = findViewById(R.id.dbtxtpack);
        txtprod = findViewById(R.id.dbtxtprod);
        txtuser = findViewById(R.id.dbtxtuser);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), AdminViewOrders.class));
            }
        });
        txtorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AdminViewOrders.class));
                //Toast.makeText(DashBoard.this, "Orders", Toast.LENGTH_SHORT).show();
            }
        });

        pack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sm.getSessionAdminRole().equals("Delivery"))
                {
                    warnMessage();
                }
                else{
                    startActivity(new Intent(getApplicationContext(), AdminPackages.class));
                }

            }
        });
        txtpack.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View view) {
                if(sm.getSessionAdminRole().equals("Delivery"))
                {
                    warnMessage();
                }
                else
                startActivity(new Intent(getApplicationContext(), AdminPackages.class));
            }
        });

        prod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(sm.getSessionAdminRole().equals("Delivery"))
                {
                    warnMessage();
                }
               else
                startActivity(new Intent(getApplicationContext(), AdminDisplayProducts.class));
                //Toast.makeText(DashBoard.this, "Products", Toast.LENGTH_SHORT).show();
            }
        });
        txtprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sm.getSessionAdminRole().equals("Delivery"))
                {
                    warnMessage();
                }
                else
                startActivity(new Intent(getApplicationContext(), AdminDisplayProducts.class));
                //Toast.makeText(DashBoard.this, "Products", Toast.LENGTH_SHORT).show();
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AdminUserDetails.class));
            }
        });
        txtuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AdminUserDetails.class));
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MenuLogout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void openprofile() {
        Intent intent = new Intent(getApplicationContext(), ProfileUser.class);
        intent.putExtra("From","Dashboard");
        startActivity(intent);
    }

    private void logout() {
        sm.removeAdminSession();
        sm.removeUserSession();
        removeNotification();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
    private void removeNotification()
    {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("AdminNotify").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                {
                    Toast.makeText(DashBoard.this, "Task Failed : "+task.getResult(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void warnMessage()
    {
        new AestheticDialog.Builder(DashBoard.this, DialogStyle.FLAT, DialogType.WARNING)
                .setTitle("No Permission")
                .setMessage("You are not allowed to use these features!!")
                .setAnimation(DialogAnimation.SPLIT)
                .setCancelable(false)
                .setDarkMode(true)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        builder.dismiss();
                    }
                }).show();
    }
}