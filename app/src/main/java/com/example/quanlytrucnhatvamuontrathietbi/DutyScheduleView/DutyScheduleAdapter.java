package com.example.quanlytrucnhatvamuontrathietbi.DutyScheduleView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlytrucnhatvamuontrathietbi.R;

import Data.DataUtil;
import Data.DutySchedule;
import Data.DutySchedulesStatus;
import Data.User;

import java.util.ArrayList;
import java.util.List;

public class DutyScheduleAdapter extends RecyclerView.Adapter<DutyScheduleAdapter.ViewHolder> {

    private List<DutySchedule> dutySchedules;
    private final Context context;
    private final DataUtil dataUtil;

    public DutyScheduleAdapter(Context context, List<DutySchedule> dutySchedules, DataUtil dataUtil) {
        this.context = context;
        this.dutySchedules = dutySchedules;
        this.dataUtil = dataUtil;
    }

    public void updateList(List<DutySchedule> newList) {
        this.dutySchedules = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_duty_schedule_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DutySchedule schedule = dutySchedules.get(position);

        holder.tvRoom.setText("Phòng: " + schedule.getClassName());
        holder.tvDate.setText("Ngày: " + schedule.getDay());

        // GÁN DỮ LIỆU MỚI: Loại trực nhật
        holder.tvDutyType.setText("Loại: " + schedule.getDutyType());

        // Hiển thị Tên sinh viên
        holder.tvAssigneeName.setText(resolveAssigneeNames(schedule.getAssigneeIds()));

        // Xử lý Trạng thái và nút Hoàn thành (Giữ nguyên logic cũ)
        DutySchedulesStatus status = schedule.getStatus();
        holder.tvStatusBadge.setText(status == DutySchedulesStatus.Completed ? "Đã hoàn thành" : "Chờ thực hiện");

        if (status == DutySchedulesStatus.Completed) {
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_completed);
            holder.btnCompleteDuty.setVisibility(View.GONE);
        } else {
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_pending);
            holder.btnCompleteDuty.setVisibility(View.VISIBLE);
        }

        // Xử lý sự kiện CLICK ITEM
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewScheduleActivity.class);
            intent.putExtra("SCHEDULE_ID", schedule.getId());
            context.startActivity(intent);
        });

        // Xử lý sự kiện CLICK NÚT HOÀN THÀNH
        holder.btnCompleteDuty.setOnClickListener(v -> {
            schedule.setStatus(DutySchedulesStatus.Completed);
            dataUtil.dutySchedules.update(schedule);
            // Cần cast context sang Activity để gọi loadSchedules nếu cần sắp xếp lại
            ((DutyScheduleListActivity) context).loadSchedules();
        });
    }

    @Override
    public int getItemCount() {
        return dutySchedules.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // KHAI BÁO BIẾN MỚI
        TextView tvRoom, tvDate, tvStatusBadge, tvAssigneeName, tvDutyType;
        Button btnCompleteDuty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoom = itemView.findViewById(R.id.tv_room);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvAssigneeName = itemView.findViewById(R.id.tv_assignee_name);
            tvDutyType = itemView.findViewById(R.id.tv_duty_type); // ÁNH XẠ BIẾN MỚI
            btnCompleteDuty = itemView.findViewById(R.id.btn_complete_duty);
        }
    }

    /**
     * Hàm trợ giúp tìm tên sinh viên dựa trên danh sách ID.
     * @param assigneeIds Danh sách ID sinh viên.
     * @return Chuỗi tên sinh viên được phân cách.
     */
    private String resolveAssigneeNames(List<String> assigneeIds) {
        if (assigneeIds == null || assigneeIds.isEmpty()) return "Chưa phân công";

        List<String> names = new ArrayList<>();

        for (String userId : assigneeIds) {
            // Chỉ kiểm tra User (loại bỏ Group)
            for (User user : dataUtil.users.getAll()) {
                if (user.getId().equals(userId)) {
                    names.add(user.getName());
                    break;
                }
            }
        }

        if (names.isEmpty()) return "Không xác định";

        // Format hiển thị:
        if (names.size() == 1) {
            return names.get(0);
        } else if (names.size() <= 3) {
            return String.join(", ", names);
        } else {
            return names.get(0) + ", " + names.get(1) + " và " + (names.size() - 2) + " người khác";
        }
    }
}