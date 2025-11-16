package Ui.thongbao;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlytrucnhatvamuontrathietbi.R;

import java.util.ArrayList;
import java.util.List;

import Data.ThongBao;

public class ThongBaoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ThongBaoAdapter adapter;
    List<ThongBao> dsThongBao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_bao);

        recyclerView = findViewById(R.id.rcvThongBao);

        dsThongBao = new ArrayList<>();

        // Tạo dữ liệu mẫu
        dsThongBao.add(new ThongBao("Yêu cầu mượn thiết bị", "Bạn đã mượn thành công máy chiếu", "Hôm nay, 09:15", false));
        dsThongBao.add(new ThongBao("Phê duyệt đơn trực nhật", "Đơn của bạn đã được duyệt", "Hôm qua", true));

        adapter = new ThongBaoAdapter(dsThongBao);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
