package com.app.pujaconnectsc.View.UIAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.OrderModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.View.UI.User.OrderDetail;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MYVH> {
    private Context context;
    private ArrayList<OrderModel> orderArrayList;


    public OrderAdapter(Context context, ArrayList<OrderModel> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;

    }

    @NonNull
    @Override
    public MYVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail, parent, false);
        MYVH myvh = new MYVH(view);
        return myvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MYVH holder, int position) {
        OrderModel temp = orderArrayList.get(position);
        holder.id.setText(String.valueOf(temp.getId()));
        holder.total.setText(String.valueOf(temp.getTotal()));
        String date = temp.getDate().substring(0, 10);
        String date2;
        if (temp.getDeliveryDate().equals(""))
            date2 = "-";
        else
            date2 = temp.getDeliveryDate().substring(0, 10);
        holder.date.setText(date);
        holder.viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetail.class);
                intent.putExtra("Id", String.valueOf(temp.getId()));
                intent.putExtra("Total", String.valueOf(temp.getTotal()));
                intent.putExtra("Date", date);
                intent.putExtra("Status", temp.getStatus());
                intent.putExtra("Otp", temp.getOtp());
                intent.putExtra("DeliveryDate", date2);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderArrayList == null ? 0 : orderArrayList.size();
    }


    class MYVH extends RecyclerView.ViewHolder {
        TextView id, total, date, viewDetail;

        public MYVH(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.txtOrderId);
            date = itemView.findViewById(R.id.txtOrderDate);
            total = itemView.findViewById(R.id.txtOrderTotal);
            viewDetail = itemView.findViewById(R.id.txtOrderViewDetail);
        }
    }
}
