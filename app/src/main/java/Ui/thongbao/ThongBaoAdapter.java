package Ui.thongbao;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlytrucnhatvamuontrathietbi.R;

import java.util.List;

import Data.ThongBao;

public class ThongBaoAdapter extends RecyclerView.Adapter<ThongBaoAdapter.ViewHolder> {

    private List<ThongBao> list;

    public ThongBaoAdapter(List<ThongBao> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thong_bao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThongBao tb = list.get(position);

        holder.txtTieuDe.setText(tb.getTieuDe());
        holder.txtNoiDung.setText(tb.getNoiDung());
        holder.txtThoiGian.setText(tb.getThoiGian());

        if (tb.isDaDoc()) {
            holder.root.setBackgroundResource(R.drawable.bg_read);
        } else {
            holder.root.setBackgroundResource(R.drawable.bg_unread);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        TextView txtTieuDe, txtNoiDung, txtThoiGian;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.itemRoot);
            txtTieuDe = itemView.findViewById(R.id.txtTieuDe);
            txtNoiDung = itemView.findViewById(R.id.txtNoiDung);
            txtThoiGian = itemView.findViewById(R.id.txtThoiGian);
        }
    }
}
