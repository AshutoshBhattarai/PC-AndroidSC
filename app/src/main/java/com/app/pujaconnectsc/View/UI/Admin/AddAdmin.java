package com.app.pujaconnectsc.View.UI.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.pujaconnectsc.Model.AdminModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAdmin extends AppCompatActivity {
    EditText name, pass, phone,id;
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);
        name = findViewById(R.id.addAdmName);
        pass = findViewById(R.id.addAdmPass);
        phone = findViewById(R.id.addAdmPhone);
        id=findViewById(R.id.addAdmId);
        btnRegister=findViewById(R.id.btnAddAdm);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals(""))
                {
                    name.setError("Empty");
                    name.requestFocus();
                }
                else if(pass.getText().toString().equals(""))
                {
                    pass.setError("Empty");
                    pass.requestFocus();
                }
                else if(phone.getText().toString().equals(""))
                {
                    phone.setError("Empty");
                    phone.requestFocus();
                }
                else if(phone.getText().toString().length()!=10)
                {
                    phone.setError("10 digit number");
                    phone.requestFocus();
                }
                else if(id.getText().toString().equals(""))
                {
                    id.setError("Empty");
                    id.requestFocus();
                }
                else {
                    processData();
                }
            }
        });
    }
    private AdminModel createAdmin() {
        AdminModel reg = new AdminModel();
        reg.setUser(name.getText().toString());
        reg.setPass(pass.getText().toString());
        reg.setPhone(Long.parseLong(phone.getText().toString()));
        reg.setId(Integer.parseInt(id.getText().toString()));
        return reg;
    }

    private void processData() {
        Call<AdminModel> call= RetrofitConnect.getUserApi().saveAdmin(createAdmin());
        call.enqueue(new Callback<AdminModel>() {
            @Override
            public void onResponse(Call<AdminModel> call, Response<AdminModel> response) {
                if (response.code() == 200) {
                    Toast.makeText(getApplicationContext(), "User Registered ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AdminLogin.class));
                    finish();
                } else if (response.code() == 400) {
                    Toast.makeText(getApplicationContext(), "Phone number Already Exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "User Registered ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),AdminLogin.class));
                finish();
            }
        });


    }
}