package com.app.pujaconnectsc.View.UI.Admin;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.OrderModel;
import com.app.pujaconnectsc.Model.PackDetailModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.View.UIAdapter.ProductAdapter2;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderDetail extends AppCompatActivity {
    TextView id, date, total, txtstatus, textView50, tries,deliveryDate;
    RecyclerView rv;
    ImageView qr;
    ProgressBar pb;
    int pass;
    Button map, isDelivered, confirm, scanqr;
    Dialog dialog;
    ArrayList<PackDetailModel> productModels;
    ProductAdapter2 adapter;
    String disid, disdate, distotal, lat, lng, status ,disdelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setTitle("Order Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            disid = extras.getString("Id");
            distotal = extras.getString("Total");
            disdate = extras.getString("Date");
            lat = extras.getString("Lat");
            lng = extras.getString("Lng");
            status = extras.getString("Status");
            disdelivery = extras.getString("Delivery");

        }
        id = findViewById(R.id.txtODID);
        txtstatus = findViewById(R.id.txtODStatus);
        deliveryDate = findViewById(R.id.txtODDelivDate);
        total = findViewById(R.id.txtODTotal);
        qr = findViewById(R.id.orderDisplayQr);
        qr.setVisibility(View.GONE);

        textView50 = findViewById(R.id.textView50);
        textView50.setVisibility(View.GONE);
        date = findViewById(R.id.txtODDate);
        map = findViewById(R.id.btnOpenMap);
        map.setVisibility(View.VISIBLE);
        isDelivered = findViewById(R.id.btnDelivered);
        isDelivered.setVisibility(View.VISIBLE);
        if (status.equals("Delivered") || status.equals("Cancelled")) {
            isDelivered.setEnabled(false);
            map.setEnabled(false);
        }
        rv = findViewById(R.id.rvorderdetail);
        id.setText(disid);
        deliveryDate.setText(disdelivery);
        txtstatus.setText(status);
        date.setText(disdate);
        total.setText(distotal);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        rv.setItemAnimator(new DefaultItemAnimator());
        getOrderDetail();
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lat == null && lng == null)
                    Toast.makeText(AdminOrderDetail.this, "No Location data found", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=l"));
                    intent.setPackage("com.google.android.apps.maps");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });
        isDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOtpDialog();
            }
        });
    }

//        isDelivered.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Objects.equals(status, "Not Delivered")) {
//                    Call<OrderModel> call = RetrofitConnect.getPackageApi().changeStatus(new OrderModel(Integer.parseInt(disid)));
//                    call.enqueue(new Callback<OrderModel>() {
//                        @Override
//                        public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
//                            if (response.code() == 200) {
//                                Toast.makeText(AdminOrderDetail.this, "Delivery Status Updated", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getApplicationContext(), AdminViewOrders.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                            }
//                            else
//                                Toast.makeText(AdminOrderDetail.this, "Failed to change status", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(Call<OrderModel> call, Throwable t) {
//
//                        }
//                    });
//                }
//                else if(Objects.equals(status, "Delivered"))
//                    Toast.makeText(AdminOrderDetail.this, "Order already delivered", Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(AdminOrderDetail.this, "Order has been cancelled", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

    private void getOrderDetail() {
        Call<List<PackDetailModel>> call = RetrofitConnect.getPackageApi().getOrderDetail(disid);
        call.enqueue(new Callback<List<PackDetailModel>>() {
            @Override
            public void onResponse(Call<List<PackDetailModel>> call, Response<List<PackDetailModel>> response) {
                productModels = new ArrayList<>(response.body());
                if (productModels.isEmpty())
                    Toast.makeText(AdminOrderDetail.this, "No Details Found", Toast.LENGTH_SHORT).show();
                else {
                    Collections.sort(productModels, new Comparator<PackDetailModel>() {
                        @Override
                        public int compare(PackDetailModel packDetailModel, PackDetailModel t1) {
                            return packDetailModel.getName().compareToIgnoreCase(t1.getName());
                        }
                    });
                    adapter = new ProductAdapter2(getApplicationContext(), productModels);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<PackDetailModel>> call, Throwable t) {
                Toast.makeText(AdminOrderDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showOtpDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.order_confirmation);
        EditText code;
        code = dialog.findViewById(R.id.etconfirmcode);
        confirm = dialog.findViewById(R.id.btnConfirmOtp);
        scanqr = dialog.findViewById(R.id.btnScanQr);
        pb = dialog.findViewById(R.id.pbconfirmorder);
        pass = 3;
        tries = dialog.findViewById(R.id.codetries);
        tries.setText(String.valueOf(pass));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                if (code.getText().toString().equals("") || code.getText().toString().length() != 6) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(AdminOrderDetail.this, "Enter the Correct Code", Toast.LENGTH_SHORT).show();
                } else {
                    sendConfirmCode(code.getText().toString());
                    //Toast.makeText(AdminOrderDetail.this, code.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        scanqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AdminOrderDetail.this, "QR Scanner Open", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(),QRScannerView.class));
                openScanner();
            }
        });

        dialog.show();
    }

    private void openScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRScannerView.class);
        qrLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            pb.setVisibility(View.VISIBLE);
            //Toast.makeText(this, "Scan Code : "+result.getContents(), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendConfirmCode(result.getContents());
                }
            }, 1000);


        } else {
            Toast.makeText(this, "Code not Found", Toast.LENGTH_SHORT).show();
        }
    });

    private OrderModel orderCode(String code) {
        OrderModel order = new OrderModel();
        order.setId(Integer.parseInt(disid));
        order.setOtp(code);
        return order;
    }

    private void sendConfirmCode(String code) {
        Call<OrderModel> call = RetrofitConnect.getPackageApi().confirmDelivery(orderCode(code));
        call.enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                pb.setVisibility(View.GONE);
                if (response.code() == 200) {
                    dialog.dismiss();
                    sucessMessage();
                } else if (response.code() == 400) {
                    pass = pass - 1;
                    tries.setText(String.valueOf(pass));
                    if (pass == 0) {
                        confirm.setEnabled(false);
                        scanqr.setEnabled(false);
                        Call<OrderModel> call1 = RetrofitConnect.getPackageApi().changeStatus(new OrderModel(Integer.parseInt(disid)));
                        call1.enqueue(new Callback<OrderModel>() {
                            @Override
                            public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {

                            }

                            @Override
                            public void onFailure(Call<OrderModel> call, Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderModel> call, Throwable t) {

            }
        });
        // Toast.makeText(this, code + "  Sent", Toast.LENGTH_SHORT).show();
    }

    private void sucessMessage() {
        new AestheticDialog.Builder(AdminOrderDetail.this, DialogStyle.FLAT, DialogType.SUCCESS)
                .setTitle("Delivery Confiramtion")
                .setMessage("Delivery Confirmed Sucessfully !!")
                .setDarkMode(true)
                .setCancelable(false)
                .setAnimation(DialogAnimation.ZOOM)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        Intent intent = new Intent(getApplicationContext(), AdminViewOrders.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }
}
