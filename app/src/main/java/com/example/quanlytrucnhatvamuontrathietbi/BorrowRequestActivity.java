package com.example.quanlytrucnhatvamuontrathietbi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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

import Data.Notification;
import Data.BorrowRequest;
import Data.BorrowRequestStatus;
import Data.DataUtil;
import Data.Equipment;


public class BorrowRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerRequests;
    private List<BorrowRequest> requestList;
    private BorrowRequestAdapter adapter;
    private LinearLayout emptyStateView;
    private ImageButton btnOverflowMenu;
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


        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
//
        btnOverflowMenu = findViewById(R.id.btnOverflowMenu);


        btnOverflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // 4. Tải dữ liệu ban đầu
        loadPendingRequests();
    }


    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenuInflater().inflate(R.menu.header_menu_request, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem menuItem) {
                return handleMenuItemClick(menuItem);
            }
        });


        popup.show();
    }
    private boolean handleMenuItemClick(android.view.MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.duyet) {
            Intent intent = new Intent(BorrowRequestActivity.this, HistoryRequestActivityv.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.tuchoi) {
            Intent intent = new Intent(BorrowRequestActivity.this, HistoryRequestActivityx.class);
            startActivity(intent);
            return true;
        }
        return false;
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

            holder.btnApprove.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition();

                // Kiểm tra tính hợp lệ của vị trí trước khi thao tác
                if (currentPosition == RecyclerView.NO_POSITION) {
                    return; // Thoát nếu vị trí không hợp lệ
                }

                BorrowRequest currentRequest = requests.get(currentPosition);

                // 1. Cập nhật trạng thái trong dữ liệu gốc
                currentRequest.setStatus(BorrowRequestStatus.Approved);
                DataUtil dataUtil = DataUtil.getInstance(context);
                dataUtil.borrowRequests.update(currentRequest);

                // 1.1. TẠO THÔNG BÁO VÀ LƯU VÀO DATAUTIL.NOTIFICATIONS
                String title = "Yêu cầu mượn đã được DUYỆT";
                String content = "Yêu cầu mượn thiết bị " + currentRequest.getIdEquipment()
                        + " ngày " + currentRequest.getBorrowDay()
                        + " từ " + currentRequest.getStartBorrowDay() + "h đến "
                        + currentRequest.getEndBorrowDay() + "h đã được duyệt.";

                Notification notification = new Notification(title, content);
                notification.setApproved(true);

                dataUtil.notifications.add(notification); // Thêm dòng này

                // 2. Xóa yêu cầu khỏi danh sách hiển thị
                requests.remove(currentPosition);

                // 3. Thông báo cho Adapter biết dữ liệu đã thay đổi
                notifyItemRemoved(currentPosition);

                // 4. HIỂN THỊ THÔNG BÁO DUYỆT THÀNH CÔNG 🎉
                Toast.makeText(context, "Đã duyệt yêu cầu " + currentRequest.getIdEquipment() + " thành công!",
                        Toast.LENGTH_SHORT).show();

                // 5. Cập nhật Empty State (Giữ nguyên logic này)
                if (requests.isEmpty()) {
                    ((BorrowRequestActivity) context).loadPendingRequests();
                }
            });

            holder.btnReject.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition();

                // Kiểm tra tính hợp lệ của vị trí trước khi thao tác
                if (currentPosition == RecyclerView.NO_POSITION) {
                    return; // Thoát nếu vị trí không hợp lệ
                }

                BorrowRequest currentRequest = requests.get(currentPosition);

                // 1. Cập nhật trạng thái trong dữ liệu gốc
                currentRequest.setStatus(BorrowRequestStatus.Rejected);
                DataUtil dataUtil = DataUtil.getInstance(context);
                dataUtil.borrowRequests.update(currentRequest);

                // 1.1. TẠO THÔNG BÁO TỪ CHỐI
                String title = "Yêu cầu mượn đã bị TỪ CHỐI";
                String content = "Yêu cầu mượn thiết bị " + currentRequest.getIdEquipment()
                        + " ngày " + currentRequest.getBorrowDay()
                        + " từ " + currentRequest.getStartBorrowDay() + "h đến "
                        + currentRequest.getEndBorrowDay() + "h đã bị từ chối.";

                Notification notification = new Notification(title, content);
                notification.setApproved(false);
                dataUtil.notifications.add(notification);

                // 2. Xóa yêu cầu khỏi danh sách hiển thị
                requests.remove(currentPosition);

                // 3. Thông báo cho Adapter biết dữ liệu đã thay đổi và cập nhật giao diện
                notifyItemRemoved(currentPosition);

                // 4. HIỂN THỊ THÔNG BÁO TỪ CHỐI THÀNH CÔNG
                Toast.makeText(context, "Đã từ chối yêu cầu " + currentRequest.getIdEquipment() ,
                        Toast.LENGTH_SHORT).show();

                // 5. Kiểm tra và cập nhật Empty State (Giữ nguyên logic này)
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
                btnApprove = itemView.findViewById(R.id.btn_approve);
                btnReject = itemView.findViewById(R.id.btn_reject);
            }
        }
    }
}