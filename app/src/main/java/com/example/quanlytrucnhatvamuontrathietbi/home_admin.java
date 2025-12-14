
package com.example.quanlytrucnhatvamuontrathietbi;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.content.Intent;
import com.example.quanlytrucnhatvamuontrathietbi.DutyScheduleView.DutyScheduleActivity;

public class home_admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
                startActivity(new Intent(this, DutyScheduleActivity.class))
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
