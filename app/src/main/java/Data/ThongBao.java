package Data;

public class ThongBao {
    private String tieuDe;
    private String noiDung;
    private String thoiGian;
    private boolean daDoc;

    public ThongBao(String tieuDe, String noiDung, String thoiGian, boolean daDoc) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.thoiGian = thoiGian;
        this.daDoc = daDoc;
    }

    public String getTieuDe() { return tieuDe; }
    public String getNoiDung() { return noiDung; }
    public String getThoiGian() { return thoiGian; }
    public boolean isDaDoc() { return daDoc; }
}
