package com.example.quanlytrucnhatvamuontrathietbi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class AccountActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        session = new SessionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAccount);
        toolbar.setNavigationOnClickListener(v -> finish()); // <-- Mũi tên back

        TextView tvName = findViewById(R.id.tvAccountName);
        TextView tvId = findViewById(R.id.tvAccountId);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvName.setText("Tên: " + session.getName());
        tvId.setText("MSV: " + session.getUserId());

        btnLogout.setOnClickListener(v -> {
            session.clear();

            // về màn hình đăng nhập (hoặc màn chính tùy app bạn)
            Intent i = new Intent(AccountActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
