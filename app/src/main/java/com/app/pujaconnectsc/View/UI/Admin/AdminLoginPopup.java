package com.app.pujaconnectsc.View.UI.Admin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.app.pujaconnectsc.Model.AdminModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.Services.SessionManagement;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//Also not in use
public class AdminLoginPopup extends AppCompatDialogFragment {
    EditText phone, pass;
    Button login;
    ProgressBar pb;
    SessionManagement sm;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admin_login_popup, null);
        builder.setView(view);
        sm = new SessionManagement(getContext());
        phone = view.findViewById(R.id.admLogPhone);
        pass = view.findViewById(R.id.admLogPass);
        login = view.findViewById(R.id.btnAdmLogin);
        pb = view.findViewById(R.id.pbAdminLogin);
        pb.setVisibility(View.VISIBLE);
        phone.setVisibility(View.GONE);
        pass.setVisibility(View.GONE);
        login.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AutoLogin();
            }
        },1000);
        return builder.create();
    }

    private AdminModel autoAuth() {
        AdminModel req = new AdminModel();
        req.setPhone(sm.getSessionUserPhone());
        return req;
    }

    private void AutoLogin() {
        Call<AdminModel> call = RetrofitConnect.getUserApi().autoAuthAdmin(autoAuth());
        call.enqueue(new Callback<AdminModel>() {
            @Override
            public void onResponse(Call<AdminModel> call, Response<AdminModel> response) {
                pb.setVisibility(View.GONE);
                if (response.code() == 200) {
                    startActivity(new Intent(getActivity(), DashBoard.class));
                    dismiss();
                }
                else if(response.code()==404)
                {
                    credentialLogin();
                }
            }

            @Override
            public void onFailure(Call<AdminModel> call, Throwable t) {
                pb.setVisibility(View.GONE);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void credentialLogin() {
        phone.setVisibility(View.VISIBLE);
        pass.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);
        phone.setText(String.valueOf(sm.getSessionUserPhone()));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText().toString().equals("")) {
                    phone.setError("No Empty");
                    phone.requestFocus();
                } else if (pass.getText().toString().equals("")) {
                    pass.setError("No Empty");
                    pass.requestFocus();
                } else {
                    adminAuth();
                }
            }
        });
    }

    private AdminModel authRequest() {
        AdminModel req = new AdminModel();
        req.setPhone(Long.parseLong(phone.getText().toString()));
        req.setPass(pass.getText().toString());
        return req;
    }

    private void adminAuth() {
        Call<AdminModel> call = RetrofitConnect.getUserApi().authAdmin(authRequest());
        call.enqueue(new Callback<AdminModel>() {
            @Override
            public void onResponse(Call<AdminModel> call, Response<AdminModel> response) {
                if (response.code() == 404) {
                    Toast.makeText(getContext(), "Wrong Credentials Try Again", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 200) {
                    startActivity(new Intent(getContext(), DashBoard.class));
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<AdminModel> call, Throwable t) {
                Toast.makeText(getContext(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
