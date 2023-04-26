package com.app.pujaconnectsc.View.UI.Admin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.app.pujaconnectsc.Model.PackModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPackagePopup extends AppCompatDialogFragment {
    EditText name,desc,imgurl;
    TextView delete;
    Button btnSubmit,btnImg;
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.pack_add_popup,null);
        builder.setView(view);
        name=view.findViewById(R.id.addPackName);
        desc=view.findViewById(R.id.addPackDesc);
        imgurl=view.findViewById(R.id.addPackImage);
        delete=view.findViewById(R.id.txtDeletePack2);
        delete.setVisibility(View.GONE);
        btnImg = view.findViewById(R.id.addPackImagebtn);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 0);
            }
        });
        btnSubmit=view.findViewById(R.id.btnPackSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals(""))
                {
                    name.setError("Please enter data");
                    name.requestFocus();
                }
                else if(desc.getText().toString().equals(""))
                {
                    desc.setError("Please enter data");
                    desc.requestFocus();
                }
                else {
                    submitPackage();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getContext(), AdminPackages.class));
                            getActivity().finish();
                            Toast.makeText(getActivity(), "Package added", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }, 1000);
                }
            }
        });
        return builder.create();
    }

    private PackModel savePack()
    {
        PackModel pack=new PackModel();
        pack.setTitle(name.getText().toString());
        pack.setDescription(desc.getText().toString());
        return pack;
    }

    private void submitPackage() {
        Call<PackModel> call= RetrofitConnect.getPackageApi().savePackage(savePack());
        call.enqueue(new Callback<PackModel>() {
            @Override
            public void onResponse(Call<PackModel> call, Response<PackModel> response) {
            }
            @Override
            public void onFailure(Call<PackModel> call, Throwable t) {
            }
        });

    }


}
