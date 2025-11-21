package com.example.quanlytrucnhatvamuontrathietbi;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import Data.DataUtil;
import Data.Equipment;
import Data.EquipmentStatus;

public class DeviceManagerActivity extends AppCompatActivity {

    private Button btnPickImage, btnAddDevice, btnEditDevice;
    private ImageView imgPreview;
    private EditText inputDeviceName, inputDeviceDescription;

    private RecyclerView deviceRecycler;
    private DeviceRecyclerAdapter adapter;
    private ArrayList<Equipment> deviceList;

    private Uri lastPickedUri = null;
    private String lastPickedPath = null;

    private Equipment editingDevice = null; // Thiết bị đang chỉnh sửa

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_device_manager);

        getWidget();
        setupImagePicker();

        // Lấy danh sách thiết bị từ DataUtil
        deviceList = DataUtil.getInstance(this).equipments.getAll();

        setupRecyclerView();
        setupEvents();
    }

    private void getWidget() {
        btnPickImage = findViewById(R.id.btnPickImage);
        imgPreview = findViewById(R.id.imgPreview);
        btnAddDevice = findViewById(R.id.btnAddDevice);

        inputDeviceName = findViewById(R.id.inputDeviceName);
        inputDeviceDescription = findViewById(R.id.inputDeviceDescription);

        deviceRecycler = findViewById(R.id.deviceRecycler);
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        lastPickedUri = uri;
                        imgPreview.setImageURI(uri);
                    }
                }
        );
    }

    private void setupRecyclerView() {
        adapter = new DeviceRecyclerAdapter(this, deviceList);

        // Listener click item -> hiển thị lên form
        adapter.setItemClickListener(e -> {
            inputDeviceName.setText(e.getName());
            inputDeviceDescription.setText(e.getDescription());
            imgPreview.setImageURI(Uri.fromFile(new File(e.getImageUri())));

            editingDevice = e;
            lastPickedPath = e.getImageUri();
            lastPickedUri = Uri.fromFile(new File(e.getImageUri()));
        });

        // Listener cho nút Edit/Delete
        adapter.setListener(new DeviceRecyclerAdapter.ItemActionListener() {
            @Override
            public void onEdit(Equipment e) {
                // Khi bấm Edit, sử dụng luôn form hiện tại
                if (editingDevice == null || !editingDevice.equals(e)) {
                    // Nếu chưa chọn item hoặc chọn khác, hiển thị lên form
                    inputDeviceName.setText(e.getName());
                    inputDeviceDescription.setText(e.getDescription());
                    imgPreview.setImageURI(Uri.fromFile(new File(e.getImageUri())));
                    editingDevice = e;
                    lastPickedPath = e.getImageUri();
                    lastPickedUri = Uri.fromFile(new File(e.getImageUri()));
                } else {
                    // Lấy dữ liệu từ form để cập nhật
                    updateDevice();
                }
            }

            @Override
            public void onDelete(Equipment e) {
                int index = deviceList.indexOf(e);
                if (index >= 0) {
                    deviceList.remove(index);
                    DataUtil.getInstance(DeviceManagerActivity.this).equipments.remove(e);
                    adapter.notifyItemRemoved(index);

                    // Reset form nếu đang chỉnh sửa thiết bị này
                    if (editingDevice != null && editingDevice.equals(e)) {
                        resetForm();
                    }
                }
            }
        });

        deviceRecycler.setLayoutManager(new LinearLayoutManager(this));
        deviceRecycler.setAdapter(adapter);
    }

    private void setupEvents() {
        btnPickImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnAddDevice.setOnClickListener(v -> {
            if (editingDevice != null) {
                // Nếu đang chỉnh sửa -> cập nhật
                updateDevice();
            } else {
                // Thêm mới
                addNewDevice();
            }
        });
    }

    private void updateDevice() {
        String name = inputDeviceName.getText().toString().trim();
        String desc = inputDeviceDescription.getText().toString().trim();

        if (name.isEmpty()) {
            inputDeviceName.setError("Tên thiết bị không được để trống");
            inputDeviceName.requestFocus();
            return;
        }

        if (lastPickedUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh thiết bị", Toast.LENGTH_SHORT).show();
            return;
        }

        if (desc.isEmpty()) desc = "";

        // Lưu ảnh mới nếu có thay đổi
        lastPickedPath = savePickedImage(lastPickedUri);
        if (lastPickedPath == null) {
            Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin thiết bị
        editingDevice.setName(name);
        editingDevice.setDescription(desc);
        editingDevice.setImageUri(lastPickedPath);

        // Cập nhật DataUtil
        DataUtil.getInstance(this).equipments.update(editingDevice);

        // Cập nhật RecyclerView
        int index = deviceList.indexOf(editingDevice);
        adapter.notifyItemChanged(index);

        Toast.makeText(this, "Cập nhật thiết bị thành công", Toast.LENGTH_SHORT).show();

        resetForm();
    }

    private void addNewDevice() {
        String name = inputDeviceName.getText().toString().trim();
        String desc = inputDeviceDescription.getText().toString().trim();

        if (name.isEmpty()) {
            inputDeviceName.setError("Tên thiết bị không được để trống");
            inputDeviceName.requestFocus();
            return;
        }

        if (lastPickedUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh thiết bị", Toast.LENGTH_SHORT).show();
            return;
        }

        if (desc.isEmpty()) desc = "";

        lastPickedPath = savePickedImage(lastPickedUri);
        if (lastPickedPath == null) {
            Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = UUID.randomUUID().toString();
        Equipment newDevice = new Equipment(id, name, desc, lastPickedPath, EquipmentStatus.Available);

        DataUtil.getInstance(this).equipments.add(newDevice);
        adapter.notifyItemInserted(deviceList.size() - 1);

        Toast.makeText(this, "Thêm thiết bị thành công", Toast.LENGTH_SHORT).show();

        resetForm();
    }

    private void resetForm() {
        inputDeviceName.setText("");
        inputDeviceDescription.setText("");
        imgPreview.setImageResource(0);
        lastPickedUri = null;
        lastPickedPath = null;
        editingDevice = null;
    }

    // Lưu ảnh vào internal storage và trả về path tuyệt đối
    private String savePickedImage(Uri uri) {
        try {
            File dir = new File(getFilesDir(), "images");
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID().toString() + ".jpg";
            File file = new File(dir, fileName);

            InputStream is = getContentResolver().openInputStream(uri);
            OutputStream os = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            is.close();
            os.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
