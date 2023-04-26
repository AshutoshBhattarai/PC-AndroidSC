package com.app.pujaconnectsc.View.UI.User;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.app.pujaconnectsc.Model.CusModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.Services.SessionManagement;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDialog extends AppCompatDialogFragment {
    EditText phone,pass,name,address;
    Button update,cancel;
    String sname,spass,sphone,saddress,sid;
    long authphone;
    String authpass,authname,authaddress,authEmail;
    int authid;
    SessionManagement sm;
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.update_popup_window,null);

            builder.setView(view);
            sm=new SessionManagement(getContext());
            phone=view.findViewById(R.id.updatePass);
            name=view.findViewById(R.id.updateName);
            pass=view.findViewById(R.id.updatePhone);
            address=view.findViewById(R.id.updateAddress);
            update=view.findViewById(R.id.btnDisplayUpdate);
            cancel=view.findViewById(R.id.btnDisplayCancel);
            Bundle args=getArguments();

            sname=args.getString("Name");
            sphone=args.getString("Phone");
            spass=args.getString("Pass");
            sid=args.getString("Id");
            saddress=args.getString("Address");
            name.setText(sname);
            pass.setText(spass);
            phone.setText(sphone);
            address.setText(saddress);


            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateData();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        return builder.create();
    }
    private CusModel createUser() {
        CusModel reg = new CusModel();
        reg.setName(name.getText().toString());
        reg.setPass(pass.getText().toString());
        reg.setPhone(Long.parseLong(phone.getText().toString()));
        reg.setAddress(address.getText().toString());
        return reg;
    }
    private void updateData() {
        Call<CusModel> call = RetrofitConnect.getUserApi().updateUser(sid,createUser());
        call.enqueue(new Callback<CusModel>() {
            @Override
            public void onResponse(Call<CusModel> call, Response<CusModel> response) {
                if (response.code() == 200) {
                    dismiss();
                    updateSession();
                    Toast.makeText(getContext(), "Data Updated Sucessfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(),HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else if(response.code()==401)
                {
                    Toast.makeText(getContext(), "Phone number already exists", Toast.LENGTH_SHORT).show();
                }
                else if(response.code()==400)
                {
                    Toast.makeText(getContext(), "Error : "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CusModel> call, Throwable t) {
                dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateSession()
    {
        Call<List<CusModel>> call=RetrofitConnect.getUserApi().findUserById(sid);
        call.enqueue(new Callback<List<CusModel>>() {
            @Override
            public void onResponse(Call<List<CusModel>> call, Response<List<CusModel>> response) {
                if(response.code()==200)
                {
                    List<CusModel> data =response.body();
                    for (CusModel i : data) {
                        authphone = i.getPhone();
                        authpass = i.getPass();
                        authid = i.getId();
                        authname = i.getName();
                        authaddress = i.getAddress();
                        authEmail = i.getEmail();
                    }
                    sm.saveUserSession(authid, authname, authpass, authphone, authaddress,authEmail);
                }
                else
                {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CusModel>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
