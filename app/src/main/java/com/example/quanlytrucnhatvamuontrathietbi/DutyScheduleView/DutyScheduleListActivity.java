package com.example.quanlytrucnhatvamuontrathietbi.DutyScheduleView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlytrucnhatvamuontrathietbi.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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