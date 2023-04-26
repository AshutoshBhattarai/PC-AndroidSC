package com.app.pujaconnectsc.View.UI.Admin;

import android.app.Dialog;
import android.content.DialogInterface;
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

public class AdminPackUpdateDialog extends AppCompatDialogFragment {
    TextView delete;
    EditText name,desc,imgurl;
    Button btnSubmit,btnImg;
    String id;
    String disid,distitle,disdesc;

    public AdminPackUpdateDialog(String id) {
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.pack_add_popup,null);
        builder.setView(view);
        Bundle args= getArguments();
        disid = args.getString("id");
        disdesc = args.getString("desc");
        distitle = args.getString("title");
        name=view.findViewById(R.id.addPackName);
        desc=view.findViewById(R.id.addPackDesc);
        imgurl=view.findViewById(R.id.addPackImage);
        delete=view.findViewById(R.id.txtDeletePack2);
        btnSubmit=view.findViewById(R.id.btnPackSubmit);
        btnImg = view.findViewById(R.id.addPackImagebtn);
        name.setText(distitle);
        desc.setText(disdesc);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 0);
            }
        });
        btnSubmit.setText("Update");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPackDeleteDialog();
            }
        });
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
                    updatePackage();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent=new Intent(getActivity(), AdminPackages.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            Toast.makeText(getActivity(), "Package info updated", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }, 1000);

                }
            }
        });
        return builder.create();
    }

    private PackModel updatePack()
    {
        PackModel pack=new PackModel();
        pack.setTitle(name.getText().toString());
        pack.setDescription(desc.getText().toString());
        pack.setImgUrl(imgurl.getText().toString());
        return pack;
    }

    private void updatePackage() {
        Call<PackModel> call= RetrofitConnect.getPackageApi().updatePackage(id,updatePack());
        call.enqueue(new Callback<PackModel>() {
            @Override
            public void onResponse(Call<PackModel> call, Response<PackModel> response) {
            }
            @Override
            public void onFailure(Call<PackModel> call, Throwable t) {
               // Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
               // startActivity(new Intent(getContext(), AdminPackages.class));
            }
        });

    }
    private void showPackDeleteDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Package")
                .setMessage("Do you really want to delete this Package?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deletePack();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(getActivity(), AdminPackages.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Toast.makeText(getActivity(), "Package deleted", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        }, 1000);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deletePack() {
        Call<PackModel> call = RetrofitConnect.getPackageApi().deletePackage(id);
        call.enqueue(new Callback<PackModel>() {
            @Override
            public void onResponse(Call<PackModel> call, Response<PackModel> response) {

            }

            @Override
            public void onFailure(Call<PackModel> call, Throwable t) {
                //Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
