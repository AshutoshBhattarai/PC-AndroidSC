package com.app.pujaconnectsc.View.UI.Admin;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.PackModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.Services.SessionManagement;
import com.app.pujaconnectsc.View.UIAdapter.AdminPackAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPackages extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar pb;
    private EditText name, desc, imgurl;
    private TextView delete;
    private Button btnSubmit, btnImg;
    String mediaPath;
    Boolean isSelected;
    private AdminPackAdapter packageAdapter;
    private ArrayList<PackModel> packList;
    private SessionManagement sm;
    private FloatingActionButton fbAdd, fbPackAdd, fbAdminAdd;
    private TextView txtaddpack, txtaddadmin;
    private Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);
        getSupportActionBar().setTitle("Packages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fbAdd = findViewById(R.id.fbAdd);
        fbPackAdd = findViewById(R.id.fbAddPack);
        fbAdminAdd = findViewById(R.id.fbAddAdmin);
        txtaddadmin = findViewById(R.id.txtaddadmin);
        txtaddpack = findViewById(R.id.txtaddpack);
        isAllFabsVisible = false;
        //addItems();
        isSelected = false;
        fbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                showInsertDialog();
            }
        });
        sm = new SessionManagement(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.packageRV);
        pb = (ProgressBar) findViewById(R.id.packLoading);

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getPackList();
    }

    private void getPackList() {
        pb.setVisibility(View.VISIBLE);
        Call<List<PackModel>> call = RetrofitConnect.getPackageApi().getPackages();
        call.enqueue(new Callback<List<PackModel>>() {
            @Override
            public void onResponse(Call<List<PackModel>> call, Response<List<PackModel>> response) {
                pb.setVisibility(View.GONE);
                if (response.code() == 200) {
                    packList = new ArrayList<>(response.body());
                    packageAdapter = new AdminPackAdapter(getApplicationContext(), packList);
                    recyclerView.setAdapter(packageAdapter);
                } else if (response.code() == 400) {
                    packList = new ArrayList<>();
                    packageAdapter = new AdminPackAdapter(getApplicationContext(), packList);
                    recyclerView.setAdapter(packageAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<PackModel>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showInsertDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.pack_add_popup);
        name = dialog.findViewById(R.id.addPackName);
        desc = dialog.findViewById(R.id.addPackDesc);
        imgurl = dialog.findViewById(R.id.addPackImage);
        btnImg = dialog.findViewById(R.id.addPackImagebtn);
        btnSubmit = dialog.findViewById(R.id.btnPackSubmit);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 0);
            }
        });
        delete = dialog.findViewById(R.id.txtDeletePack2);
        delete.setVisibility(View.GONE);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    name.setError("Please enter data");
                    name.requestFocus();
                } else if (desc.getText().toString().equals("")) {
                    desc.setError("Please enter data");
                    desc.requestFocus();
                }
                else {
                    if (isSelected)
                        insertPackWImg();
                    else
                        submitPackage();
                    packList.clear();
                    packageAdapter = new AdminPackAdapter(getApplicationContext(), packList);
                    recyclerView.setAdapter(packageAdapter);
                    pb.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    isSelected = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            getPackList();
                            Toast.makeText(AdminPackages.this, "Package Added", Toast.LENGTH_SHORT).show();
                        }
                    }, 800);
                }
            }
        });
        dialog.show();
    }


    private void insertPackWImg() {
        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(mediaPath);
        String reqname = name.getText().toString();
        String reqdesc = desc.getText().toString();
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), reqname);
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), reqdesc);
        Call<ResponseBody> call = RetrofitConnect.getPackageApi().insertPackwImg(fileToUpload, name, desc, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminPackages.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediaPath = cursor.getString(columnIndex);
                btnImg.setText("Image selected");
                isSelected = true;
                cursor.close();

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(AdminPackages.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AdminPackages.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(AdminPackages.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(AdminPackages.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private PackModel savePack() {
        PackModel pack = new PackModel();
        pack.setTitle(name.getText().toString());
        pack.setDescription(desc.getText().toString());
        return pack;
    }

    private void submitPackage() {
        Call<PackModel> call = RetrofitConnect.getPackageApi().savePackage(savePack());
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
