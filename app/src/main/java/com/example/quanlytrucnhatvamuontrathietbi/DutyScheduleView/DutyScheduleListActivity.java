package com.example.quanlytrucnhatvamuontrathietbi.DutyScheduleView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlytrucnhatvamuontrathietbi.AccountActivity;
import com.example.quanlytrucnhatvamuontrathietbi.R;
import com.example.quanlytrucnhatvamuontrathietbi.SettingsActivity;
import com.example.quanlytrucnhatvamuontrathietbi.home_admin;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import Data.DataUtil;
import Data.DutySchedule;
import Data.DutySchedulesStatus;

import java.util.Collections;
import java.util.List;

public class DutyScheduleListActivity extends AppCompatActivity {

    private DataUtil dataUtil;
    private RecyclerView rvSchedules;
    private DutyScheduleAdapter adapter;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty_schedule_list);

        dataUtil = DataUtil.getInstance(getApplicationContext());

        findViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_settings) {
                    // Xử lý khi nhấn vào Cài đặt
                    startActivity(new Intent(DutyScheduleListActivity.this, SettingsActivity.class));
                    return true;
                } else if (itemId == R.id.nav_account) {
                    // Xử lý khi nhấn vào Tài khoản
                    startActivity(new Intent(DutyScheduleListActivity.this, AccountActivity.class));
                    return true;
                } else if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(DutyScheduleListActivity.this, home_admin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật danh sách khi quay lại màn hình
        loadSchedules();
    }

    private void findViews() {
        rvSchedules = findViewById(R.id.rv_duty_schedules);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        fabAddSchedule = findViewById(R.id.fab_add_schedule);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // Đảm bảo navigation icon được hiển thị (Mặc dù đã set trong XML, đây là bước kiểm tra tốt)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Đặt listener cho navigation icon (nút Back)
        toolbar.setNavigationOnClickListener(v -> {
            // Khi người dùng nhấn nút Back trên Toolbar, kết thúc Activity hiện tại
            onBackPressed();
        });
    }

    private void setupRecyclerView() {
        rvSchedules.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo adapter với danh sách hiện có
        adapter = new DutyScheduleAdapter(this, dataUtil.dutySchedules.getAll(), dataUtil);
        rvSchedules.setAdapter(adapter);
    }

    private void setupListeners() {
        // Listener cho nút FAB: Mở giao diện tạo lịch (DutyScheduleActivity)
        fabAddSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(DutyScheduleListActivity.this, DutyScheduleActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Tải và sắp xếp danh sách lịch trực, cập nhật RecyclerView và trạng thái trống.
     */
    public void loadSchedules() {
        List<DutySchedule> schedules = dataUtil.dutySchedules.getAll();

        // Sắp xếp: Lịch chưa hoàn thành (Pending) lên trước lịch đã hoàn thành
        Collections.sort(schedules, (s1, s2) -> {
            boolean isS1Pending = (s1.getStatus() == DutySchedulesStatus.Pending);
            boolean isS2Pending = (s2.getStatus() == DutySchedulesStatus.Pending);

            if (isS1Pending && !isS2Pending) return -1; // S1 lên trước
            if (!isS1Pending && isS2Pending) return 1;  // S2 lên trước
            return 0;
        });

        adapter.updateList(schedules);

        // Hiển thị trạng thái trống
        if (schedules.isEmpty()) {
            rvSchedules.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvSchedules.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }
}