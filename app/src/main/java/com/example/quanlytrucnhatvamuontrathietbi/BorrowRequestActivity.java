package com.example.quanlytrucnhatvamuontrathietbi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Data.BorrowRequest;
import Data.BorrowRequestStatus;
import Data.DataUtil;
import Data.Equipment;

public class BorrowRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerRequests;
    private List<BorrowRequest> requestList;
    private BorrowRequestAdapter adapter;
    private LinearLayout emptyStateView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo R.layout.borrow_request là tên file XML chính của bạn
        setContentView(R.layout.borrow_request);

        recyclerRequests = findViewById(R.id.recycler_requests);
        recyclerRequests.setLayoutManager(new LinearLayoutManager(this));
        emptyStateView = findViewById(R.id.empty_state_view);

        // 1. Khởi tạo và thiết lập Adapter với danh sách rỗng
        requestList = new ArrayList<>();
        adapter = new BorrowRequestAdapter(requestList, this);
        recyclerRequests.setAdapter(adapter);

        // 2. Thiết lập nút Back
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 3. Thiết lập Icon Con Mắt (Sử dụng ID đã thêm vào XML Header)
        ImageView iconEye = findViewById(R.id.iconEye);
        if (iconEye != null) {
            iconEye.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFilterDialog();
                }
            });
        }

        // 4. Tải dữ liệu ban đầu
        loadPendingRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu mỗi khi Activity được resume
        loadPendingRequests();
    }

    public void loadPendingRequests() {
        DataUtil dataUtil = DataUtil.getInstance(this);
        List<BorrowRequest> allRequests = dataUtil.borrowRequests.getAll();

        if (allRequests != null) {
            // Lọc: Chỉ lấy các yêu cầu đang chờ duyệt (Pending)
            this.requestList = allRequests.stream()
                    .filter(request -> request.getStatus() == BorrowRequestStatus.Pending)
                    .collect(Collectors.toList());
        } else {
            this.requestList = new ArrayList<>();
        }

        // Cập nhật Adapter (Nếu Adapter đã được khởi tạo)
        if (adapter != null) {
            adapter.updateData(this.requestList);
        }
        checkIfEmpty();
    }
    private void checkIfEmpty() {
        if (requestList.isEmpty()) {
            recyclerRequests.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            recyclerRequests.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }
    private void showFilterDialog() {
        // Tạo Builder cho AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Lấy layout dialog_request_filter.xml
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_request_filter, null);
        builder.setView(dialogView);

        // ⭐️ Khắc phục lỗi lặp: Chỉ tạo một đối tượng dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Tìm các button trong dialog view (Đã có sẵn)
        Button btnComplete = dialogView.findViewById(R.id.btnComplete);
        Button btnFailure = dialogView.findViewById(R.id.btnFailure);
        ImageView btnClose = dialogView.findViewById(R.id.btnCloseDialog);


        // Xử lý nút Đóng
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Xử lý nút Đã duyệt (Chuyển sang màn hình Lịch sử - Đã có sẵn)
        btnComplete.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(BorrowRequestActivity.this, HistoryRequestActivityv.class);
            startActivity(intent);
        });

        // Xử lý nút Thất bại (Chuyển sang màn hình Lịch sử khác - Đã có sẵn)
        btnFailure.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(BorrowRequestActivity.this, HistoryRequestActivityx.class);
            startActivity(intent);
        });

    }

    // --- INNER CLASS: BorrowRequestAdapter (Đã sửa lỗi currentRequestNumber và btn_approve) ---

    private class BorrowRequestAdapter extends RecyclerView.Adapter<BorrowRequestAdapter.RequestViewHolder> {

        private List<BorrowRequest> requests;
        private final Context context;

        public BorrowRequestAdapter(List<BorrowRequest> requests, Context context) {
            this.requests = requests;
            this.context = context;
        }

        public void updateData(List<BorrowRequest> newRequests) {
            this.requests = newRequests;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_request, parent, false);
            return new RequestViewHolder(view);
        }
// Trong BorrowRequestActivity.java
// ...
        @Override
        public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
            BorrowRequest request = requests.get(position);

            BorrowRequestStatus status = request.getStatus();
            String displayStatus;

            // Xử lý trạng thái Tiếng Việt
            if (status == BorrowRequestStatus.Pending) {
                displayStatus = "Chờ Duyệt";
            }  else {
                displayStatus = status.toString();
            }

            holder.tvRequestId.setText("Yêu cầu mượn thiết bị: " + request.getIdEquipment());
            holder.tvUserInfo.setText("Mã SV: " + request.getIdUser() );
            holder.tvDetails.setText("Ngày: " + request.getBorrowDay() + " | Từ: " + request.getStartBorrowDay() + "H - Đến: " + request.getEndBorrowDay() + "H");
            holder.tvStatus.setText(displayStatus);

            // ⭐️ LOGIC XỬ LÝ NÚT DUYỆT ⭐️
            holder.btnApprove.setOnClickListener(v -> {
                // 1. Cập nhật trạng thái trong dữ liệu gốc
                request.setStatus(BorrowRequestStatus.Approved);
                DataUtil.getInstance(context).borrowRequests.update(request);

                // 2. Xóa yêu cầu khỏi danh sách hiển thị
                requests.remove(position);

                // 3. Thông báo cho Adapter biết dữ liệu đã thay đổi
                notifyItemRemoved(position);

                // 4. HIỂN THỊ THÔNG BÁO DUYỆT THÀNH CÔNG 🎉
                Toast.makeText(context, "Đã duyệt yêu cầu " + request.getId() + " thành công!",
                        Toast.LENGTH_SHORT).show();

                // Cần đảm bảo list trong activity được cập nhật sau khi xóa
                if (requests.isEmpty()) {
                    ((BorrowRequestActivity) context).loadPendingRequests();
                }
            });

            // Bạn có thể làm tương tự cho nút Từ chối (btnReject)
            holder.btnReject.setOnClickListener(v -> {
                // 1. Cập nhật trạng thái trong dữ liệu gốc
                request.setStatus(BorrowRequestStatus.Rejected);
                DataUtil.getInstance(context).borrowRequests.update(request);

                // 2. Xóa yêu cầu khỏi danh sách hiển thị
                requests.remove(position);

                // 3. Thông báo cho Adapter biết dữ liệu đã thay đổi và cập nhật giao diện
                // ⚠️ PHẢI GỌI notifyItemRemoved để xóa item khỏi RecyclerView
                notifyItemRemoved(position);

                // 4. HIỂN THỊ THÔNG BÁO TỪ CHỐI THÀNH CÔNG ❌
                Toast.makeText(context, "Đã từ chối yêu cầu " + request.getId() + " thành công.",
                        Toast.LENGTH_SHORT).show();

                // Kiểm tra và cập nhật Empty State nếu cần (giống logic của nút Duyệt)
                if (requests.isEmpty()) {
                    ((BorrowRequestActivity) context).loadPendingRequests();
                }
            });
        }

        // ...
        @Override
        public int getItemCount() {
            return requests.size();
        }

        class RequestViewHolder extends RecyclerView.ViewHolder {
            TextView tvRequestId, tvUserInfo, tvDetails, tvStatus;
            Button btnApprove, btnReject;

            public RequestViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRequestId = itemView.findViewById(R.id.tv_request_id);
                tvUserInfo = itemView.findViewById(R.id.tv_user_info);
                tvDetails = itemView.findViewById(R.id.tv_details);
                tvStatus = itemView.findViewById(R.id.tv_status);
                // ⭐️ KHỞI TẠO BUTTONS ⭐️
                btnApprove = itemView.findViewById(R.id.btn_approve);
                btnReject = itemView.findViewById(R.id.btn_reject);
            }
        }
    }
}