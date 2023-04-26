package com.app.pujaconnectsc.View.UI.User;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.OrderModel;
import com.app.pujaconnectsc.Model.OrderModel2;
import com.app.pujaconnectsc.Model.PackDetailModel;
import com.app.pujaconnectsc.Model.PackPost;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.Services.SessionManagement;
import com.app.pujaconnectsc.View.UIAdapter.ProductAdapter2;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetail extends AppCompatActivity {
    private static String BaseUrl;
    private TextView id, date, total, status, deliveryDate;
    private RecyclerView rv;
    private Button bill, cancel;
    private ImageView qr;
    private ArrayList<PackDetailModel> productModels;
    private ProductAdapter2 adapter;
    private ArrayList<PackPost> packPost;
    private SessionManagement sm;
    private String disid, disdate, distotal, disstatus, disotp, disdelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Bundle extras = getIntent().getExtras();
        BaseUrl = getResources().getString(R.string.baseURL);
        if (extras != null) {
            disid = extras.getString("Id");
            distotal = extras.getString("Total");
            disdate = extras.getString("Date");
            disstatus = extras.getString("Status");
            disotp = extras.getString("Otp");
            disdelivery = extras.getString("DeliveryDate");
        }
        id = findViewById(R.id.txtODID);
        status = findViewById(R.id.txtODStatus);
        total = findViewById(R.id.txtODTotal);
        qr = findViewById(R.id.orderDisplayQr);
        date = findViewById(R.id.txtODDate);
        bill = findViewById(R.id.btnDownloadBill);
        cancel = findViewById(R.id.btnCancelOrder);
        deliveryDate = findViewById(R.id.txtODDelivDate);
        cancel.setVisibility(View.VISIBLE);
        bill.setVisibility(View.VISIBLE);
        rv = findViewById(R.id.rvorderdetail);
        sm = new SessionManagement(getApplicationContext());
        id.setText(disid);
        status.setText(disstatus);
        date.setText(disdate);
        total.setText(distotal);
        deliveryDate.setText(disdelivery);
        Glide.with(getApplicationContext())
                .load(BaseUrl + "/images/qr/" + disid)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.no_image_2)
                .into(qr);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShowQrDialog();
            }
        });
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        rv.setItemAnimator(new DefaultItemAnimator());
        getOrderDetail();
        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInvoiceInfo();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrderDialog();
            }
        });

    }

    private void openShowQrDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.qrcode_display_dialog);
        Button close;
        close = dialog.findViewById(R.id.btnCloseQrDiag);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ImageView qrcode;
        qrcode = dialog.findViewById(R.id.showQrLarge);
        Glide.with(getApplicationContext())
                .load(BaseUrl + "/images/qr/" + disid)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.no_image_2)
                .into(qrcode);

        TextView code;
        code = dialog.findViewById(R.id.showQrLargeCode);
        code.setText(disotp);
        dialog.show();
    }

    private void cancelOrderDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Order")
                .setMessage("Do you really want to cancel this order?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        orderCancelRequest();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private OrderModel2 updateStock() {
        OrderModel2 orderModel2 = new OrderModel2();
        orderModel2.setUser_id(Integer.parseInt(disid));
        orderModel2.setOrders(packPost);
        return orderModel2;
    }

    private void orderCancelRequest() {
        int diff = (int) checkDateDiff();
        if (Objects.equals(disstatus, "Not Delivered")) {
            if (diff < 1)
                Toast.makeText(this, "This order has already been prepared ! Please contact us for further details", Toast.LENGTH_LONG).show();
            else {
                Call<OrderModel2> call = RetrofitConnect.getPackageApi().cancelOrder(updateStock());
                call.enqueue(new Callback<OrderModel2>() {
                    @Override
                    public void onResponse(Call<OrderModel2> call, Response<OrderModel2> response) {
                        if (response.code() == 200) {
                            Toast.makeText(getApplicationContext(), "Your Order has been Cancelled", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), ViewOrder.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(OrderDetail.this, "Failed to change status", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderModel2> call, Throwable t) {
                        Toast.makeText(OrderDetail.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        } else if (Objects.equals(disstatus, "Delivered"))
            Toast.makeText(this, "This order has already been delivered", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "This order has already been cancelled", Toast.LENGTH_SHORT).show();
    }

    private void getOrderDetail() {
        Call<List<PackDetailModel>> call = RetrofitConnect.getPackageApi().getOrderDetail(disid);
        call.enqueue(new Callback<List<PackDetailModel>>() {
            @Override
            public void onResponse(Call<List<PackDetailModel>> call, Response<List<PackDetailModel>> response) {
                productModels = new ArrayList<>(response.body());
                if(productModels.isEmpty())
                    Toast.makeText(OrderDetail.this, "No Details Found", Toast.LENGTH_SHORT).show();
                else {
                    Collections.sort(productModels, new Comparator<PackDetailModel>() {
                        @Override
                        public int compare(PackDetailModel packDetailModel, PackDetailModel t1) {
                            return packDetailModel.getName().compareToIgnoreCase(t1.getName());
                        }
                    });
                    adapter = new ProductAdapter2(getApplicationContext(), productModels);
                    packPost = new ArrayList<>();
                    for (PackDetailModel m : productModels) {
                        packPost.add(new PackPost(m.getId(), m.getQuantity()));
                    }

                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<PackDetailModel>> call, Throwable t) {

            }
        });
    }

    private OrderModel createInvInfo() {
        OrderModel model = new OrderModel();
        model.setUserid(sm.getSessionUserId());
        model.setId(Integer.parseInt(disid));
        return model;
    }

    private void getInvoiceInfo() {
        Call<OrderModel> call = RetrofitConnect.getPackageApi().getInvoice(createInvInfo());
        call.enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                if (response.code() == 200) {
                    downloadFile();
                    Toast.makeText(OrderDetail.this, "Downloading file ......", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(OrderDetail.this, "Sorry!! Invoice Not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderModel> call, Throwable t) {
                Toast.makeText(OrderDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadFile() {
        Uri uri = Uri.parse(BaseUrl + "/order/invoice/" + sm.getSessionUserId() + "/" + disid);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Bill." + disdate + "-" + disid + "-" + sm.getSessionUserId() + ".pdf");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
        request.allowScanningByMediaScanner();// if you want to be available from media players
        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private long checkDateDiff() {
        final DateTimeFormatter formatter;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            final LocalDate firstDate = LocalDate.parse(disdate, formatter);
            final LocalDate secondDate = LocalDate.parse(disdelivery, formatter);
            final long days = ChronoUnit.DAYS.between(firstDate, secondDate);
            return days;
        }
        return 0;
    }
}