package Data;

import java.io.Serializable;
import java.util.List; // QUAN TRỌNG: Cần import List
import java.util.Objects;

public class DutySchedule implements Serializable, Identifiable {
    private String id;
    private String day;
    private String shiftTime;   // Ca trực (ví dụ: "Ca 1 (7h - 9h)")
    private String dutyType;    // Loại trực nhật (ví dụ: "Phòng lý thuyết")
    private String className;   // Tên phòng học (ví dụ: "A1 - 101")
    private String creatorId;   // ID người tạo lịch
    private DutySchedulesStatus status;

    // THAY ĐỔI CƠ BẢN: Thay thế groupID bằng List<String> assigneeIds
    private List<String> assigneeIds; // DANH SÁCH ID CỦA SINH VIÊN (USERS) ĐƯỢC PHÂN CÔNG

    // CONSTRUCTOR MỚI
    public DutySchedule(String day, String shiftTime, String dutyType, String className, String creatorId, DutySchedulesStatus status, List<String> assigneeIds) {
        this.day = day;
        this.shiftTime = shiftTime;
        this.dutyType = dutyType;
        this.className = className;
        this.creatorId = creatorId;
        this.status = status;
        this.assigneeIds = assigneeIds;
    }

    // CONSTRUCTOR ĐẦY ĐỦ ID
    public DutySchedule(String id, String day, String shiftTime, String dutyType, String className, String creatorId, DutySchedulesStatus status, List<String> assigneeIds) {
        this(day, shiftTime, dutyType, className, creatorId, status, assigneeIds);
        this.id = id;
    }

    // --- GETTERS AND SETTERS ---

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getShiftTime() {
        return shiftTime;
    }

    public void setShiftTime(String shiftTime) {
        this.shiftTime = shiftTime;
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

    // GETTER/SETTER MỚI CHO DANH SÁCH SINH VIÊN
    public List<String> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(List<String> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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