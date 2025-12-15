
package com.example.quanlytrucnhatvamuontrathietbi;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import com.example.quanlytrucnhatvamuontrathietbi.DutyScheduleView.DutyScheduleListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class home_admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_admin);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_settings) {
                    // Xử lý khi nhấn vào Cài đặt
                    startActivity(new Intent(home_admin.this, SettingsActivity.class));
                    return true;
                } else if (itemId == R.id.nav_account) {
                    // Xử lý khi nhấn vào Tài khoản
                    startActivity(new Intent(home_admin.this, AccountActivity.class));
                    return true;
                } else if (itemId == R.id.nav_home) {
                    // Màn hình hiện tại là Trang chủ, có thể refresh hoặc không làm gì
                    return true;
                }
                return false;
            }
        });
        // -------------------------
        // KHAI BÁO 3 NÚT
        // -------------------------
        Button btnTao = findViewById(R.id.btnTao);
        Button btnQuanLy = findViewById(R.id.btnQuanLy);
        Button btnDuyet = findViewById(R.id.btnDuyet);

        // -------------------------
        // XỬ LÝ SỰ KIỆN BẤM NÚT
        // -------------------------

        // Mở màn hình tạo lịch trực nhật
        btnTao.setOnClickListener(v ->
                startActivity(new Intent(this, DutyScheduleListActivity.class))
        );

        // Mở màn hình quản lý thiết bị
        btnQuanLy.setOnClickListener(v ->
                startActivity(new Intent(this, DeviceManagerActivity.class))
        );

        // Mở màn hình duyệt yêu cầu mượn
        btnDuyet.setOnClickListener(v ->
                startActivity(new Intent(this, BorrowRequestActivity.class))
        );
    }
}
