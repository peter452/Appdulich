package com.example.myapplication.src.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.user_reponse.User;
import com.example.myapplication.models.user_reponse.UserReponse;
import com.example.myapplication.services.APIServices;
import com.example.myapplication.services.DataService;
import com.example.myapplication.src.dialog.LoadingDialog;
import com.example.myapplication.util.listener_change_edittext.addListenerOnTextChange;
import com.example.myapplication.util.validations.Validations;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private TextView mBtRegister;
    private EditText editTextUserName,editTextPassWord;
    private TextView txtForgetPass;
    private Button btnLogin;
    final LoadingDialog loadingDialog = new LoadingDialog();
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        anhxa();
        if(checkUserAcount()){
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_ride);
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }else{
            onclicked();
            addtextChangeListener();
        }
    }

    private void addtextChangeListener() {
        editTextUserName.addTextChangedListener(new addListenerOnTextChange(LoginActivity.this, 1, editTextUserName));
        editTextPassWord.addTextChangedListener(new addListenerOnTextChange(LoginActivity.this, 2, editTextPassWord));
    }

    private void onclicked() {
        txtForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextUserName.getText().toString().equals("") && !editTextUserName.getText().toString().endsWith("@gmail.com")){
                    editTextUserName.setError("Please enter email in the keyboar");
                }else{
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_ride);
                    Intent intent = new Intent(getApplicationContext(),ForgetPassword.class);
                    intent.putExtra("email",editTextUserName.getText().toString());
                    startActivity(intent);
                }
            }
        });

        mBtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_ride);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validations.isName(editTextUserName.getText().toString().trim()) &&
                Validations.isPass(editTextPassWord.getText().toString())){
                    FragmentManager fragmentManager= getSupportFragmentManager();
                    loadingDialog.show(fragmentManager,"123");

                    DataService dataService = APIServices.getService();
                    Call<UserReponse>callback = dataService.postLogin(editTextUserName.getText().toString(),editTextPassWord.getText().toString());
                    callback.enqueue(new Callback<UserReponse>() {
                        @Override
                        public void onResponse(Call<UserReponse> call, Response<UserReponse> response) {
                            Log.d("AAA","postLogin: "+response.toString());
                            loadingDialog.dismiss();
                           if(response.isSuccessful()){
                               User user = response.body().getUser();
                               editor.putString("username",user.getName());
                               editor.putString("password",user.getPassword());
                               editor.putInt("idUser",user.getId());
                               String json = new Gson().toJson(user);
                               editor.putString("user",json);
                               editor.commit();
                               
                               overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_ride);
                               startActivity(new Intent(getApplicationContext(),MainActivity.class));
                               finish();
                           }else {
                               Toast.makeText(LoginActivity.this, "Please  check your account or internet connection", Toast.LENGTH_SHORT).show();
                           }
                        }

                        @Override
                        public void onFailure(Call<UserReponse> call, Throwable t) {
                            Log.d("AAA","errPostLogin: "+t.toString());
                            loadingDialog.dismiss();
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "Please  check your account or internet connection", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "err login format", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void anhxa() {
        sharedPreferences = getSharedPreferences("datalogin",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        txtForgetPass = findViewById(R.id.txtForgetPass);
        mBtRegister = findViewById(R.id.btRegister);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPassWord = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    static public boolean checkUserAcount(){
        if(sharedPreferences.getString("username","") != null &&
                sharedPreferences.getString("password","") != null &&
                sharedPreferences.getInt("idUser",0) != 0){
            return true;
        }else{
            return false;
        }
    }
}
