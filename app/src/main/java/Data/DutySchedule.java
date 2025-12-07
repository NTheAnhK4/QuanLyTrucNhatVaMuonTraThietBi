package Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class DutySchedule implements Serializable, Identifiable {
    private String id;
    private String day;

    // THAY THẾ shiftTime CŨ
    private String startShift;  // Ca bắt đầu (ví dụ: "Ca 1")
    private String endShift;    // Ca kết thúc (ví dụ: "Ca 4")

    private String dutyType;    // Loại trực nhật (ví dụ: "Phòng lý thuyết")
    private String className;   // Tên phòng học (ví dụ: "A1 - 101")
    private String creatorId;   // ID người tạo lịch
    private DutySchedulesStatus status;

    private List<String> assigneeIds; // DANH SÁCH ID CỦA SINH VIÊN (USERS) ĐƯỢC PHÂN CÔNG

    // CONSTRUCTOR MỚI
    public DutySchedule(String day, String startShift, String endShift, String dutyType, String className, String creatorId, DutySchedulesStatus status, List<String> assigneeIds) {
        this.day = day;
        this.startShift = startShift;
        this.endShift = endShift;
        this.dutyType = dutyType;
        this.className = className;
        this.creatorId = creatorId;
        this.status = status;
        this.assigneeIds = assigneeIds;
    }

    // CONSTRUCTOR ĐẦY ĐỦ ID
    public DutySchedule(String id, String day, String startShift, String endShift, String dutyType, String className, String creatorId, DutySchedulesStatus status, List<String> assigneeIds) {
        this(day, startShift, endShift, dutyType, className, creatorId, status, assigneeIds);
        this.id = id;
    }

    // --- GETTERS AND SETTERS ---

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
    public String getStartShift() {
        return startShift;
    }

    public void setStartShift(String startShift) {
        this.startShift = startShift;
    }

    public String getEndShift() {
        return endShift;
    }

    public void setEndShift(String endShift) {
        this.endShift = endShift;
    }

    public String getDutyType() {
        return dutyType;
    }

    public void setDutyType(String dutyType) {
        this.dutyType = dutyType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public DutySchedulesStatus getStatus() {
        return status;
    }

    public void setStatus(DutySchedulesStatus status) {
        this.status = status;
    }

    public List<String> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(List<String> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DutySchedule that = (DutySchedule) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}