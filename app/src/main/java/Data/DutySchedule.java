package Data;

import java.io.Serializable;
import java.util.Objects;

public class DutySchedule implements Serializable, Identifiable {
    private String id;
    private String day;
    private String className;
    private DutySchedulesStatus status;
    private String groupID;

    public DutySchedule(String id,String day, String className, DutySchedulesStatus status, String groupID) {
        this.id = id;
        this.day = day;
        this.className = className;
        this.status = status;
        this.groupID = groupID;
    }

    public DutySchedule(String day, String className, DutySchedulesStatus status, String groupID) {
        this.day = day;
        this.className = className;
        this.status = status;
        this.groupID = groupID;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public DutySchedulesStatus getStatus() {
        return status;
    }

    public void setStatus(DutySchedulesStatus status) {
        this.status = status;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
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
        DutySchedule that = (DutySchedule) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
