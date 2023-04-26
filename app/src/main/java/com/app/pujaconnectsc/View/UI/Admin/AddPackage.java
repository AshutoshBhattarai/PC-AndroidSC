package com.app.pujaconnectsc.View.UI.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.pujaconnectsc.Model.PackModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPackage extends AppCompatActivity {
EditText name,desc,imgurl;
Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);
        name=findViewById(R.id.addPackName);
        desc=findViewById(R.id.addPackDesc);
        imgurl=findViewById(R.id.addPackImage);
        btnSubmit=findViewById(R.id.btnPackSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals(""))
                {
                    name.setError("Empty");
                    name.requestFocus();
                }
                else if(desc.getText().toString().equals(""))
                {
                    desc.setError("Empty");
                    desc.requestFocus();
                }
                else if(imgurl.getText().toString().equals(""))
                {
                    imgurl.setError("Empty");
                    imgurl.requestFocus();
                }

                else {
                    submitPackage();
                }
            }
        });
    }
    private PackModel savePack()
    {
        PackModel pack=new PackModel();
        pack.setTitle(name.getText().toString());
        pack.setDescription(desc.getText().toString());
        pack.setImgUrl(imgurl.getText().toString());
        return pack;
    }

    private void submitPackage() {
        Call<PackModel> call= RetrofitConnect.getPackageApi().savePackage(savePack());
        call.enqueue(new Callback<PackModel>() {
            @Override
            public void onResponse(Call<PackModel> call, Response<PackModel> response) {
                if (response.code() == 200) {
                    Toast.makeText(getApplicationContext(), "New Package Added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AdminPackages.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call<PackModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "New Package Added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), AdminPackages.class));
                finish();
            }
        });

    }
}