package Data;

import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Group implements Serializable,Identifiable {
    private String id;
    private String name;
    private List<String> idUsers;

    public Group(String id, String name, List<String> idUsers) {
        this.id = id;
        this.name = name;
        this.idUsers = idUsers;
    }

    public Group(String name, List<String> idUsers) {
        this.name = name;
        this.idUsers = idUsers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(List<String> idUsers) {
        this.idUsers = idUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
