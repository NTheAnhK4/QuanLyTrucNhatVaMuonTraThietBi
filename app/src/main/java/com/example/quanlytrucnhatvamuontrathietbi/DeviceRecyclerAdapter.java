package com.example.quanlytrucnhatvamuontrathietbi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import Data.Equipment;

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceRecyclerAdapter.DeviceViewHolder> {

    private ArrayList<Equipment> list;
    private Context context;

    // Listener cho nút Edit/Delete
    public interface ItemActionListener {
        void onEdit(Equipment e);
        void onDelete(Equipment e);
    }

    private ItemActionListener actionListener;

    public void setListener(ItemActionListener listener) {
        this.actionListener = listener;
    }

    // Listener cho click vào item (hiển thị lên form)
    public interface ItemClickListener {
        void onItemClick(Equipment e);
    }

    private ItemClickListener clickListener;

    public void setItemClickListener(ItemClickListener listener) {
        this.clickListener = listener;
    }

    public DeviceRecyclerAdapter(Context context, ArrayList<Equipment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.device_item_listview, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Equipment device = list.get(position);

        holder.itemName.setText(device.getName());
        holder.itemDescription.setText(device.getDescription());

        // Hiển thị tình trạng
        if (device.getStatus() != null) {
            holder.itemStatus.setText("Tình trạng: " + device.getStatus().name());
        } else {
            holder.itemStatus.setText("Tình trạng: Không xác định");
        }

        // Load ảnh từ path tuyệt đối
        String path = device.getImageUri();
        if (path != null && !path.isEmpty()) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.itemImage.setImageBitmap(bitmap);
            } else {
                holder.itemImage.setImageResource(R.drawable.ic_placeholder);
            }
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_placeholder);
        }

        // Nút Edit
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null)
                actionListener.onEdit(device);
        });

        // Nút Delete
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null)
                actionListener.onDelete(device);
        });

        // Click vào toàn bộ item để hiển thị lên form
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null)
                clickListener.onItemClick(device);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemStatus, itemDescription;
        Button btnEdit, btnDelete;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemStatus = itemView.findViewById(R.id.itemStatus); // Mới thêm
            itemDescription = itemView.findViewById(R.id.itemDescription);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
