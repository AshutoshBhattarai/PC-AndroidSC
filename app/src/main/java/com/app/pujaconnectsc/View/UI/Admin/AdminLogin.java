package com.app.pujaconnectsc.View.UI.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.SessionManagement;

// Functionality not currently used
public class AdminLogin extends AppCompatActivity {
    EditText id, pass;
    Button login;
    TextView logerr;
    long authphone;
    String authpass,authname;
    int authid;
    SessionManagement sm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        id = findViewById(R.id.adminLoginId);
        pass = findViewById(R.id.adminLoginPass);
        login = (Button)findViewById(R.id.btnAdminLogin);
        logerr = findViewById(R.id.adminLogErr);
        sm=new SessionManagement(getApplicationContext());
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id.getText().toString().equals(""))
                {
                    id.setError("No Empty");
                    id.requestFocus();
                }
                else if(pass.getText().toString().equals(""))
                {
                    pass.setError("No Empty");
                    pass.requestFocus();
                }
                else{
                   // validateLogin();
                }
            }
        });
    }
//    private void validateLogin() {
//        Call<List<AdminModel>> call = RetrofitConnect.
//                getUserApi().findAdmin(sm.getSessionUserPhone().toString());
//        call.enqueue(new Callback<List<AdminModel>>() {
//            @Override
//            public void onResponse(Call<List<AdminModel>> call, Response<List<AdminModel>> response) {
//                if (response.code()==200) {
//                    List<AdminModel> data = response.body();
//                    for (AdminModel i : data) {
//                        authpass = i.getPass();
//                        authid=i.getId();
//                        authname=i.getUser();
//                        authphone=i.getPhone();
//                        if (pass.getText().toString().equals(authpass)){
//                            sm.saveAdminSession(authid,authname,authpass);
//                            sm.removeUserSession();
//                            startActivity(new Intent(getApplicationContext(), AdminHomePage.class));
//                            finish();
//                        }
//                        else
//                        {
//
//                            logerr.setText("Wrong Pass "+authpass+authname+"  "+response.message());
//                            //logerr.setText("Credentials Incorrect Try Again");
//                        }
//                    }
//                }
//                else if (response.code()==404)
//                {
//                    logerr.setText("NO USer"+response.message());
//                    logerr.setText("Credentials Incorrect Try Again");
//                }
//
//            }
//            @Override
//            public void onFailure(Call<List<AdminModel>> call, Throwable t) {
//               logerr.setText(t.getMessage());
//            }
//        });
//    }
}