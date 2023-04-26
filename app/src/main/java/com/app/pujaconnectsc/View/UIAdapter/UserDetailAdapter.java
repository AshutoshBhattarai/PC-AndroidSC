package com.app.pujaconnectsc.View.UIAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.CusModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.Services.SessionManagement;
import com.app.pujaconnectsc.View.UI.Admin.AdminUserDetails;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.UserViewHolder> {
    private Context context;
    private ArrayList<CusModel> customerList;
    Activity ctx;
    String getRole;
    SessionManagement sm;
    int getId;

    public UserDetailAdapter(Context context, ArrayList<CusModel> customerList,Activity ctx) {
        this.context = context;
        this.customerList = customerList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_user_detail,parent,false);
        final UserViewHolder holder = new UserViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final CusModel temp = customerList.get(position);
        sm = new SessionManagement(context);
        holder.name.setText(temp.getName());
        holder.address.setText(temp.getAddress());
        holder.id.setText(String.valueOf(temp.getId()));
        holder.phone.setText(String.valueOf(temp.getPhone()));
        holder.email.setText(temp.getEmail());
        if(sm.getSessionAdminRole().equals("Admin"))
        {
            holder.changeRole.setVisibility(View.VISIBLE);
            holder.changeRole.setText(temp.getRole());
            holder.changeRole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   changeRoleDialog(temp.getId());
                }
            });
        }
    }

    private void changeRoleDialog(int userid) {

        ArrayList<String> list = new ArrayList<>();
        list.add("Select a Role");
        list.add("Admin");
        list.add("Customer");
        list.add("Delivery");
        Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_admin_change_role);
        Spinner spinner = dialog.findViewById(R.id.spinnerAdminRole);
        Button confirm = dialog.findViewById(R.id.btnChangeRole);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               getRole = list.get(i);
               getId = userid;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getRole.equals("Select a Role"))
                {
                    Toast.makeText(context, "Select a role", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendChangeRole(dialog);
                }
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return customerList == null ? 0 : customerList.size();
    }
    protected class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name,address,phone,id,email,changeRole;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.txtUDName);
            phone=itemView.findViewById(R.id.txtUDPhone);
            id=itemView.findViewById(R.id.txtUDId);
            address=itemView.findViewById(R.id.txtUDAddress);
            email =  itemView.findViewById(R.id.txtUDEmail);
            changeRole = itemView.findViewById(R.id.txtUDChangeRole);
        }
    }
    private void sendChangeRole(Dialog dialog)
    {
        CusModel cusModel = new CusModel();
        cusModel.setId(getId);
        cusModel.setRole(getRole);
        Call<CusModel> call = RetrofitConnect.getUserApi().updateUserRole(cusModel);
        call.enqueue(new Callback<CusModel>() {
            @Override
            public void onResponse(Call<CusModel> call, Response<CusModel> response) {
                if(response.code()==200)
                {
                    dialog.dismiss();
                    new AestheticDialog.Builder(ctx, DialogStyle.FLAT, DialogType.SUCCESS)
                            .setTitle("Role Updated")
                            .setMessage("Role Changed Sucessfully")
                            .setCancelable(false)
                            .setAnimation(DialogAnimation.FADE)
                            .setOnClickListener(new OnDialogClickListener() {
                                @Override
                                public void onClick(@NonNull AestheticDialog.Builder builder) {
                                    builder.dismiss();
                                    Intent intent = new Intent(ctx, AdminUserDetails.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ctx.startActivity(intent);
                                    ctx.finish();
                                }
                            }).show();
                }
                else if(response.code()==500)
                {
                    new AestheticDialog.Builder(ctx, DialogStyle.RAINBOW, DialogType.ERROR)
                            .setTitle("Error")
                            .setMessage(response.message())
                            .setCancelable(false)
                            .setAnimation(DialogAnimation.FADE)
                            .setOnClickListener(new OnDialogClickListener() {
                                @Override
                                public void onClick(@NonNull AestheticDialog.Builder builder) {
                                    builder.dismiss();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<CusModel> call, Throwable t) {
                new AestheticDialog.Builder(ctx, DialogStyle.RAINBOW, DialogType.ERROR)
                        .setTitle("Error")
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setAnimation(DialogAnimation.FADE)
                        .setOnClickListener(new OnDialogClickListener() {
                            @Override
                            public void onClick(@NonNull AestheticDialog.Builder builder) {
                                        builder.dismiss();
                            }
                        }).show();
            }
        });


    }
}

