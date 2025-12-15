package com.example.quanlytrucnhatvamuontrathietbi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytrucnhatvamuontrathietbi.DutyScheduleView.DutyScheduleListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import Ui.thongbao.ThongBaoActivity;   // ✅ THÊM IMPORT

public class home_admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_admin);

        // ✅ CLICK ICON THÔNG BÁO
        ImageView ivNotifications = findViewById(R.id.iv_notifications);
        ivNotifications.setOnClickListener(v ->
                startActivity(new Intent(home_admin.this, ThongBaoActivity.class))
        );

        // ===== BOTTOM NAV =====
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_settings) {
                    startActivity(new Intent(home_admin.this, SettingsActivity.class));
                    return true;
                } else if (itemId == R.id.nav_account) {
                    startActivity(new Intent(home_admin.this, AccountActivity.class));
                    return true;
                } else if (itemId == R.id.nav_home) {
                    // đang ở Trang chủ
                    return true;
                }
                return false;
            }
        });

        // -------------------------
        // 3 NÚT CHỨC NĂNG
        // -------------------------
        Button btnTao = findViewById(R.id.btnTao);
        Button btnQuanLy = findViewById(R.id.btnQuanLy);
        Button btnDuyet = findViewById(R.id.btnDuyet);

        btnTao.setOnClickListener(v ->
                startActivity(new Intent(home_admin.this, DutyScheduleListActivity.class))
        );

        btnQuanLy.setOnClickListener(v ->
                startActivity(new Intent(home_admin.this, DeviceManagerActivity.class))
        );

        btnDuyet.setOnClickListener(v ->
                startActivity(new Intent(home_admin.this, BorrowRequestActivity.class))
        );
    }
}
