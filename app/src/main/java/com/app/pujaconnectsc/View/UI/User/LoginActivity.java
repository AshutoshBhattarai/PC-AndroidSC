package com.app.pujaconnectsc.View.UI.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.pujaconnectsc.Model.CusModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.Services.SessionManagement;
import com.app.pujaconnectsc.View.UI.Admin.DashBoard;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.messaging.FirebaseMessaging;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText phone, pass;
    private Button login;
    private TextView register,resetPass;
    private long authphone;
    private String authpass, authname, authaddress,authrole,authEmail;
    private int authid;
    private SessionManagement sm;
    private ProgressBar pb;
    private LinearProgressIndicator lpb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone = findViewById(R.id.loginPhone);
        pass = findViewById(R.id.loginPass);
        login = (Button) findViewById(R.id.btnLogin);
        register = findViewById(R.id.txtLoginReg);
        pb = findViewById(R.id.spinkit_Login);
        pb.setIndeterminateDrawable(new FadingCircle());
        resetPass = findViewById(R.id.txtResetPass);
        lpb = findViewById(R.id.pbLoginLinear);
        sm = new SessionManagement(getApplicationContext());
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), RegisterUser.class));
            }
        });
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ResetPassword.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                pb.setVisibility(View.VISIBLE);
                //lpb.setVisibility(View.VISIBLE);
                if (phone.getText().toString().equals("")) {
                    // lpb.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                    phone.setError("Please enter your number or email");
                    phone.requestFocus();
                } else if (pass.getText().toString().equals("")) {
                   // lpb.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                    pass.setError("Please enter your password");
                    pass.requestFocus();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            authLogin();
                        }
                    }, 1000);

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sm.isLogged()) {
            startActivity(new Intent(getApplicationContext(), HomePage.class));
            finish();
        }
        else if(sm.isAdminLogged())
        {
            startActivity(new Intent(getApplicationContext(), DashBoard.class));
            finish();
        }
    }

    private CusModel authDetail() {
        CusModel auth = new CusModel();
        if(phone.getText().toString().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"))
            auth.setEmail(phone.getText().toString());
        else
            auth.setPhone(Long.parseLong(phone.getText().toString()));
        auth.setPass(pass.getText().toString());
        return auth;
    }

    private void authLogin() {
        Call<List<CusModel>> call = RetrofitConnect.getUserApi().authUser(authDetail());
        call.enqueue(new Callback<List<CusModel>>() {
            @Override
            public void onResponse(Call<List<CusModel>> call, Response<List<CusModel>> response) {
                if (response.code() == 200) {
                    pb.setVisibility(View.GONE);
                    //lpb.setVisibility(View.GONE);
                    List<CusModel> data = response.body();
                    notificationInit("Customer");
                    for (CusModel i : data) {
                        authphone = i.getPhone();
                        authpass = i.getPass();
                        authid = i.getId();
                        authname = i.getName();
                        authaddress = i.getAddress();
                        authEmail = i.getEmail();
                    }
                    sm.saveUserSession(authid, authname, authpass, authphone, authaddress,authEmail);
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                    finish();
                }
                if (response.code() == 201 || response.code() == 202) {
                    //lpb.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                    List<CusModel> data = response.body();
                    notificationInit("Admin");
                    for (CusModel i : data) {
                        authphone = i.getPhone();
                        authpass = i.getPass();
                        authid = i.getId();
                        authname = i.getName();
                        authrole = i.getRole();
                        authEmail = i.getEmail();
                    }
                    sm.saveAdminSession(authid, authname, authpass, authphone,authrole,authEmail);
                    startActivity(new Intent(getApplicationContext(), DashBoard.class));
                    finish();
                }
                else if (response.code() == 400) {
                    //lpb.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                    errorDialog("Password incorrect try again");
                } else if (response.code() == 404) {
                    //lpb.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                    errorDialog("User doesn't exist try again or Register");
                }
                else if(response.code()==401)
                {
                    //lpb.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                    errorDialog("User with this Id is already logged in!! Try Again Later");
                }
            }

            @Override
            public void onFailure(Call<List<CusModel>> call, Throwable t) {
                //lpb.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);
                errorDialog(t.getMessage());
            }
        });
    }
    private void notificationInit(String user)
    {
        if(user == "Admin")
        {
            FirebaseMessaging.getInstance().subscribeToTopic("AdminNotify").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful())
                        Toast.makeText(LoginActivity.this, "Task Failed"+task.getResult(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(user == "Customer")
        {
            FirebaseMessaging.getInstance().subscribeToTopic("CustomerNotify").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful())
                        Toast.makeText(LoginActivity.this, "Task Failed"+task.getResult(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void errorDialog(String message)
    {
        new AestheticDialog.Builder(LoginActivity.this, DialogStyle.FLAT, DialogType.INFO)
                .setTitle("Credentials Warning")
                .setMessage(message)
                .setCancelable(false)
                .setAnimation(DialogAnimation.SPIN)
                .setDarkMode(true)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        builder.dismiss();
                    }
                }).show();
    }
}