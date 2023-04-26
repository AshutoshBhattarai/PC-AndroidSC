package com.app.pujaconnectsc.View.UI.User;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.Location;
import com.app.pujaconnectsc.Model.OrderModel2;
import com.app.pujaconnectsc.Model.PackDetailModel;
import com.app.pujaconnectsc.Model.PackPost;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.Services.SessionManagement;
import com.app.pujaconnectsc.View.UIAdapter.ProductAdapter2;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOrder extends AppCompatActivity implements OrderMap.MapDialogListener {
    RecyclerView rv;
    Button confirm, location, date, request;
    ArrayList<PackDetailModel> prodlist;
    ProductAdapter2 adapter;
    String lat, lng, userLat, userLng;
    SessionManagement sm;
    Boolean isDateClicked, isLocationClicked , locationAgreed ,dateAgreed;
    TextView total, name, phone;
    List<Address> addresses;
    String selectedDate;
    ArrayList<PackPost> packPosts;
    int id;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    double sum = 0;
    private FusedLocationProviderClient fusedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        confirm = findViewById(R.id.btnLockOrder);
        location = findViewById(R.id.btnOrderLocation);
        date = findViewById(R.id.btnSelectDate);
        request = findViewById(R.id.btnOrderRequest);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        rv = findViewById(R.id.rvConfirmOrder);
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        rv.setItemAnimator(new DefaultItemAnimator());
        sm = new SessionManagement(this);
        name = findViewById(R.id.txtCOName);
        phone = findViewById(R.id.txtCOPhone);
        name.setText(sm.getSessionUserName());
        phone.setText(String.valueOf(sm.getSessionUserPhone()));
        id = sm.getSessionUserId();
        isDateClicked = false;
        isLocationClicked = false;
        locationAgreed = false;
        dateAgreed = false;
        prodlist = new ArrayList<>();
        packPosts = new ArrayList<>();
        prodlist = (ArrayList<PackDetailModel>) getIntent().getSerializableExtra("Array");
        Collections.sort(prodlist, new Comparator<PackDetailModel>() {
            @Override
            public int compare(PackDetailModel packDetailModel, PackDetailModel t1) {
                return packDetailModel.getName().compareToIgnoreCase(t1.getName());
            }
        });
        adapter = new ProductAdapter2(getApplicationContext(), prodlist);
        rv.setAdapter(adapter);
        for (PackDetailModel m : prodlist) {

            double prodprice = m.getQuantity() * m.getPrice();
            sum = sum + prodprice;
        }
        if (prodlist.isEmpty()) {
            Toast.makeText(ConfirmOrder.this, "Order could not be placed!!\nPlease try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), HomePage.class));
            finish();
        }
        total = findViewById(R.id.txtTotal);
        total.setText(String.valueOf(sum));
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDateClicked = true;
                getLocation();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sum < 500)
                    Toast.makeText(ConfirmOrder.this, "Total should be above Rs.500 to place order", Toast.LENGTH_SHORT).show();
                else if((lat == null && !isLocationClicked && !locationAgreed) || (!isLocationClicked && !locationAgreed))
                    locationWarningDialog();
                else if ((lat == null || userLat == null) && !sm.hasLocation())
                    Toast.makeText(ConfirmOrder.this, "Please select delivery location", Toast.LENGTH_SHORT).show();
                else if((!isDateClicked && !dateAgreed)||(isDateClicked && selectedDate.equals("")))
                    dateWarningDialog();
                else if(isDateClicked && selectedDate.equals("Invalid"))
                    validDateDialog();
                else {
                    for (PackDetailModel m : prodlist) {
                        packPosts.add(new PackPost(m.getId(), m.getQuantity()));
                    }
                    sendOrder();
                    orderSucessDialog();
                    //Toast.makeText(ConfirmOrder.this, "Order Placed", Toast.LENGTH_SHORT).show();

                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDateClicked = true;
                showDatePicker();
            }
        });
    }

    private OrderModel2 createOrder() {
        OrderModel2 order = new OrderModel2();
        order.setUser_id(sm.getSessionUserId());
        order.setTotal(sum);
        if (lat == null)
            order.setLocation(new Location(sm.getUserLat(), sm.getUserLng()));
        else
            order.setLocation(new Location(lat, lng));
        order.setOrders(packPosts);
        if(isDateClicked)
        order.setDate(selectedDate);
        else
            order.setDate("");
        return order;
    }

    private void sendOrder() {
        Call<OrderModel2> call = RetrofitConnect.getPackageApi().sendOrder(createOrder());
        call.enqueue(new Callback<OrderModel2>() {
            @Override
            public void onResponse(Call<OrderModel2> call, Response<OrderModel2> response) {

            }

            @Override
            public void onFailure(Call<OrderModel2> call, Throwable t) {

            }
        });
    }

    @Override
    public void getLatLng(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(ConfirmOrder.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ConfirmOrder.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(ConfirmOrder.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(ConfirmOrder.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void getLocation() {
        checkPermission();
        fusedLocation.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                if (location != null) {
                    userLat = String.valueOf(location.getLatitude());
                    userLng = String.valueOf(location.getLongitude());
                    sm.saveLocation(userLat, userLng);
                    Bundle args = new Bundle();
                    args.putString("UserLat", userLat);
                    args.putString("UserLng", userLng);
                    OrderMap dialog = new OrderMap();
                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), "Map");
                } else {
                    checkGpsPermission();
                    Toast.makeText(ConfirmOrder.this, "Please Turn on location from Settings", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(ConfirmOrder.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkGpsPermission() {
        LocationManager manager;
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!(manager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            SettingsClient client = LocationServices.getSettingsClient(getApplicationContext());
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {

                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(ConfirmOrder.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                        }

                    }
                }
            });
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(ConfirmOrder.this
                ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1=i1+1;
                if(i<year || i1<month || i2<day)
                {
                    validDateDialog();
                   // Toast.makeText(ConfirmOrder.this, "Please Select a valid Date", Toast.LENGTH_SHORT).show();
                    selectedDate = "invalid";
                }
                else
                selectedDate =i+"/"+i1+"/"+i2;
                //Toast.makeText(ConfirmOrder.this, "Date : "+selectedDate, Toast.LENGTH_SHORT).show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    private void orderSucessDialog()
    {
        AestheticDialog.Builder builder = new AestheticDialog.Builder(ConfirmOrder.this,DialogStyle.FLAT,DialogType.SUCCESS);
        builder.setTitle("Order Placed");
        builder.setMessage("Thank you for ordering!! You will soon receive a confirmation call");
        builder.setAnimation(DialogAnimation.FADE);
        builder.setCancelable(false);
        builder.setOnClickListener(new OnDialogClickListener() {
            @Override
            public void onClick(@NonNull AestheticDialog.Builder builder) {
                builder.dismiss();
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }
    private void locationWarningDialog()
    {
        new AestheticDialog.Builder(ConfirmOrder.this,DialogStyle.FLAT,DialogType.WARNING)
                .setTitle("Location")
                .setMessage("If location not selected your current location will be used")
                .setAnimation(DialogAnimation.CARD)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        locationAgreed = true;
                        builder.dismiss();
                    }
                }).show();
    }
    private void dateWarningDialog()
    {
        new AestheticDialog.Builder(ConfirmOrder.this,DialogStyle.FLAT,DialogType.WARNING)
                .setTitle("Delivery Date")
                .setMessage("If delivery date not selected your order will be delivered within 7 days")
                .setAnimation(DialogAnimation.CARD)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        dateAgreed = true;
                        builder.dismiss();
                    }
                }).show();
    }
    private void validDateDialog()
    {
        new AestheticDialog.Builder(ConfirmOrder.this,DialogStyle.TOASTER,DialogType.WARNING)
                .setTitle("Delivery Date")
                .setMessage("Please Select a valid date!")
                .show();
    }
}
