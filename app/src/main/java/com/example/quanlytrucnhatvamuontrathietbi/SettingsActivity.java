package com.example.quanlytrucnhatvamuontrathietbi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbarSettings);
        toolbar.setNavigationOnClickListener(v -> finish()); // <-- Mũi tên back

        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        Button btnAbout = findViewById(R.id.btnAbout);

        btnChangePassword.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng đổi mật khẩu (chưa triển khai)", Toast.LENGTH_SHORT).show()
        );

        btnAbout.setOnClickListener(v ->
                Toast.makeText(this, "Quản lý trực nhật & mượn trả thiết bị", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
