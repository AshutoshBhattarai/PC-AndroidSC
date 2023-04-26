package com.app.pujaconnectsc.View.UI.User;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.pujaconnectsc.Model.CusModel;
import com.app.pujaconnectsc.R;
import com.app.pujaconnectsc.Services.RetrofitConnect;
import com.app.pujaconnectsc.Services.SessionManagement;
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

public class ProfileUser extends AppCompatActivity {
    private TextView name, phone, email, address, dialogName;
    private ImageView eName, ePhone, eEmail, eAddress;
    private Button ePass, dialogConfirm;
    private SessionManagement sm;
    private EditText input, inputPassword;
    LinearProgressIndicator pbSingle, pbPass;
    ProgressBar pb;
    ConstraintLayout layout;
    String activity;
    private String userName, userPhone, userEmail, userAddress;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initViews();
        sm = new SessionManagement(getApplicationContext());
        id = sm.getSessionUserId();
        layout = findViewById(R.id.layoutProfileUser);
        pb = findViewById(R.id.pbProfileUser);
        getUserDetails();
        pb.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 1000);
    }

    private void initViews() {
        name = (TextView) findViewById(R.id.profName);
        phone = (TextView) findViewById(R.id.profPhone);
        email = (TextView) findViewById(R.id.profEmail);
        address = (TextView) findViewById(R.id.profLocation);
        ePass = (Button) findViewById(R.id.btnEditProfPass);
        eName = (ImageView) findViewById(R.id.editProfName);
        ePhone = (ImageView) findViewById(R.id.editProfPhone);
        eEmail = (ImageView) findViewById(R.id.editProfEmail);
        eAddress = (ImageView) findViewById(R.id.editProfLocation);
        eName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog("name");
            }
        });
        ePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog("phone");
            }
        });
        eEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog("email");
            }
        });
        eAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog("address");
            }
        });
        ePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordUpdateDialog();
            }
        });
    }

    private void showUpdateDialog(String type) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_update_profile_single);
        input = dialog.findViewById(R.id.editProfileInput);
        dialogConfirm = dialog.findViewById(R.id.btnEditProfileText);
        dialogName = dialog.findViewById(R.id.txtProfEditName);
        pbSingle = dialog.findViewById(R.id.profDialogSinglePB);
        inputPassword = dialog.findViewById(R.id.editProfileInputPassword);
        if (type == "name") {
            dialogName.setText("Name");
            editName(dialog);
        } else if (type == "email") {
            dialogName.setText("Email");
            editEmail(dialog);
        } else if (type == "phone") {
            dialogName.setText("Phone");
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            editPhone(dialog);
        } else if (type == "address") {
            input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
            dialogName.setText("Address");
            editAddress(dialog);
        }
        dialog.show();

    }

    private void editName(Dialog dialog) {
        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeKeyboard(view);
                if (input.getText().toString().equals(""))
                    infoDialog("Please enter a name");
                else if (inputPassword.getText().toString().equals(""))
                    infoDialog("Please enter your password");
                else if (input.getText().toString().matches(".*[0-9].*"))
                    infoDialog("Please enter a valid Name");
                else {
                    CusModel model = new CusModel();
                    model.setName(input.getText().toString());
                    model.setId(id);
                    model.setPass(inputPassword.getText().toString());
                    Call<CusModel> call = RetrofitConnect.getUserApi().updateUserName(model);
                    pbSingle.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            call.enqueue(new Callback<CusModel>() {
                                @Override
                                public void onResponse(Call<CusModel> call, Response<CusModel> response) {
                                    pb.setVisibility(View.GONE);
                                    if (response.code() == 200) {
                                        pbSingle.setVisibility(View.GONE);
                                        sucessDialog("Name");
                                        dialog.dismiss();
                                    } else if (response.code() == 404) {
                                        pbSingle.setVisibility(View.GONE);
                                        infoDialog("Password incorrect try again");
                                    }
                                }

                                @Override
                                public void onFailure(Call<CusModel> call, Throwable t) {
                                    errorDialog(t.getMessage());
                                }
                            });
                        }
                    }, 1000);
                }
            }
        });
    }

    private void editAddress(Dialog dialog) {
        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeKeyboard(view);
                if (input.getText().toString().equals(""))
                    infoDialog("Please enter an address");
                else if (inputPassword.getText().toString().equals(""))
                    infoDialog("Please enter your password");
                else {
                    CusModel model = new CusModel();
                    model.setAddress(input.getText().toString());
                    model.setId(id);
                    model.setPass(inputPassword.getText().toString());
                    Call<CusModel> call = RetrofitConnect.getUserApi().updateUserAddress(model);
                    pbSingle.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            call.enqueue(new Callback<CusModel>() {
                                @Override
                                public void onResponse(Call<CusModel> call, Response<CusModel> response) {
                                    pb.setVisibility(View.GONE);
                                    if (response.code() == 200) {
                                        pbSingle.setVisibility(View.GONE);
                                        sucessDialog("Address");
                                        dialog.dismiss();
                                    } else if (response.code() == 404) {
                                        pbSingle.setVisibility(View.GONE);
                                        infoDialog("Password incorrect try again");
                                    }
                                }

                                @Override
                                public void onFailure(Call<CusModel> call, Throwable t) {
                                    errorDialog(t.getMessage());
                                }
                            });
                        }
                    }, 1000);
                }
            }
        });
    }

    private void editPhone(Dialog dialog) {
        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeKeyboard(view);
                if (input.getText().toString().equals(""))
                    infoDialog("Please enter a phone");
                else if (inputPassword.getText().toString().equals(""))
                    infoDialog("Please enter your password");
                else if (input.getText().toString().length() != 10)
                    infoDialog("Please enter a valid phone number!!");
                else {
                    CusModel model = new CusModel();
                    model.setPhone(Long.parseLong(input.getText().toString()));
                    model.setId(id);
                    model.setPass(inputPassword.getText().toString());
                    Call<CusModel> call = RetrofitConnect.getUserApi().updateUserPhone(model);
                    pbSingle.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            call.enqueue(new Callback<CusModel>() {
                                @Override
                                public void onResponse(Call<CusModel> call, Response<CusModel> response) {
                                    pb.setVisibility(View.GONE);
                                    if (response.code() == 200) {
                                        pbSingle.setVisibility(View.GONE);
                                        sucessDialog("Phone");
                                        dialog.dismiss();
                                    } else if (response.code() == 404) {
                                        pbSingle.setVisibility(View.GONE);
                                        infoDialog("Password incorrect try again");
                                    }
                                }

                                @Override
                                public void onFailure(Call<CusModel> call, Throwable t) {
                                    errorDialog(t.getMessage());
                                }
                            });
                        }
                    }, 1000);
                }
            }
        });
    }

    private void editEmail(Dialog dialog) {
        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeKeyboard(view);
                if (input.getText().toString().equals(""))
                    infoDialog("Please enter an email address");
                else if (inputPassword.getText().toString().equals(""))
                    infoDialog("Please enter your password");
                else if (!input.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+"))
                    infoDialog("Please enter a valid email");
                else {
                    CusModel model = new CusModel();
                    model.setName(input.getText().toString());
                    model.setId(id);
                    model.setPass(inputPassword.getText().toString());
                    Call<CusModel> call = RetrofitConnect.getUserApi().updateUserEmail(model);
                    pbSingle.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            call.enqueue(new Callback<CusModel>() {
                                @Override
                                public void onResponse(Call<CusModel> call, Response<CusModel> response) {
                                    pb.setVisibility(View.GONE);
                                    if (response.code() == 200) {
                                        pbSingle.setVisibility(View.GONE);
                                        sucessDialog("Email");
                                        dialog.dismiss();
                                    } else if (response.code() == 404) {
                                        pbSingle.setVisibility(View.GONE);
                                        infoDialog("Password incorrect try again");
                                    }
                                }

                                @Override
                                public void onFailure(Call<CusModel> call, Throwable t) {
                                    errorDialog(t.getMessage());
                                }
                            });
                        }
                    }, 1000);
                }
            }
        });
    }

    private void showPasswordUpdateDialog() {
        TextView reset;
        EditText oldPass, newPass1, newPass2;
        Button passconfirm;
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_update_password);
        oldPass = dialog.findViewById(R.id.editPassOldPass);
        newPass1 = dialog.findViewById(R.id.editPassNewPass1);
        newPass2 = dialog.findViewById(R.id.editPassNewPass2);
        passconfirm = dialog.findViewById(R.id.btnConfirmEditPass);
        pbPass = dialog.findViewById(R.id.profUpdateDialogPassPB);
        reset = dialog.findViewById(R.id.txtPassDialogResetPass);
        passconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pbPass.setVisibility(View.VISIBLE);
                removeKeyboard(view);
                if (oldPass.getText().toString().equals("")) {
                    pbPass.setVisibility(View.GONE);
                    infoDialog("Password field cannot be empty");
                    oldPass.requestFocus();
                } else if (newPass1.getText().toString().equals("")) {
                    pbPass.setVisibility(View.GONE);
                    infoDialog("Password field cannot be empty");
                    newPass1.requestFocus();
                } else if (newPass1.getText().toString().equals("")) {
                    pbPass.setVisibility(View.GONE);
                    infoDialog("Password field cannot be empty");
                    newPass1.requestFocus();
                } else if (!newPass1.getText().toString().equals(newPass2.getText().toString()))
                {
                    pbPass.setVisibility(View.GONE);
                    infoDialog("New passwords do not match");
                }
                else if (newPass1.getText().toString().matches("^" +
                        "(?=.*[a-zA-Z])" +      //any letter
                        "(?=.*[@#$%^&+=])" +    //at least 1 special character
                        "(?=S+$)" +           //no white spaces
                        ".{5,}" +               //at least 5 characters
                        "$"))
                {
                    pbPass.setVisibility(View.GONE);
                    infoDialog("Enter a password with special character no spaces and at least 5 words ");
                }
                else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updatePassword(oldPass.getText().toString(), newPass1.getText().toString(), dialog);
                        }
                    }, 1000);

                }

            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                startActivity(new Intent(getApplicationContext(), ResetPassword.class));

            }
        });
        dialog.show();
    }

    private void updatePassword(String pass1, String pass2, Dialog dialog) {
        CusModel model = new CusModel();
        model.setId(id);
        model.setPass(pass1);
        model.setNewPass(pass2);
        Call<CusModel> call = RetrofitConnect.getUserApi().updateUserPassword(model);
        call.enqueue(new Callback<CusModel>() {
            @Override
            public void onResponse(Call<CusModel> call, Response<CusModel> response) {
                pb.setVisibility(View.GONE);
                if (response.code() == 200) {
                    pbPass.setVisibility(View.GONE);
                    sucessDialog("Password");
                    dialog.dismiss();
                } else if (response.code() == 404) {
                    pbPass.setVisibility(View.GONE);
                    infoDialog("Password incorrect try again");
                }
            }

            @Override
            public void onFailure(Call<CusModel> call, Throwable t) {
                errorDialog(t.getMessage());
            }
        });

    }

    private void sucessDialog(String msg) {
        new AestheticDialog.Builder(ProfileUser.this, DialogStyle.FLAT, DialogType.SUCCESS)
                .setTitle("Success")
                .setMessage(msg+" changed successfully")
                .setAnimation(DialogAnimation.WINDMILL)
                .setCancelable(false)
                .setDarkMode(true)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        builder.dismiss();
                        Intent intent = new Intent(ProfileUser.this,ProfileUser.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }

    private void infoDialog(String message) {
        new AestheticDialog.Builder(ProfileUser.this, DialogStyle.FLAT, DialogType.WARNING)
                .setTitle("Warning")
                .setMessage(message)
                .setAnimation(DialogAnimation.FADE)
                .setCancelable(false)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        builder.dismiss();
                    }
                }).show();
    }

    private void errorDialog(String message) {
        new AestheticDialog.Builder(ProfileUser.this, DialogStyle.FLAT, DialogType.WARNING)
                .setTitle("Error")
                .setMessage(message)
                .setAnimation(DialogAnimation.FADE)
                .setCancelable(false)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        builder.dismiss();
                    }
                }).show();
    }

    private void getUserDetails() {
        Call<List<CusModel>> call = RetrofitConnect.getUserApi().findUserById(String.valueOf(id));
        call.enqueue(new Callback<List<CusModel>>() {
            @Override
            public void onResponse(Call<List<CusModel>> call, Response<List<CusModel>> response) {
                if (response.code() == 200) {
                    List<CusModel> data = response.body();
                    name.setText(data.get(0).getName());
                    address.setText(data.get(0).getAddress());
                    phone.setText(String.valueOf(data.get(0).getPhone()));
                    email.setText(data.get(0).getEmail());
                    layout.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<List<CusModel>> call, Throwable t) {
                errorDialog(t.getMessage());
            }
        });
    }

    private void logout() {
        sm.removeUserSession();
        sm.removeLocation();
        sm.removeAdminSession();
        notificationRemove();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private void notificationRemove() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("CustomerNotify").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(ProfileUser.this, "Task Failed" + task.getResult(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}