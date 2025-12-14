package Data;

import java.io.Serializable;
import java.util.Objects;

public class BorrowRequest implements Serializable, Identifiable{
    private String id;
    private String idUser;
    private String idEquipment;
    private String borrowDay;
    private int startBorrowDay;
    private int endBorrowDay;
    private BorrowRequestStatus status;
    private long latestUpdateTime;

    public BorrowRequest(String id, String idUser, String idEquipment, String borrowDay, int startBorrowDay, int endBorrowDay, BorrowRequestStatus status) {
        this.id = id;
        this.idUser = idUser;
        this.idEquipment = idEquipment;
        this.borrowDay = borrowDay;
        this.startBorrowDay = startBorrowDay;
        this.endBorrowDay = endBorrowDay;
        this.status = status; // Gán trạng thái
    }
    public BorrowRequest(String id,String idUser, String idEquipment, String borrowDay, int startBorrowDay, int endBorrowDay) {
        this.idUser = idUser;
        this.idEquipment = idEquipment;
        this.borrowDay = borrowDay;
        this.startBorrowDay = startBorrowDay;
        this.endBorrowDay = endBorrowDay;
        this.id = id;
        status = BorrowRequestStatus.Pending;
    }

    public BorrowRequest(String idUser, String idEquipment, String borrowDay, int startBorrowDay, int endBorrowDay) {
        this.idUser = idUser;
        this.idEquipment = idEquipment;
        this.borrowDay = borrowDay;
        this.startBorrowDay = startBorrowDay;
        this.endBorrowDay = endBorrowDay;
        this.status = BorrowRequestStatus.Pending;
    }

    public long getLatestUpdateTime() {
        return latestUpdateTime;
    }

    public void setLatestUpdateTime(long latestUpdateTime) {
        this.latestUpdateTime = latestUpdateTime;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(String idEquipment) {
        this.idEquipment = idEquipment;
    }

    public String getBorrowDay() {
        return borrowDay;
    }

    public void setBorrowDay(String borrowDay) {
        this.borrowDay = borrowDay;
    }

    public int getStartBorrowDay() {
        return startBorrowDay;
    }

    public void setStartBorrowDay(int startBorrowDay) {
        this.startBorrowDay = startBorrowDay;
    }

    public int getEndBorrowDay() {
        return endBorrowDay;
    }

    public void setEndBorrowDay(int endBorrowDay) {
        this.endBorrowDay = endBorrowDay;
    }

    public BorrowRequestStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowRequestStatus status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BorrowRequest that = (BorrowRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}