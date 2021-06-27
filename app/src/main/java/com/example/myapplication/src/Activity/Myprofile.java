package com.example.myapplication.src.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.models.user_reponse.User;
import com.example.myapplication.services.APIServices;
import com.example.myapplication.services.DataService;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Myprofile extends AppCompatActivity {
    private EditText txtusername,txtemail,txtsdt,txtage,txtgender;
    private ImageView imgback;
    private TextView mTxtUpdate;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        anhxa();
        setData();
        listenerOnclicked();
    }

    private void listenerOnclicked() {
        mTxtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataService dataService = APIServices.getService();
                Call<String>call = dataService.updateUser(user.getId(),txtusername.getText().toString(),
                        txtemail.getText().toString(),txtsdt.getText().toString(),txtage.getText().toString(),
                        txtgender.getText().toString().equals("Boy") ? 0 : 1);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("AAA", "onResponse update user "+response.toString());
                        if(response.isSuccessful()){
                            Log.d("AAA", "onResponse update user1 "+response.body());
                            if(response.body().equals("Successfully")){
                                user.setName(txtusername.getText().toString());
                                user.setPhone(txtsdt.getText().toString());
                                user.setAge(txtage.getText().toString());
                                user.setGender(txtgender.getText().toString().equals("Boy") ? 0 : 1);
                                String json = new Gson().toJson(user);
                                LoginActivity.editor.putString("user",json);
                                LoginActivity.editor.commit();
                                Toast.makeText(Myprofile.this, "Update Success", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(Myprofile.this, "Update Error", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(Myprofile.this, "Update Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("AAA", "onFailure update user: "+t.getMessage().toString());
                        Toast.makeText(Myprofile.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void setData() {
        user = new Gson().fromJson(LoginActivity.sharedPreferences.getString("user",""),User.class);
        txtusername.setText(user.getName());
        txtsdt.setText(user.getPhone()+"");
        txtemail.setText(user.getEmail());
        txtage.setText(user.getAge()+"");
        txtsdt.setText(user.getPhone()+"");
        if(user.getGender() != null){
            txtgender.setText(user.getGender().equals("0") ? "Male" : "Female");
        }
    }


    private void anhxa() {
        mTxtUpdate = findViewById(R.id.mTxtUpdate);
        txtgender = findViewById(R.id.txtgender);
        txtage = findViewById(R.id.txtage);
        txtsdt = findViewById(R.id.txtsdt);
        txtemail = findViewById(R.id.txtemail);
        txtusername = findViewById(R.id.txtusername);
        imgback = findViewById(R.id.imgback);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}