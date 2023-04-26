package com.app.pujaconnectsc.View.UI.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.pujaconnectsc.Model.CusModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {
    EditText email;
    Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password );
        getSupportActionBar().setTitle("Reset Password");
        email = findViewById(R.id.resetPassEmail);
        confirm = findViewById(R.id.btnResetConfirmEmail);
        confirm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                if(email.getText().toString().equals(""))
                {
                    email.setError("Please enter an email");
                    email.requestFocus();
                }
                else
                sendRequest();;
            }
        });

    }
    private CusModel createRequest()
    {
        CusModel cusModel = new CusModel();
        cusModel.setEmail(email.getText().toString());
        return cusModel;
    }
    private void sendRequest()
    {
        Call<CusModel> call = RetrofitConnect.getUserApi().resetPasswordEmail(createRequest());
        call.enqueue(new Callback<CusModel>() {
            @Override
            public void onResponse(Call<CusModel> call, Response<CusModel> response) {
                if(response.code()==200)
                    sucessDialog();
                else if(response.code()==404)
                    errorDialog("User");
                else if(response.code()==500)
                    errorDialog("Server");
            }

            @Override
            public void onFailure(Call<CusModel> call, Throwable t) {
                errorDialog("Server");

            }
        });
     }
     private void sucessDialog()
     {
         new AestheticDialog.Builder(ResetPassword.this, DialogStyle.FLAT, DialogType.SUCCESS)
                 .setTitle("Email Sent")
                 .setMessage("Password Reset email has been sent please check Your email")
                 .setAnimation(DialogAnimation.FADE)
                 .setCancelable(false)
                 .setDarkMode(true)
                 .setOnClickListener(new OnDialogClickListener() {
                     @Override
                     public void onClick(@NonNull AestheticDialog.Builder builder) {
                         builder.dismiss();
                         Intent intent = new Intent(ResetPassword.this,LoginActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         startActivity(intent);
                         finish();
                     }
                 }).show();
     }
     private void errorDialog(String type)
     {
         String message = null;
         if(type == "Server")
             message = "Something went wrong try again";
         else if(type == "User")
             message = "User with email not found please check your email and try again";
         new AestheticDialog.Builder(ResetPassword.this, DialogStyle.FLAT, DialogType.ERROR)
                 .setTitle("Error")
                 .setMessage(message)
                 .setAnimation(DialogAnimation.FADE)
                 .setCancelable(false)
                 .setDarkMode(true)
                 .setOnClickListener(new OnDialogClickListener() {
                     @Override
                     public void onClick(@NonNull AestheticDialog.Builder builder) {
                         builder.dismiss();
                     }
                 }).show();
     }
}