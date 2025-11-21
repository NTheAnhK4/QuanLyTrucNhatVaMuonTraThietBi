package com.example.quanlytrucnhatvamuontrathietbi;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

    private Button btnPickImage, btnAddDevice;
    private ImageView imgPreview;
    private EditText inputDeviceName, inputDeviceDescription;
    private Spinner spinnerDeviceStatus;

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
        setupSpinner();

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
        spinnerDeviceStatus = findViewById(R.id.spinnerDeviceStatus);

        deviceRecycler = findViewById(R.id.deviceRecycler);
    }

    private void setupSpinner() {
        ArrayAdapter<EquipmentStatus> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                EquipmentStatus.values()
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeviceStatus.setAdapter(statusAdapter);
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

        // Click vào item -> hiển thị lên form
        adapter.setItemClickListener(e -> {
            inputDeviceName.setText(e.getName());
            inputDeviceDescription.setText(e.getDescription());
            imgPreview.setImageURI(Uri.fromFile(new File(e.getImageUri())));
            spinnerDeviceStatus.setSelection(e.getStatus().ordinal());

            editingDevice = e;
            lastPickedPath = e.getImageUri();
            lastPickedUri = Uri.fromFile(new File(e.getImageUri()));
        });

        // Listener nút Edit/Delete
        adapter.setListener(new DeviceRecyclerAdapter.ItemActionListener() {
            @Override
            public void onEdit(Equipment e) {
                if (editingDevice == null || !editingDevice.equals(e)) {
                    inputDeviceName.setText(e.getName());
                    inputDeviceDescription.setText(e.getDescription());
                    imgPreview.setImageURI(Uri.fromFile(new File(e.getImageUri())));
                    spinnerDeviceStatus.setSelection(e.getStatus().ordinal());

                    editingDevice = e;
                    lastPickedPath = e.getImageUri();
                    lastPickedUri = Uri.fromFile(new File(e.getImageUri()));
                } else {
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
                updateDevice();
            } else {
                addNewDevice();
            }
        });
    }

    private void updateDevice() {
        String name = inputDeviceName.getText().toString().trim();
        String desc = inputDeviceDescription.getText().toString().trim();
        EquipmentStatus selectedStatus = (EquipmentStatus) spinnerDeviceStatus.getSelectedItem();

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

        editingDevice.setName(name);
        editingDevice.setDescription(desc);
        editingDevice.setImageUri(lastPickedPath);
        editingDevice.setStatus(selectedStatus);

        DataUtil.getInstance(this).equipments.update(editingDevice);

        int index = deviceList.indexOf(editingDevice);
        adapter.notifyItemChanged(index);

        Toast.makeText(this, "Cập nhật thiết bị thành công", Toast.LENGTH_SHORT).show();

        resetForm();
    }

    private void addNewDevice() {
        String name = inputDeviceName.getText().toString().trim();
        String desc = inputDeviceDescription.getText().toString().trim();
        EquipmentStatus selectedStatus = (EquipmentStatus) spinnerDeviceStatus.getSelectedItem();

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
        Equipment newDevice = new Equipment(id, name, desc, lastPickedPath, selectedStatus);

        DataUtil.getInstance(this).equipments.add(newDevice);
        adapter.notifyItemInserted(deviceList.size() - 1);

        Toast.makeText(this, "Thêm thiết bị thành công", Toast.LENGTH_SHORT).show();

        resetForm();
    }

    private void resetForm() {
        inputDeviceName.setText("");
        inputDeviceDescription.setText("");
        spinnerDeviceStatus.setSelection(0); // Reset về Available
        imgPreview.setImageResource(0);
        lastPickedUri = null;
        lastPickedPath = null;
        editingDevice = null;
    }

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
