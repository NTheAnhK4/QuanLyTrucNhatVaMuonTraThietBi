package com.example.quanlytrucnhatvamuontrathietbi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Data.DataUtil;
import Data.Notification;
import Data.BorrowRequest;
import Data.BorrowRequestStatus;

public class BorrowRequestActivity extends AppCompatActivity {
    private DataUtil dataUtil;
    private RecyclerView recyclerRequests;
    private List<BorrowRequest> requestList;
    private BorrowRequestAdapter adapter;

    Button btnTroVe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrow_request);
        dataUtil = DataUtil.getInstance(getApplicationContext());
        recyclerRequests = findViewById(R.id.recycler_requests);
        recyclerRequests.setLayoutManager(new LinearLayoutManager(this));
        Button btnBack = findViewById(R.id.btnBack);

        // 2. Thiết lập sự kiện click cho nút
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Phương thức finish() sẽ đóng Activity hiện tại
                finish();
            }
        });
        // Dữ liệu giả lập
        requestList = new ArrayList<>();
        requestList.add(new BorrowRequest("REQ001", "user01", "EQ001", "20/09/2025", 13, 16));
        requestList.add(new BorrowRequest("REQ002", "user02", "EQ002", "21/09/2025", 9, 12));

        adapter = new BorrowRequestAdapter(requestList);
        recyclerRequests.setAdapter(adapter);
    }

    private class BorrowRequestAdapter extends RecyclerView.Adapter<BorrowRequestAdapter.RequestViewHolder> {

        private final List<BorrowRequest> requests;

        public BorrowRequestAdapter(List<BorrowRequest> requests) {
            this.requests = requests;
        }

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_request, parent, false);
            return new RequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
            BorrowRequest request = requests.get(position);
            holder.tvRequestId.setText("Yêu cầu: " + request.getId());
            holder.tvUserInfo.setText("User: " + request.getIdUser() + " - Thiết bị: " + request.getIdEquipment());
            holder.tvDetails.setText("Ngày: " + request.getBorrowDay() + " | Từ: " + request.getStartBorrowDay() + "H - Đến: " + request.getEndBorrowDay() + "H");

            holder.btnApprove.setOnClickListener(v -> {
                request.setStatus(BorrowRequestStatus.Approved);
                dataUtil.borrowRequests.update(request);

                String title = "Yêu cầu mượn thiết bị đã được duyệt";
                String content =
                        "Mã yêu cầu: " + request.getId() +
                                "\nMã người dùng: " + request.getIdUser() +
                                "\nMã thiết bị: " + request.getIdEquipment() +
                                "\nNgày mượn: " + request.getBorrowDay() +
                                "\nThời gian: " + request.getStartBorrowDay() + " - " + request.getEndBorrowDay();

                Notification noti = new Notification(null, title, content);
                noti.setApproved(true);
                dataUtil.notifications.add(noti);

                notifyItemChanged(position);
            });

            holder.btnReject.setOnClickListener(v -> {
                request.setStatus(BorrowRequestStatus.Rejected);
                dataUtil.borrowRequests.update(request);

                String title = "Yêu cầu mượn thiết bị không được duyệt";
                String content =
                        "Mã yêu cầu: " + request.getId() +
                                "\nMã người dùng: " + request.getIdUser() +
                                "\nMã thiết bị: " + request.getIdEquipment() +
                                "\nNgày mượn: " + request.getBorrowDay() +
                                "\nThời gian: " + request.getStartBorrowDay() + " - " + request.getEndBorrowDay();

                Notification noti = new Notification(null, title, content);
                noti.setApproved(false);
                dataUtil.notifications.add(noti);


                notifyItemChanged(position);
            });

        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        class RequestViewHolder extends RecyclerView.ViewHolder {
            TextView tvRequestId, tvUserInfo, tvDetails;
            Button btnApprove, btnReject;

            public RequestViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRequestId = itemView.findViewById(R.id.tv_request_id);
                tvUserInfo = itemView.findViewById(R.id.tv_user_info);
                tvDetails = itemView.findViewById(R.id.tv_details);
                btnApprove = itemView.findViewById(R.id.btn_approve);
                btnReject = itemView.findViewById(R.id.btn_reject);
            }
        }
    }}