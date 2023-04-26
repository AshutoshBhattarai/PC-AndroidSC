package com.app.pujaconnectsc.View.UI.Admin;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.PackDetailModel;
import com.app.pujaconnectsc.Model.PackModel;
import com.app.pujaconnectsc.Model.PackPost;
import com.app.pujaconnectsc.Model.ProductModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RVClickListener;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.View.UIAdapter.ProductAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPackDetail extends AppCompatActivity {
    private TextView txtTitle, txtDesc, noData, packdelete, diagName;
    private ImageView img;
    String mediaPath;
    private Button addItem, confirm, updatePack, packSubmit, delete;
    private ProductAdapter productAdapter;
    private ArrayList<PackDetailModel> packDetailModels;
    private ArrayList<ProductModel> productList;
    private EditText quant, packname, packdesc, packimg;
    private RVClickListener listener;
    private RecyclerView rv;
    TextView popDelete;
    EditText popName, popDesc;
    Boolean isSelected;
    Button btnSubmit, btnImg;
    private ProgressBar pb;
    private Spinner spinner;
    private int id, prod_id, updateProdId;
    private String desc, title, imgurl, amount, packid;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> nameArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pack_detail);
        isSelected = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            id = (int) getIntent().getSerializableExtra("AdminPackId");
            desc = (String) getIntent().getSerializableExtra("AdminPackDescription");
            title = (String) getIntent().getSerializableExtra("AdminPackTitle");
            imgurl = (String) getIntent().getSerializableExtra("AdminPackImage");
            packid = String.valueOf(id);
        }
        txtTitle = findViewById(R.id.txtPackTitle);
        pb = findViewById(R.id.adminPackPb);
        txtDesc = findViewById(R.id.txtPackDesc);
        img = findViewById(R.id.packImgPP);
        updatePack = findViewById(R.id.btnUpdatePack);
        noData = findViewById(R.id.txtNodata);
        getProductList();
        getPackDetail();
        buildRecyclerView();
        txtTitle.setText(title);
        txtDesc.setText(desc);
        Glide.with(getApplicationContext())
                .load(imgurl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.no_image_2)
                .into(img);


        updatePack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                showUpdateDialogwImg();

            }
        });

        addItem = findViewById(R.id.btnAddItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInsertDialog();
            }
        });

    }

    private void buildRecyclerView() {
        setOnClickListener();
        rv = findViewById(R.id.productRV);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        rv.setItemAnimator(new DefaultItemAnimator());
        // productAdapter = new ProductAdapter(this, packDetailModels, listener);
        //rv.setAdapter(productAdapter);
    }

    private void showUpdateDialog(int pos) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_customize_package);
        diagName = dialog.findViewById(R.id.txtDiagProdName);
        quant = dialog.findViewById(R.id.cusQuantity);
        confirm = dialog.findViewById(R.id.btnCustReq);
        delete = dialog.findViewById(R.id.btnCusDelete);
        quant.setText(String.valueOf(packDetailModels.get(pos).getQuantity()));
        diagName.setText(packDetailModels.get(pos).getName());
        updateProdId = packDetailModels.get(pos).getId();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = "0";
                showProductDeleteDialog(pos);
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quant.getText().toString().equals(""))
                    amount = "0";
                else
                    amount = quant.getText().toString();
                updateProductInPack();
                Handler handler = new Handler();
                packDetailModels.clear();
                dialog.dismiss();
                pb.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPackDetail();
                        Toast.makeText(AdminPackDetail.this, "Product Detail Updated", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }
        });

        dialog.show();
    }

    private void showProductDeleteDialog(int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Do you really want to delete this product?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteProductInPack();
                        pb.setVisibility(View.VISIBLE);
                        packDetailModels.clear();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getPackDetail();
                                Toast.makeText(AdminPackDetail.this, "Product deleted", Toast.LENGTH_SHORT).show();
                            }
                        }, 1000);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


    private void showInsertDialog() {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.package_additem_popup);
        confirm = dialog.findViewById(R.id.btnSpinAddItem);
        quant = dialog.findViewById(R.id.spinAIQuant);
        spinner = dialog.findViewById(R.id.dialogSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nameArray);
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prod_id = productList.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quant.getText().toString().equals(""))
                    amount = "0";
                else
                    amount = quant.getText().toString();
                Handler handler = new Handler();
                addProductInPack();
                packDetailModels.clear();
                dialog.dismiss();
                pb.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPackDetail();
                        Toast.makeText(AdminPackDetail.this, "Product Added", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);


            }
        });

        dialog.show();

    }

    private void setOnClickListener() {
        listener = new RVClickListener() {
            @Override
            public void onClick(View v, int pos) {
                showUpdateDialog(pos);
            }
        };

    }

    private PackPost createInsertPost() {
        PackPost packPost = new PackPost();
        packPost.setPackid(id);
        packPost.setProdid(prod_id);
        packPost.setQuantity(Integer.parseInt(amount));
        return packPost;
    }

    private PackPost createUpdatePost() {
        PackPost packPost = new PackPost();
        packPost.setPackid(id);
        packPost.setProdid(updateProdId);
        packPost.setQuantity(Integer.parseInt(amount));
        return packPost;
    }

    private void addProductInPack() {
        Call<PackPost> call = RetrofitConnect.getPackageApi().insertPackProduct(createInsertPost());
        call.enqueue(new Callback<PackPost>() {
            @Override
            public void onResponse(Call<PackPost> call, Response<PackPost> response) {
            }

            @Override
            public void onFailure(Call<PackPost> call, Throwable t) {
                //Toast.makeText(AdminPackDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductInPack() {
        Call<PackPost> call = RetrofitConnect.getPackageApi().updatePackProduct(createUpdatePost());
        call.enqueue(new Callback<PackPost>() {
            @Override
            public void onResponse(Call<PackPost> call, Response<PackPost> response) {
            }

            @Override
            public void onFailure(Call<PackPost> call, Throwable t) {
                //Toast.makeText(AdminPackDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteProductInPack() {
        Call<PackPost> call = RetrofitConnect.getPackageApi().deletePackProduct(createUpdatePost());
        call.enqueue(new Callback<PackPost>() {
            @Override
            public void onResponse(Call<PackPost> call, Response<PackPost> response) {
            }

            @Override
            public void onFailure(Call<PackPost> call, Throwable t) {
                //Toast.makeText(AdminPackDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProductList() {
        Call<List<ProductModel>> call = RetrofitConnect.getPackageApi().getProdcuts();
        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.code() == 200) {
                    productList = new ArrayList<>(response.body());
                    Collections.sort(productList, new Comparator<ProductModel>() {
                        @Override
                        public int compare(ProductModel productModel, ProductModel t1) {
                            return productModel.getName().compareToIgnoreCase(t1.getName());
                        }
                    });
                    nameArray = new ArrayList<>();
                    for (ProductModel m : productList) {
                        nameArray.add(m.getName().trim());
                    }
                } else if (response.code() == 400) {
                    productList = new ArrayList<>();
                    addItem.setVisibility(View.GONE);
                    Toast.makeText(AdminPackDetail.this, "No Products to add", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(AdminPackDetail.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void getPackDetail() {
        Call<List<PackDetailModel>> call = RetrofitConnect.getPackageApi().getPackDetail(packid);
        pb.setVisibility(View.GONE);
        call.enqueue(new Callback<List<PackDetailModel>>() {
            @Override
            public void onResponse(Call<List<PackDetailModel>> call, Response<List<PackDetailModel>> response) {
                if (response.code() == 200) {
                    packDetailModels = new ArrayList<>(response.body());
                    Collections.sort(packDetailModels, new Comparator<PackDetailModel>() {
                        @Override
                        public int compare(PackDetailModel packDetailModel, PackDetailModel t1) {
                            return packDetailModel.getName().compareToIgnoreCase(t1.getName());
                        }
                    });
                    productAdapter = new ProductAdapter(getApplicationContext(), packDetailModels, listener);
                    rv.setAdapter(productAdapter);
                } else if (response.code() == 400) {
                    packDetailModels = new ArrayList<>();
                    productAdapter = new ProductAdapter(getApplicationContext(), packDetailModels, listener);
                    rv.setAdapter(productAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<PackDetailModel>> call, Throwable t) {
                Toast.makeText(AdminPackDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPackUpdateDialog() {
        AdminPackUpdateDialog dialog = new AdminPackUpdateDialog(packid);
        Bundle args = new Bundle();
        args.putString("id", String.valueOf(id));
        args.putString("title", title);
        args.putString("desc", desc);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Update");
    }

    private void showUpdateDialogwImg() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.pack_add_popup);
        popName = dialog.findViewById(R.id.addPackName);
        popDesc = dialog.findViewById(R.id.addPackDesc);
        popDelete = dialog.findViewById(R.id.txtDeletePack2);
        btnSubmit = dialog.findViewById(R.id.btnPackSubmit);
        btnImg = dialog.findViewById(R.id.addPackImagebtn);
        popName.setText(title);
        popDesc.setText(desc);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 0);
            }
        });
        popDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPackDeleteDialog();
                dialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popName.getText().toString().equals("") || popDesc.getText().toString().equals(""))
                    Toast.makeText(AdminPackDetail.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                else {
                    if (!isSelected)
                        updatePackage();
                    else
                        updateProdWImg();
                    dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), AdminPackages.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Package info updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();

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
        if (ContextCompat.checkSelfPermission(AdminPackDetail.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AdminPackDetail.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(AdminPackDetail.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(AdminPackDetail.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void showPackDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Package")
                .setMessage("Do you really want to delete this Package?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deletePack();
                        Intent intent = new Intent(getApplicationContext(), AdminPackages.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Package deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deletePack() {
        Call<PackModel> call = RetrofitConnect.getPackageApi().deletePackage(String.valueOf(id));
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

    private PackModel updatePack() {
        PackModel pack = new PackModel();
        pack.setTitle(popName.getText().toString());
        pack.setDescription(popDesc.getText().toString());
        return pack;
    }

    private void updatePackage() {
        Call<PackModel> call = RetrofitConnect.getPackageApi().updatePackage(String.valueOf(id), updatePack());
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

    private void updateProdWImg() {
        String reqstock;
        File file = new File(mediaPath);
        String reqname = popName.getText().toString();
        String reqdesc = popDesc.getText().toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), reqname);
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), reqdesc);
        RequestBody reqid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));
        Call<ResponseBody> call = RetrofitConnect.getPackageApi().updatePackwImg(fileToUpload, name, desc, filename, reqid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200)
                    Toast.makeText(AdminPackDetail.this, "Package Updated", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminPackDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}