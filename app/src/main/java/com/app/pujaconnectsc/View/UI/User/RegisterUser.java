package com.app.pujaconnectsc.View.UI.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.pujaconnectsc.Model.CusModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterUser extends AppCompatActivity {
    EditText name, pass, phone, email;
    Button btnRegister;
    AutoCompleteTextView address;
    TextView login;
    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        String[] locations = getResources().getStringArray(R.array.addresses);
        name = findViewById(R.id.regName);
        pass = findViewById(R.id.regPass);
        phone = findViewById(R.id.regPhone);
        address = findViewById(R.id.regAddress);
        email = findViewById(R.id.regEmail);
        login = findViewById(R.id.txtRegisterLogin);
        pb = (ProgressBar) findViewById(R.id.spin_kitPbRegister);
        pb.setIndeterminateDrawable(new FadingCircle());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locations);
        address.setAdapter(adapter);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        validate();
                    }
                },1000);
            }
        });
    }

    private CusModel createUser() {
        CusModel reg = new CusModel();
        reg.setName(name.getText().toString().trim());
        reg.setPass(pass.getText().toString().trim());
        reg.setPhone(Long.parseLong(phone.getText().toString().trim()));
        reg.setAddress(address.getText().toString().trim());
        reg.setEmail(email.getText().toString().trim());
        return reg;
    }

    private void processData() {

        Call<CusModel> call = RetrofitConnect.getUserApi().saveUser(createUser());
        call.enqueue(new Callback<CusModel>() {
            @Override
            public void onResponse(Call<CusModel> call, Response<CusModel> response) {
                pb.setVisibility(View.GONE);
                if (response.code() == 200) {
                    sucessDialog();
                    //Toast.makeText(getApplicationContext(), "User Registered sucessfully", Toast.LENGTH_SHORT).show();

                } else if (response.code() == 400) {
                    btnRegister.setEnabled(true);
                   errorDialog("Phone number Already Exists");
                }
            }
            @Override
            public void onFailure(Call<CusModel> call, Throwable t) {
                pb.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
               errorDialog(t.getMessage());
            }
        });
    }

    private void validate() {
        String checkName = name.getText().toString().trim();
        String checkEmail = email.getText().toString().trim();
        String checkPass = pass.getText().toString().trim();
        String checkAddress = address.getText().toString().trim();
        String checkPhone = phone.getText().toString().trim();
        String validEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";
        String validPass= "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                //"(?=S+$)" +           //no white spaces
                ".{5,}" +               //at least 4 characters
                "$";
        String checkspaces = "Aw{1,20}z";
        if (checkName.equals("")) {
            pb.setVisibility(View.GONE);
            warningDialog("Please enter your name");
            //name.setError("Please enter your Name");
            name.requestFocus();
        } else if (checkName.matches(".*[0-9].*")) {
            pb.setVisibility(View.GONE);
            warningDialog("Please enter a valid name");
            //name.setError("Please enter a valid Name");
            name.requestFocus();
        } else if (checkPass.equals("")) {
            pb.setVisibility(View.GONE);
            warningDialog("Please enter your password");
           // pass.setError("Please enter your Password");
            pass.requestFocus();
        } else if (!checkPass.matches(validPass)) {
            pb.setVisibility(View.GONE);
            warningDialog("Password should contain 5 letters and special characters");
            //pass.setError("Password should be greater than 5 character");
            pass.requestFocus();
        } else if (checkPhone.equals("")) {
            pb.setVisibility(View.GONE);
            warningDialog("Please enter your phone number");
            //phone.setError("Please enter your Phone Number");
            phone.requestFocus();
        } else if (checkPhone.length() != 10) {
            pb.setVisibility(View.GONE);
            warningDialog("Invalid phone number");
            //phone.setError("Invalid phone Number");
            phone.requestFocus();
        }
        else if(checkEmail.equals(""))
        {
            pb.setVisibility(View.GONE);
            //email.setError("Please enter an email address");
            warningDialog("Please enter an email address");
            email.requestFocus();
        }
        else if(!checkEmail.matches(validEmail))
        {
            pb.setVisibility(View.GONE);
            warningDialog("Please enter a valid email");
            email.requestFocus();
        }
        else if (checkAddress.equals("")) {
            pb.setVisibility(View.GONE);
            warningDialog("Please enter your address");
            //address.setError("Please enter your Address");
            address.requestFocus();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnRegister.setEnabled(false);
                    processData();
                }
            },1500);

        }
    }
    private void warningDialog(String message)
    {
        new AestheticDialog.Builder(RegisterUser.this, DialogStyle.TOASTER, DialogType.WARNING)
                .setTitle("Warning")
                .setMessage(message)
                .setAnimation(DialogAnimation.FADE)
                .setGravity(Gravity.BOTTOM)
                .show();
    }
    private void errorDialog(String message)
    {
        new AestheticDialog.Builder(RegisterUser.this, DialogStyle.FLAT, DialogType.WARNING)
                .setTitle("Oops !!")
                .setMessage(message)
                .setAnimation(DialogAnimation.FADE)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        builder.dismiss();
                    }
                })
                .show();
    }
    private void sucessDialog()
    {
        new AestheticDialog.Builder(RegisterUser.this, DialogStyle.FLAT, DialogType.SUCCESS)
                .setTitle("User Registered")
                .setMessage("User has been registered successfully")
                .setAnimation(DialogAnimation.WINDMILL)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        builder.dismiss();
                        Intent intent =new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}