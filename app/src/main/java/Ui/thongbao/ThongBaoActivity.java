package Ui.thongbao;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlytrucnhatvamuontrathietbi.R;

import java.util.ArrayList;
import java.util.List;

import Data.DataUtil;
import Data.Notification;
import Data.ThongBao;

public class ThongBaoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ThongBaoAdapter adapter;
    private List<ThongBao> dsThongBao;
    private DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_bao);

        dataUtil = DataUtil.getInstance(getApplicationContext());

        recyclerView = findViewById(R.id.rcvThongBao);
        dsThongBao = new ArrayList<>();

        loadNotificationsFromData();

        adapter = new ThongBaoAdapter(dsThongBao);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi lần quay lại màn hình thì load lại cho chắc
        loadNotificationsFromData();
        adapter.notifyDataSetChanged();
    }

    private void loadNotificationsFromData() {
        dsThongBao.clear();

        // Lấy toàn bộ Notification trong DataUtil
        for (Notification n : dataUtil.notifications.getAll()) {
            dsThongBao.add(new ThongBao(
                    n.getTitle(),          // tiêu đề
                    n.getContent(),        // nội dung
                    n.getTimeAgo(),        // chuỗi thời gian tương đối: "5 phút trước" ...
                    false                  // đã đọc hay chưa, tạm để false
            ));
        }
    }
}
