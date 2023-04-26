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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.pujaconnectsc.Model.ProductModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RVClickListener;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.View.UIAdapter.ProductDisplayAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class AdminDisplayProducts extends AppCompatActivity {
    RecyclerView rv;
    EditText name, stock, price, image;
    Button update, additem, addimg;
    ProgressBar pb;
    Boolean isSelected;
    int getId;
    TextView delete;
    String mediaPath;
    ArrayList<ProductModel> productModelArrayList;
    ProductDisplayAdapter adapter;
    FloatingActionButton addProd;
    RVClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_display_products);
        getSupportActionBar().setTitle("Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        addProd = findViewById(R.id.fabAddProduct);
        addProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                showinsertDialogImg();
            }
        });
        setClickListener();
        isSelected = false;
        pb = findViewById(R.id.pbDisProd);
        pb.setVisibility(View.VISIBLE);
        rv = findViewById(R.id.rvDisplayProducts);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        rv.setItemAnimator(new DefaultItemAnimator());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getProdList();
            }
        }, 700);

    }

    private void getProdList() {
        Call<List<ProductModel>> call = RetrofitConnect.getPackageApi().getProdcuts();
        call.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.code() == 200) {
                    pb.setVisibility(View.GONE);
                    productModelArrayList = new ArrayList<>(response.body());
                    Collections.sort(productModelArrayList, new Comparator<ProductModel>() {
                        @Override
                        public int compare(ProductModel productModel, ProductModel t1) {
                            return productModel.getName().compareToIgnoreCase(t1.getName());
                        }
                    });
                    adapter = new ProductDisplayAdapter(getApplicationContext(), productModelArrayList, listener);
                    rv.setAdapter(adapter);
                } else if (response.code() == 400) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(AdminDisplayProducts.this, "There are no products", Toast.LENGTH_SHORT).show();
                    productModelArrayList = new ArrayList<>();
                    adapter = new ProductDisplayAdapter(getApplicationContext(), productModelArrayList);
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                pb.setVisibility(View.GONE);
                Toast.makeText(AdminDisplayProducts.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setClickListener() {
        listener = new RVClickListener() {
            @Override
            public void onClick(View v, int pos) {
                showUpdateDiagImg(pos);
            }
        };
    }

    private void showinsertDialog() {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_add_display_product);

        name = dialog.findViewById(R.id.etAddProdName);
        stock = dialog.findViewById(R.id.etAddProdStock);
        price = dialog.findViewById(R.id.etAddProdPrice);
        image = dialog.findViewById(R.id.etAddProdImg);
        image.setVisibility(View.VISIBLE);
        additem = dialog.findViewById(R.id.btnAddDisProduct);
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")
                        || stock.getText().toString().equals("")
                        || price.getText().toString().equals("")
                        || image.getText().toString().equals("")
                ) {
                    Toast.makeText(AdminDisplayProducts.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    addProduct();
                    productModelArrayList.clear();
                    adapter = new ProductDisplayAdapter(getApplicationContext(), productModelArrayList);
                    rv.setAdapter(adapter);
                    pb.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            getProdList();
                            Toast.makeText(AdminDisplayProducts.this, "Product Added", Toast.LENGTH_SHORT).show();
                        }
                    }, 800);
                }
            }
        });

        dialog.show();
    }

    private ProductModel createProducts() {
        ProductModel productModel = new ProductModel();
        productModel.setName(name.getText().toString());
        productModel.setStock(Integer.parseInt(stock.getText().toString()));
        productModel.setPrice(Double.parseDouble(price.getText().toString()));
        productModel.setImage(image.getText().toString());
        return productModel;
    }

    private void addProduct() {
        Call<ProductModel> call = RetrofitConnect.getPackageApi().insertProducts(createProducts());
        call.enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                if (response.code() == 200) {
                    Toast.makeText(AdminDisplayProducts.this, "Product Added", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(AdminDisplayProducts.this, response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {
                Toast.makeText(AdminDisplayProducts.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDialog(int pos) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_add_display_product);

        name = dialog.findViewById(R.id.etAddProdName);
        stock = dialog.findViewById(R.id.etAddProdStock);
        price = dialog.findViewById(R.id.etAddProdPrice);
        image = dialog.findViewById(R.id.etAddProdImg);
        image.setVisibility(View.VISIBLE);
        additem = dialog.findViewById(R.id.btnAddDisProduct);
        name.setText(productModelArrayList.get(pos).getName());
        price.setText(String.valueOf(productModelArrayList.get(pos).getPrice()));
        String img = productModelArrayList.get(pos).getImage().substring(33);
        getId = productModelArrayList.get(pos).getId();
        image.setText(img);
        additem.setText("Update");
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")
                        || price.getText().toString().equals("")
                        || image.getText().toString().equals("")
                ) {
                    Toast.makeText(AdminDisplayProducts.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    updateProduct();
                    productModelArrayList.clear();
                    adapter = new ProductDisplayAdapter(getApplicationContext(), productModelArrayList);
                    rv.setAdapter(adapter);
                    pb.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            getProdList();
                        }
                    }, 800);
                }
            }
        });

        dialog.show();
    }

    private ProductModel updatePost() {
        ProductModel productModel = new ProductModel();
        productModel.setId(getId);
        productModel.setName(name.getText().toString());
        if (stock.getText().toString().equals(""))
            productModel.setStock(0);
        else
            productModel.setStock(Integer.parseInt(stock.getText().toString()));
        productModel.setPrice(Double.parseDouble(price.getText().toString()));
        return productModel;
    }

    private void updateProduct() {
        Call<ProductModel> call = RetrofitConnect.getPackageApi().updateProduct(updatePost());
        call.enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                if (response.code() == 200) {
                    Toast.makeText(AdminDisplayProducts.this, "Product updated", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(AdminDisplayProducts.this, response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {
                Toast.makeText(AdminDisplayProducts.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showUpdateDiagImg(int pos) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_add_display_product);

        name = dialog.findViewById(R.id.etAddProdName);
        stock = dialog.findViewById(R.id.etAddProdStock);
        price = dialog.findViewById(R.id.etAddProdPrice);
        addimg = dialog.findViewById(R.id.etAddProdImgbtn);
        delete = dialog.findViewById(R.id.btntxtDeleteProd);
        delete.setVisibility(View.VISIBLE);
        addimg.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductDeleteDialog(pos);
                dialog.dismiss();
            }
        });
        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 0);
            }
        });
        additem = dialog.findViewById(R.id.btnAddDisProduct);
        name.setText(productModelArrayList.get(pos).getName());
        price.setText(String.valueOf(productModelArrayList.get(pos).getPrice()));
        String img = productModelArrayList.get(pos).getImage().substring(33);
        getId = productModelArrayList.get(pos).getId();
        //image.setText(img);
        additem.setText("Update");
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")
                        || price.getText().toString().equals("")
                ) {
                    Toast.makeText(AdminDisplayProducts.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (isSelected)
                        updateProdWImg();
                    else
                        updateProduct();
                    productModelArrayList.clear();
                    adapter = new ProductDisplayAdapter(getApplicationContext(), productModelArrayList);
                    rv.setAdapter(adapter);
                    pb.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    isSelected = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            getProdList();
                        }
                    }, 800);
                }
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
                        productModelArrayList.clear();
                        adapter = new ProductDisplayAdapter(getApplicationContext(), productModelArrayList);
                        rv.setAdapter(adapter);
                        pb.setVisibility(View.VISIBLE);;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getProdList();
                                Toast.makeText(AdminDisplayProducts.this, "Product deleted", Toast.LENGTH_SHORT).show();
                            }
                        }, 1000);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteProductInPack() {
        Call<ProductModel> call = RetrofitConnect.getPackageApi().deleteProd(String.valueOf(getId));
        call.enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {

            }

            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {

            }
        });

    }

    private void showinsertDialogImg() {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_add_display_product);

        name = dialog.findViewById(R.id.etAddProdName);
        stock = dialog.findViewById(R.id.etAddProdStock);
        price = dialog.findViewById(R.id.etAddProdPrice);
        addimg = dialog.findViewById(R.id.etAddProdImgbtn);
        addimg.setVisibility(View.VISIBLE);
        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 0);
            }
        });
        additem = dialog.findViewById(R.id.btnAddDisProduct);
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")
                        || stock.getText().toString().equals("")
                        || price.getText().toString().equals("")
                ) {
                    Toast.makeText(AdminDisplayProducts.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else if (!isSelected)
                    Toast.makeText(AdminDisplayProducts.this, "Please Select an image", Toast.LENGTH_SHORT).show();
                else {
                    //addProduct();
                    insertProdWImg();
                    productModelArrayList.clear();
                    adapter = new ProductDisplayAdapter(getApplicationContext(), productModelArrayList);
                    rv.setAdapter(adapter);
                    pb.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    isSelected= false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            getProdList();
                            Toast.makeText(AdminDisplayProducts.this, "Product Added", Toast.LENGTH_SHORT).show();
                        }
                    }, 800);
                }
            }
        });

        dialog.show();
    }

    private void insertProdWImg() {
        // Map is used to multipart the file using okhttp3.RequestBody
        File file = new File(mediaPath);
        String reqname = name.getText().toString();
        String reqstock = stock.getText().toString();
        String reqprice = price.getText().toString();
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), reqname);
        RequestBody stock = RequestBody.create(MediaType.parse("text/plain"), reqstock);
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), reqprice);
        Call<ResponseBody> call = RetrofitConnect.getPackageApi().insertProdwImg(fileToUpload, name, stock, price, filename);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminDisplayProducts.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProdWImg() {
        String reqstock;
        File file = new File(mediaPath);
        String reqname = name.getText().toString();
        if (stock.getText().toString().equals(""))
            reqstock = "0";
        else
            reqstock = stock.getText().toString();
        String reqprice = price.getText().toString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), reqname);
        RequestBody stock = RequestBody.create(MediaType.parse("text/plain"), reqstock);
        RequestBody price = RequestBody.create(MediaType.parse("text/plain"), reqprice);
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(getId));
        Call<ResponseBody> call = RetrofitConnect.getPackageApi().updateProdwImg(fileToUpload, name, stock, price, filename, id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200)
                    Toast.makeText(AdminDisplayProducts.this, "Product Updated", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminDisplayProducts.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                addimg.setText("Image selected");
                isSelected = true;
                cursor.close();

            } else {
                Toast.makeText(this, "You haven't picked any Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Oops ! Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(AdminDisplayProducts.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AdminDisplayProducts.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(AdminDisplayProducts.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(AdminDisplayProducts.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}