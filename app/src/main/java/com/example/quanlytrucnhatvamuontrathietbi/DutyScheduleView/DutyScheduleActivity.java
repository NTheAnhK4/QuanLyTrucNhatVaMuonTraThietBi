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
import android.widget.Space;

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
import Data.DutySchedulesStatus; // ĐÃ BAO GỒM Cancelled
import Data.User;
import Data.Notification;


public class DutyScheduleActivity extends AppCompatActivity {

    private DataUtil dataUtil;

    private AutoCompleteTextView edtDutyType;
    private AutoCompleteTextView edtStartShift, edtEndShift;
    private TextInputEditText edtRoom, edtDate;

    private TextInputEditText edtParticipantId;
    private Button btnCreateSchedule;
    private MaterialToolbar toolbar;

    private ChipGroup chipGroupParticipants;
    private Map<String, String> lookupMap;
    private List<String> importedParticipantIds = new ArrayList<>();

    private final String[] DUTY_TYPES = new String[]{"Phòng lý thuyết", "Phòng thực hành", "Lao động công ích"};

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

    private boolean isShiftSequenceValid(String startShiftStr, String endShiftStr) {
        try {
            int startShiftNum = Integer.parseInt(startShiftStr.replaceAll("[^0-9]", ""));
            int endShiftNum = Integer.parseInt(endShiftStr.replaceAll("[^0-9]", ""));
            return endShiftNum >= startShiftNum;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int parseShift(String shiftStr) {
        try {
            return Integer.parseInt(shiftStr.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // HÀM KIỂM TRA XUNG ĐỘT LỊCH TRỰC
    private boolean checkScheduleConflict(String roomName, String dateStr, String startShiftStr, String endShiftStr, List<String> participantIds) {

        int newStartShift = parseShift(startShiftStr);
        int newEndShift = parseShift(endShiftStr);

        if (newStartShift == -1 || newEndShift == -1) {
            return true;
        }

        for (DutySchedule existingSchedule : dataUtil.dutySchedules.getAll()) {

            if (existingSchedule.getStatus() == DutySchedulesStatus.Completed || existingSchedule.getStatus() == DutySchedulesStatus.Cancelled) {
                continue;
            }

            boolean sameRoomAndDate = existingSchedule.getClassName().equalsIgnoreCase(roomName) &&
                    existingSchedule.getDay().equals(dateStr);

            if (sameRoomAndDate) {
                int existingStartShift = parseShift(existingSchedule.getStartShift());
                int existingEndShift = parseShift(existingSchedule.getEndShift());

                boolean shiftOverlap = newStartShift <= existingEndShift && newEndShift >= existingStartShift;

                if (shiftOverlap) {

                    for (String newParticipantId : participantIds) {
                        if (existingSchedule.getAssigneeIds().contains(newParticipantId)) {
                            Toast.makeText(this, "Sinh viên đã chọn bị trùng lịch trực tại: " + existingSchedule.getClassName() + ", Ngày: " + existingSchedule.getDay() + ", Ca: " + existingSchedule.getStartShift() + "-" + existingSchedule.getEndShift(), Toast.LENGTH_LONG).show();
                            return true;
                        }
                    }

                    // Kiểm tra xung đột Lịch (Nếu hai lịch tạo trùng nhau hoàn toàn về Phòng/Ngày/Ca)
                    if (newStartShift == existingStartShift && newEndShift == existingEndShift) {
                        Toast.makeText(this, "Lịch trực này đã tồn tại tại: " + existingSchedule.getClassName() + ", Ngày: " + existingSchedule.getDay() + ", Ca: " + existingSchedule.getStartShift() + "-" + existingSchedule.getEndShift(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
            }
        }

        return false;
    }

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


    private void validateAndSaveSchedule() {
        String roomName = edtRoom.getText().toString().trim();
        String selectedDate = edtDate.getText().toString().trim();
        String selectedDutyType = edtDutyType.getText().toString().trim();

        String selectedStartShift = edtStartShift.getText().toString().trim();
        String selectedEndShift = edtEndShift.getText().toString().trim();

        String currentCreatorId = "ID_ADMIN_001";

        // Xử lý nhập
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

        // Kiểm tra dữ liệu
        if (importedParticipantIds.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 sinh viên.", Toast.LENGTH_LONG).show();
            return;
        }

        if (roomName.isEmpty() || selectedDate.isEmpty() || selectedStartShift.isEmpty() || selectedEndShift.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc (Phòng, Ngày, Ca Bắt đầu, Ca Kết thúc).", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isDateValid(selectedDate)) {
            Toast.makeText(this, "Ngày trực không hợp lệ. Vui lòng chọn lại.", Toast.LENGTH_LONG).show();
            return;
        }

        if (isPastDate(selectedDate)) {
            Toast.makeText(this, "Ngày trực phải là ngày hiện tại hoặc ngày trong tương lai.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isShiftSequenceValid(selectedStartShift, selectedEndShift)) {
            Toast.makeText(this, "Ca Kết thúc phải lớn hơn hoặc bằng Ca Bắt đầu.", Toast.LENGTH_LONG).show();
            return;
        }

        // KIỂM TRA XUNG ĐỘT
        if (checkScheduleConflict(roomName, selectedDate, selectedStartShift, selectedEndShift, importedParticipantIds)) {
            return;
        }

        // Lưu lịch
        try {
            DutySchedule newSchedule = new DutySchedule(
                    selectedDate,
                    selectedStartShift,
                    selectedEndShift,
                    selectedDutyType,
                    roomName,
                    currentCreatorId,
                    DutySchedulesStatus.Pending,
                    new ArrayList<>(importedParticipantIds)
            );

            dataUtil.dutySchedules.add(newSchedule);

            // Tạo thông báo
            String title = "Lịch trực mới đã được tạo";
            String content = "Ngày: " + selectedDate +
                    "\nCa: " + selectedStartShift + " - " + selectedEndShift +
                    "\nLoại trực: " + selectedDutyType +
                    "\nPhòng: " + roomName;

            Notification noti = new Notification(null, title, content);
            dataUtil.notifications.add(noti);


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