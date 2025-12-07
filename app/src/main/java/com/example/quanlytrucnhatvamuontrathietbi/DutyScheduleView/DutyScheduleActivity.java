package com.example.quanlytrucnhatvamuontrathietbi.DutyScheduleView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlytrucnhatvamuontrathietbi.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.appbar.MaterialToolbar;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import android.widget.Space; // BỔ SUNG NẾU SỬ DỤNG Space TRONG XML

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Data.DataUtil;
import Data.DutySchedule;
import Data.DutySchedulesStatus;
import Data.User;

public class DutyScheduleActivity extends AppCompatActivity {

    private DataUtil dataUtil;

    private AutoCompleteTextView edtDutyType;
    // CẬP NHẬT: Thay thế edtShift bằng hai trường mới
    private AutoCompleteTextView edtStartShift, edtEndShift;
    private TextInputEditText edtRoom, edtDate;

    private TextInputEditText edtParticipantId;
    private Button btnCreateSchedule;
    private MaterialToolbar toolbar;

    private ChipGroup chipGroupParticipants;
    // Map này lưu trữ Key là "MSV - TÊN" và Value là User ID
    private Map<String, String> lookupMap;

    // List này lưu trữ các User ID được chọn
    private List<String> importedParticipantIds = new ArrayList<>();

    private final String[] DUTY_TYPES = new String[]{"Phòng lý thuyết", "Phòng thực hành", "Lao động công ích"};

    // KHỞI TẠO MẢNG CHỨA CÁC CA (Ca 1 -> Ca 17)
    private final String[] SHIFTS;

    public DutyScheduleActivity() {
        SHIFTS = new String[17];
        for (int i = 0; i < 17; i++) {
            SHIFTS[i] = "Ca " + (i + 1);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        dataUtil = DataUtil.getInstance(getApplicationContext());
        lookupMap = createLookupMap();

        findViews();
        setupToolbar();
        setupDropdownAdapters();
        setupListeners();
        updateButtonText();
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);

        edtDutyType = findViewById(R.id.edt_duty_type);
        edtRoom = findViewById(R.id.edt_room);

        // TÌM KIẾM CÁC ID MỚI
        edtStartShift = findViewById(R.id.edt_start_shift);
        edtEndShift = findViewById(R.id.edt_end_shift);

        edtDate = findViewById(R.id.edt_date);

        edtParticipantId = findViewById(R.id.edt_participant_id);
        btnCreateSchedule = findViewById(R.id.btn_create_schedule);

        chipGroupParticipants = findViewById(R.id.chip_group_participants);
    }

    private void setupDropdownAdapters() {
        Context context = this;
        ArrayAdapter<String> dutyTypeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, DUTY_TYPES);
        edtDutyType.setAdapter(dutyTypeAdapter);

        // ADAPTER CHUNG CHO CẢ HAI TRƯỜNG CA
        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, SHIFTS);
        edtStartShift.setAdapter(shiftAdapter);
        edtEndShift.setAdapter(shiftAdapter);
    }

    private void setupListeners() {
        edtDate.setOnClickListener(v -> showDatePickerDialog());
        edtDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) showDatePickerDialog();
        });

        TextInputLayout tilParticipantContainer = findViewById(R.id.til_participant_id_container);
        tilParticipantContainer.setEndIconOnClickListener(v -> {
            edtParticipantId.setText("");
            showParticipantSelectionDialog();
        });

        btnCreateSchedule.setOnClickListener(v -> validateAndSaveSchedule());
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d/%02d/%d", selectedDay, (selectedMonth + 1), selectedYear);
                    edtDate.setText(selectedDate);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private boolean isDateValid(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isPastDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date scheduleDate = sdf.parse(dateStr);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return scheduleDate.before(today.getTime());

        } catch (ParseException e) {
            return false;
        }
    }

    // HÀM KIỂM TRA THỨ TỰ CA (Ca bắt đầu <= Ca kết thúc)
    private boolean isShiftSequenceValid(String startShiftStr, String endShiftStr) {
        try {
            // Lấy số từ chuỗi "Ca X"
            // Ví dụ: "Ca 10" -> "10"
            int startShiftNum = Integer.parseInt(startShiftStr.replaceAll("[^0-9]", ""));
            int endShiftNum = Integer.parseInt(endShiftStr.replaceAll("[^0-9]", ""));

            // Ca kết thúc phải lớn hơn hoặc bằng Ca bắt đầu
            return endShiftNum >= startShiftNum;

        } catch (NumberFormatException e) {
            // Nếu parse lỗi (chuỗi không đúng định dạng "Ca X"), coi như không hợp lệ
            return false;
        }
    }

    // HÀM TẠO BẢN ĐỒ TRA CỨU: CHỈ LẤY SINH VIÊN (MSV - NAME)
    private Map<String, String> createLookupMap() {
        Map<String, String> map = new HashMap<>();

        try {
            for (User user : dataUtil.users.getAll()) {
                if (user.getMsv() != null && !user.getMsv().isEmpty() && user.getRole() == Data.Role.User) {
                    String label = user.getMsv().trim().toUpperCase() + " - " + user.getName();
                    map.put(label, user.getId());
                }
            }
        } catch (Exception e) {
            Log.e("DutyScheduleActivity", "Lỗi tạo bản đồ tra cứu: " + e.getMessage());
        }
        return map;
    }

    // HÀM MỞ DIALOG CHỌN
    private void showParticipantSelectionDialog() {
        final List<String> displayKeys = new ArrayList<>(lookupMap.keySet());
        Collections.sort(displayKeys);
        final String[] items = displayKeys.toArray(new String[0]);

        final boolean[] checkedItems = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            String id = lookupMap.get(items[i]);
            checkedItems[i] = importedParticipantIds.contains(id);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn Sinh viên");

        builder.setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton("Xác nhận", (dialog, id) -> {
            importedParticipantIds.clear();
            chipGroupParticipants.removeAllViews();

            for (int i = 0; i < items.length; i++) {
                if (checkedItems[i]) {
                    String key = items[i];
                    String participantId = lookupMap.get(key);

                    if (participantId != null && !importedParticipantIds.contains(participantId)) {
                        importedParticipantIds.add(participantId);
                        addChipToGroup(key);
                    }
                }
            }
            updateButtonText();
        });

        builder.setNegativeButton("Hủy", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void addChipToGroup(String label) {
        Chip chip = new Chip(this);
        chip.setText(label);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);

        chip.setOnCloseIconClickListener(v -> {
            chipGroupParticipants.removeView(chip);

            String participantId = lookupMap.get(label);
            if (participantId != null) {
                importedParticipantIds.remove(participantId);
            }
            updateButtonText();
        });

        chipGroupParticipants.addView(chip);
    }

    private void updateButtonText() {
        int count = importedParticipantIds.size();
        if (count > 0) {
            btnCreateSchedule.setText("Tạo lịch (" + count + " sinh viên)");
        } else {
            btnCreateSchedule.setText("Tạo lịch");
        }
    }

    // HÀM TẠO LỊCH (ĐÃ SỬA LỖI LOGIC GROUP)
    private void validateAndSaveSchedule() {
        String roomName = edtRoom.getText().toString().trim();
        String selectedDate = edtDate.getText().toString().trim();
        String selectedDutyType = edtDutyType.getText().toString().trim();

        // THU THẬP CA BẮT ĐẦU VÀ KẾT THÚC
        String selectedStartShift = edtStartShift.getText().toString().trim();
        String selectedEndShift = edtEndShift.getText().toString().trim();

        String currentCreatorId = "ID_ADMIN_001";

        // 1. Xử lý nhập thủ công
        String inputManual = edtParticipantId.getText().toString().trim();
        if (!inputManual.isEmpty()) {
            String inputUpper = inputManual.toUpperCase();
            String foundKey = null;

            for (String key : lookupMap.keySet()) {
                if (key.startsWith(inputUpper + " -") || key.equals(inputUpper)) {
                    foundKey = key;
                    break;
                }
            }

            if (foundKey == null) {
                Toast.makeText(this, "Không tìm thấy sinh viên có MSV: " + inputManual, Toast.LENGTH_LONG).show();
                return;
            } else {
                String foundId = lookupMap.get(foundKey);
                if (foundId != null && !importedParticipantIds.contains(foundId)) {
                    importedParticipantIds.add(foundId);
                    addChipToGroup(foundKey);
                }
            }
            edtParticipantId.setText("");
        }

        // 2. Kiểm tra dữ liệu
        if (importedParticipantIds.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 sinh viên.", Toast.LENGTH_LONG).show();
            return;
        }

        if (roomName.isEmpty() || selectedDate.isEmpty() || selectedStartShift.isEmpty() || selectedEndShift.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc (Phòng, Ngày, Ca Bắt đầu, Ca Kết thúc).", Toast.LENGTH_LONG).show();
            return;
        }

        // KIỂM TRA TÍNH HỢP LỆ CỦA NGÀY
        if (!isDateValid(selectedDate)) {
            Toast.makeText(this, "Ngày trực không hợp lệ. Vui lòng chọn lại.", Toast.LENGTH_LONG).show();
            return;
        }

        if (isPastDate(selectedDate)) {
            Toast.makeText(this, "Ngày trực phải là ngày hiện tại hoặc ngày trong tương lai.", Toast.LENGTH_LONG).show();
            return;
        }

        // KIỂM TRA THỨ TỰ CA
        if (!isShiftSequenceValid(selectedStartShift, selectedEndShift)) {
            Toast.makeText(this, "Ca Kết thúc phải lớn hơn hoặc bằng Ca Bắt đầu.", Toast.LENGTH_LONG).show();
            return;
        }


        // 3. Lưu lịch
        try {
            DutySchedule newSchedule = new DutySchedule(
                    selectedDate,
                    selectedStartShift, // THÊM MỚI
                    selectedEndShift,   // THÊM MỚI
                    selectedDutyType,
                    roomName,
                    currentCreatorId,
                    DutySchedulesStatus.Pending,
                    new ArrayList<>(importedParticipantIds)
            );

            dataUtil.dutySchedules.add(newSchedule);

            // Reset UI
            importedParticipantIds.clear();
            chipGroupParticipants.removeAllViews();
            updateButtonText();

            // Reset các trường đã nhập
            edtDate.setText("");
            edtRoom.setText("");
            edtDutyType.setText("");
            edtStartShift.setText("");
            edtEndShift.setText("");

            Toast.makeText(this, "Tạo lịch thành công!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}