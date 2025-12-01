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
import Data.Notification;




public class DutyScheduleActivity extends AppCompatActivity {

    private DataUtil dataUtil;

    private AutoCompleteTextView edtDutyType, edtShift;
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
    private final String[] SHIFTS = new String[]{"Ca 1 (7h - 9h)", "Ca 2 (9h - 11h)", "Ca 3 (13h - 15h)", "Ca 4 (15h - 17h)"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lưu ý: Đảm bảo layout này tồn tại: R.layout.activity_create_schedule hoặc R.layout.activity_duty_schedule
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
        edtShift = findViewById(R.id.edt_shift);
        edtDate = findViewById(R.id.edt_date);

        // Giả định: Các ID này vẫn đúng với layout bạn đang dùng cho màn hình tạo lịch
        edtParticipantId = findViewById(R.id.edt_participant_id);
        btnCreateSchedule = findViewById(R.id.btn_create_schedule);

        chipGroupParticipants = findViewById(R.id.chip_group_participants);
    }

    private void setupDropdownAdapters() {
        Context context = this;
        edtDutyType.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, DUTY_TYPES));
        edtShift.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, SHIFTS));
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
        // Định dạng phải khớp với định dạng bạn sử dụng trong showDatePickerDialog
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Quan trọng: Ngăn chặn chuyển đổi ngày không hợp lệ (ví dụ: 30/02 thành 02/03)
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

            // Lấy ngày hiện tại, bỏ qua thời gian (giờ, phút, giây)
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Nếu ngày lịch trực nằm trước ngày hôm nay, đó là ngày trong quá khứ
            return scheduleDate.before(today.getTime());

        } catch (ParseException e) {
            // Nếu không parse được, coi như không phải ngày trong quá khứ (vì nó không hợp lệ)
            return false;
        }
    }


    // HÀM TẠO BẢN ĐỒ TRA CỨU: CHỈ LẤY SINH VIÊN (MSV - NAME)
    private Map<String, String> createLookupMap() {
        Map<String, String> map = new HashMap<>();

        try {
            // Chỉ thêm User (Sinh viên)
            for (User user : dataUtil.users.getAll()) {
                if (user.getMsv() != null && !user.getMsv().isEmpty() && user.getRole() == Data.Role.User) {
                    // Định dạng Key: "MSV - TÊN" (Chữ hoa để dễ tìm kiếm)
                    // Lưu ý: Cần đảm bảo `user.getName()` không chứa dấu gạch ngang (-) để tránh trùng lặp
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
        // Lấy danh sách Keys ("MSV - NAME")
        final List<String> displayKeys = new ArrayList<>(lookupMap.keySet());
        Collections.sort(displayKeys); // Sắp xếp A-Z
        final String[] items = displayKeys.toArray(new String[0]);

        final boolean[] checkedItems = new boolean[items.length];
        // Khôi phục trạng thái checkedItems dựa trên importedParticipantIds hiện có
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
                        // Khi Chip được tạo, nó cần label ("MSV - TÊN")
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

            // Tìm và xóa ID tương ứng
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
        String selectedShift = edtShift.getText().toString().trim();
        String currentCreatorId = "ID_ADMIN_001";

        // 1. Xử lý nhập thủ công
        String inputManual = edtParticipantId.getText().toString().trim();
        if (!inputManual.isEmpty()) {
            String inputUpper = inputManual.toUpperCase();
            String foundKey = null; // Key ("MSV - TÊN")

            for (String key : lookupMap.keySet()) {
                // Key định dạng "MSV - TÊN". Kiểm tra xem Key có bắt đầu bằng MSV người dùng nhập không
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
                    // Thêm ID và thêm Chip để hiển thị rõ ràng
                    importedParticipantIds.add(foundId);
                    addChipToGroup(foundKey);
                }
            }
            edtParticipantId.setText(""); // Xóa text nhập tay sau khi xử lý
        }

        // 2. Kiểm tra dữ liệu
        if (importedParticipantIds.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 sinh viên.", Toast.LENGTH_LONG).show();
            return;
        }

        if (roomName.isEmpty() || selectedDate.isEmpty() || selectedShift.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_LONG).show();
            return;
        }

        // KIỂM TRA TÍNH HỢP LỆ CỦA NGÀY
        if (!isDateValid(selectedDate)) {
            Toast.makeText(this, "Ngày trực không hợp lệ. Vui lòng chọn lại.", Toast.LENGTH_LONG).show();
            return;
        }

        // KIỂM TRA NGÀY TRONG QUÁ KHỨ
        if (isPastDate(selectedDate)) {
            Toast.makeText(this, "Ngày trực phải là ngày hiện tại hoặc ngày trong tương lai.", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Lưu lịch (Sử dụng List<String> assigneeIds)
        try {
            // KHÔNG CÒN LOGIC TẠO GROUP TẠM THỜI NỮA
            // Chỉ cần truyền trực tiếp importedParticipantIds vào constructor

            DutySchedule newSchedule = new DutySchedule(
                    selectedDate,
                    selectedShift,
                    selectedDutyType,
                    roomName,
                    currentCreatorId,
                    DutySchedulesStatus.Pending,
                    // THAY ĐỔI QUAN TRỌNG: Truyền List<String> thay vì String ID đơn lẻ
                    new ArrayList<>(importedParticipantIds)
            );

            dataUtil.dutySchedules.add(newSchedule);
            // Tạo thông báo khi tạo lịch thành công
            String title = "Lịch trực mới đã được tạo";
            String content = "Ngày: " + selectedDate +
                    "\nCa: " + selectedShift +
                    "\nLoại trực: " + selectedDutyType +
                    "\nPhòng: " + roomName;

            // id để null, Repository sẽ tự sinh UUID
            Notification noti = new Notification(null, title, content);
            dataUtil.notifications.add(noti);


            // Reset UI
            importedParticipantIds.clear();
            chipGroupParticipants.removeAllViews();
            updateButtonText();

            Toast.makeText(this, "Tạo lịch thành công!", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}