package com.example.quanlytrucnhatvamuontrathietbi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText ed_username;
    private EditText ed_password;
    private Button bt_login;

    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        ed_username = findViewById(R.id.et_username);
        ed_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.btn_login);
        ed_username.setText("admin");
        ed_password.setText("admin");

        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        RegisterActivity.class);

                startActivity(intent);
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ed_username.getText().toString();
                String password = ed_password.getText().toString();
                if (username.equals("admin") && password.equals("admin")) {
                    Intent intent = new Intent(getApplicationContext(),
                            MainActivity.class);

                    startActivity(intent);

                } else {
                    Toast.makeText(
                            LoginActivity.this,
                            "Username or Password incorrect!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

