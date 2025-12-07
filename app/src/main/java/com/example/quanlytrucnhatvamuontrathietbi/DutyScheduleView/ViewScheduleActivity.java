package com.example.quanlytrucnhatvamuontrathietbi.DutyScheduleView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytrucnhatvamuontrathietbi.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import Data.DataUtil;
import Data.DutySchedule;
import Data.User;
import java.util.ArrayList;
import java.util.List;

public class ViewScheduleActivity extends AppCompatActivity {

    private DataUtil dataUtil;
    private String scheduleId;
    private DutySchedule currentSchedule;

    private MaterialToolbar toolbar;
    private TextInputEditText edtParticipantName, edtDutyType, edtRoom, edtDate, edtShiftRange, edtScheduleStatus;
    private Button btnDelete, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        dataUtil = DataUtil.getInstance(getApplicationContext());

        if (getIntent() != null && getIntent().hasExtra("SCHEDULE_ID")) {
            scheduleId = getIntent().getStringExtra("SCHEDULE_ID");
            loadScheduleData(scheduleId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID lịch!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViews();
        setupToolbar();
        displayData();
        setupListeners();
    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        edtParticipantName = findViewById(R.id.edt_participant_name);
        edtScheduleStatus = findViewById(R.id.edt_schedule_status);
        edtDutyType = findViewById(R.id.edt_duty_type);
        edtRoom = findViewById(R.id.edt_room);
        edtDate = findViewById(R.id.edt_date);

        // SỬ DỤNG ID CŨ (edt_shift) NHƯNG ĐÃ ĐỔI TÊN BIẾN
        edtShiftRange = findViewById(R.id.edt_shift);

        btnDelete = findViewById(R.id.btn_delete);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadScheduleData(String id) {
        // Giả định: dutySchedules.getAll() trả về danh sách các DutySchedule có trường startShift/endShift
        for (DutySchedule schedule : dataUtil.dutySchedules.getAll()) {
            if (schedule.getId().equals(id)) {
                currentSchedule = schedule;
                break;
            }
        }
    }

    private void displayData() {
        if (currentSchedule == null) {
            Toast.makeText(this, "Không tìm thấy thông tin lịch!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin
        edtDutyType.setText(currentSchedule.getDutyType());
        edtRoom.setText(currentSchedule.getClassName());
        edtDate.setText(currentSchedule.getDay());

        // THAY ĐỔI QUAN TRỌNG: Hiển thị Ca Bắt đầu - Ca Kết thúc
        String shiftRange = currentSchedule.getStartShift() + " - " + currentSchedule.getEndShift();
        edtShiftRange.setText(shiftRange);

        // Hiển thị trạng thái
        String statusText = currentSchedule.getStatus().toString().equals("Completed") ? "Đã hoàn thành" : "Chờ thực hiện";
        edtScheduleStatus.setText(statusText);

        // Xử lý hiển thị danh sách sinh viên
        String assigneeNames = resolveAssigneeNames(currentSchedule.getAssigneeIds());
        edtParticipantName.setText(assigneeNames);
    }

    /**
     * Hàm tìm tên sinh viên dựa trên danh sách ID và format thành chuỗi.
     */
    private String resolveAssigneeNames(List<String> assigneeIds) {
        if (assigneeIds == null || assigneeIds.isEmpty()) {
            return "Chưa phân công";
        }

        List<String> resolvedDetails = new ArrayList<>();

        for (String userId : assigneeIds) {
            // Chỉ kiểm tra User
            for (User user : dataUtil.users.getAll()) {
                if (user.getId().equals(userId)) {
                    // Hiển thị MSV - Tên
                    resolvedDetails.add(user.getMsv() + " - " + user.getName());
                    break;
                }
            }
        }

        if (resolvedDetails.isEmpty()) {
            return "Không xác định";
        } else {
            // Hiển thị mỗi người trên một dòng
            return String.join("\n", resolvedDetails);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch trực")
                .setMessage("Bạn có chắc chắn muốn xóa lịch trực này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Xóa khỏi DataUtil
                    dataUtil.dutySchedules.remove(currentSchedule);
                    Toast.makeText(this, "Đã xóa lịch thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}