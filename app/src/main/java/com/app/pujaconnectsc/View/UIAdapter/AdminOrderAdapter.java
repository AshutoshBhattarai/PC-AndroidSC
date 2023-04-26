package com.app.pujaconnectsc.View.UIAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.OrderModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.View.UI.Admin.AdminOrderDetail;

import java.util.ArrayList;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.AdminOrderVH> implements Filterable {
    private Context context;
    private ArrayList<OrderModel> orderArrayList;
    private ArrayList<OrderModel> orderArrayListTemp;

    public AdminOrderAdapter(Context context, ArrayList<OrderModel> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        orderArrayListTemp = new ArrayList<>(orderArrayList);

    }

    @NonNull
    @Override
    public AdminOrderAdapter.AdminOrderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_order_detail,parent,false);
        AdminOrderAdapter.AdminOrderVH myvh=new AdminOrderAdapter.AdminOrderVH(view);
        return myvh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderAdapter.AdminOrderVH holder, int position) {
        OrderModel temp=orderArrayList.get(position);
        holder.id.setText(String.valueOf(temp.getId()));
        holder.userid.setText(String.valueOf(temp.getUserid()));
        holder.total.setText(String.valueOf(temp.getTotal()));
        String date=temp.getDate().substring(0,10);
        holder.date.setText(date);
        holder.viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, AdminOrderDetail.class);
                intent.putExtra("Id",String.valueOf(temp.getId()));
                intent.putExtra("Total",String.valueOf(temp.getTotal()));
                intent.putExtra("Date",date);
                intent.putExtra("Lat",String.valueOf(temp.getLocation().getLat()));
                intent.putExtra("Lng",String.valueOf(temp.getLocation().getLng()));
                intent.putExtra("Status",temp.getStatus());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderArrayList==null ? 0 : orderArrayList.size();
    }

    protected class AdminOrderVH extends RecyclerView.ViewHolder{
        TextView id,total,date,viewDetail,userid;
        public AdminOrderVH(@NonNull View itemView) {
            super(itemView);
            userid=itemView.findViewById(R.id.txtOrderUserId);
            id=itemView.findViewById(R.id.txtOrderId);
            date=itemView.findViewById(R.id.txtOrderDate);
            total=itemView.findViewById(R.id.txtOrderTotal);
            viewDetail=itemView.findViewById(R.id.txtOrderViewDetail);
        }
    }
    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<OrderModel> filteredData = new ArrayList<>();
            if(charSequence.toString().isEmpty())
            {
                filteredData.addAll(orderArrayListTemp);
            }
            else
            {
                for(OrderModel m : orderArrayListTemp)
                {
                    String id = String.valueOf(m.getId());
                    if(id.toString().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filteredData.add(m);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            orderArrayList.clear();
            orderArrayList.addAll((ArrayList<OrderModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
