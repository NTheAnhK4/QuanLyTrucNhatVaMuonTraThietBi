package com.example.quanlytrucnhatvamuontrathietbi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Data.BorrowRequest;
import Data.BorrowRequestStatus;
import Data.DataUtil;

public class HistoryRequestActivityv extends AppCompatActivity {

    private RecyclerView recyclerRequests;
    private List<BorrowRequest> requestList;
    private HistoryRequestAdapter adapter;
    private LinearLayout emptyStateView;
    private TextView hisStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo R.layout.history_request là tên file XML chính của bạn
        setContentView(R.layout.history_request);

        // 1. Khởi tạo Views
        recyclerRequests = findViewById(R.id.recycler_requests);
        emptyStateView = findViewById(R.id.empty_state_view_history);
        Button btnBack = findViewById(R.id.btnBackHistory);

        // 2. Thiết lập nút Back (dùng finish() để quay lại BorrowRequestActivity)
        btnBack.setOnClickListener(v -> finish());

        // 3. Khởi tạo RecyclerView và Adapter
        recyclerRequests.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();
        adapter = new HistoryRequestAdapter(requestList, this);
        recyclerRequests.setAdapter(adapter);

        // 4. Tải dữ liệu lần đầu
        loadHistoryRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistoryRequests();
    }

    private void loadHistoryRequests() {
        DataUtil dataUtil = DataUtil.getInstance(this);
        List<BorrowRequest> allRequests = dataUtil.borrowRequests.getAll();

        if (allRequests != null) {
            // Lọc: Chỉ lấy các yêu cầu ĐÃ XỬ LÝ và KHÔNG PHẢI là Rejected (chỉ Approved)
            List<BorrowRequest> historyRequests = allRequests.stream()

                    // ⭐️ SỬA ĐỔI ĐIỀU KIỆN LỌC Ở ĐÂY ⭐️
                    .filter(request -> request.getStatus() == BorrowRequestStatus.Approved)

                    // Hoặc bạn có thể dùng cách loại trừ (nhưng cách trên rõ ràng hơn cho mục đích "Chỉ hiển thị Approved")
                    // .filter(request -> request.getStatus() != BorrowRequestStatus.Pending && request.getStatus() != BorrowRequestStatus.Rejected)

                    .collect(Collectors.toList());

            this.requestList = historyRequests;
            if (adapter != null) {
                adapter.updateData(this.requestList);
            }

        } else {
            this.requestList = new ArrayList<>();
            if (adapter != null) {
                adapter.updateData(this.requestList);
            }
        }

        // Kiểm tra Empty State sau khi dữ liệu được tải
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

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }

    // --- INNER CLASS: HistoryRequestAdapter ---

    private class HistoryRequestAdapter extends RecyclerView.Adapter<HistoryRequestAdapter.HistoryRequestViewHolder> {

        private List<BorrowRequest> requests;
        private final Context context;

        public HistoryRequestAdapter(List<BorrowRequest> requests, Context context) {
            this.requests = requests;
            this.context = context;
        }

        public void updateData(List<BorrowRequest> newRequests) {
            this.requests = newRequests;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public HistoryRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // SỬ DỤNG LAYOUT history_item_request
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_item_request, parent, false);
            return new HistoryRequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryRequestViewHolder holder, int position) {
            BorrowRequest request = requests.get(position);

            // Đánh số thứ tự
            int requestNumber = position + 1;

            BorrowRequestStatus status = request.getStatus();
            String displayStatus = getVietnameseStatus(status);


            if (status == BorrowRequestStatus.Approved) {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_approved);

            }
            // Hiển thị thông tin
            holder.tvRequestId.setText("Yêu cầu mượn thiết bị: " + request.getIdEquipment());
            holder.tvUserInfo.setText("Mã SV: " + request.getIdUser() );
            holder.tvDetails.setText("Ngày: " + request.getBorrowDay() + " | Từ: " + request.getStartBorrowDay() + "H - Đến: " + request.getEndBorrowDay() + "H");
            holder.tvStatus.setText(displayStatus);
            if (holder.btnAction != null) {
                holder.btnAction.setVisibility(View.GONE);
            }
        }

        private String getVietnameseStatus(BorrowRequestStatus status) {
            switch (status) {
                case Approved:
                    return "Đã Duyệt";
                default:
                    return status.toString();
            }
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        class HistoryRequestViewHolder extends RecyclerView.ViewHolder {
            TextView tvRequestId, tvUserInfo, tvDetails, tvStatus;
            Button btnAction;
            public HistoryRequestViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRequestId = itemView.findViewById(R.id.his_request_id);
                tvUserInfo = itemView.findViewById(R.id.his_user_info);
                tvDetails = itemView.findViewById(R.id.his_details);
                tvStatus = itemView.findViewById(R.id.his_status);
                btnAction = itemView.findViewById(R.id.btn_approve_his);
            }
        }
    }
}